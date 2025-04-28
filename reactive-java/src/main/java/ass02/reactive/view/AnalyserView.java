package ass02.reactive.view;

import ass02.reactive.logic.Parser;

import io.reactivex.rxjava3.schedulers.Schedulers;

import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.awt.BorderLayout;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class AnalyserView {
    Parser parser;

    private JTextArea outputArea;
    private JLabel classCounterLabel;
    private JLabel depCounterLabel;
    private mxGraph graph;
    private Object parent;

    private int classCounter = 0;
    private Set<String> allDependencies = new HashSet<>();
    private Map<String, Object> nodeMap = new HashMap<>();

    public AnalyserView(Parser parser) {
        this.parser = parser;
    }

    public void show() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Dependency Analyser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JButton selectButton = new JButton("Select Project Root Folder");
        JLabel folderLabel = new JLabel("No Folder Selected");

        JButton startButton = new JButton("Start Analysis");
        startButton.setEnabled(false);

        this.classCounterLabel = new JLabel("Classes/Interfaces: 0");
        this.depCounterLabel = new JLabel("Dependencies found: 0");

        this.outputArea = new JTextArea(15, 40);
        this.outputArea.setEditable(false);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(selectButton);
        controlPanel.add(folderLabel);
        controlPanel.add(startButton);
        controlPanel.add(this.classCounterLabel);
        controlPanel.add(this.depCounterLabel);
        controlPanel.add(new JScrollPane(this.outputArea));

        frame.add(controlPanel, BorderLayout.WEST);

        this.graph = new mxGraph();
        this.parent = this.graph.getDefaultParent();
        mxGraphComponent graphComponent = new mxGraphComponent(this.graph);
        frame.add(graphComponent, BorderLayout.CENTER);

        final Path[] selectedPath = new Path[1];

        selectButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(selectedPath[0] != null ? selectedPath[0].toFile() : null);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                selectedPath[0] = chooser.getSelectedFile().toPath();
                folderLabel.setText(selectedPath[0].toString());
                startButton.setEnabled(true);
            }
        });

        startButton.addActionListener(e -> {
            if (selectedPath[0] != null) {
                startAnalysis(selectedPath[0]);
            }
        });

        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void startAnalysis(Path root) {
        this.graph.getModel().beginUpdate();
        try {
            outputArea.setText("");
            classCounter = 0;
            allDependencies.clear();
            nodeMap.clear();
            mxHierarchicalLayout treeLayout = new mxHierarchicalLayout(this.graph);
            mxParallelEdgeLayout parallelLayout = new mxParallelEdgeLayout(this.graph);

            this.parser.analyse(root)
                    .onBackpressureBuffer(500, () -> {
                        new RuntimeException("Buffer overflow");
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.trampoline())
                    .subscribe(info -> SwingUtilities.invokeLater(() -> {
                        classCounter++;
                        allDependencies.addAll(info.dependencies);

                        classCounterLabel.setText("Classes/Interfaces: " + classCounter);
                        depCounterLabel.setText("Dependencies found: " + allDependencies.size());

                        String fullName = info.packageName + "." + info.className;

                        outputArea.append("📦 " + fullName + " (" + info.classType + ")" + "\n");

                        Object classNode = nodeMap.get(fullName);
                        if (classNode == null) {
                            classNode = graph.insertVertex(this.parent, null, fullName, 0, 0, 120, 40);
                            graph.getModel().setValue(classNode, info.className);
                            nodeMap.put(fullName, classNode);
                        }

                        for (String dep : info.dependencies) {
                            Object depNode = nodeMap.get(dep);
                            if (depNode == null) {
                                depNode = graph.insertVertex(parent, null, dep, 0, 0, 120, 40);
                                graph.getModel().setValue(depNode, dep.substring(dep.lastIndexOf('.') + 1));
                                nodeMap.put(dep, depNode);
                            }

                            graph.insertEdge(parent, null, "", classNode, depNode);

                            outputArea.append("   ↳ " + dep + "\n");
                        }

                        treeLayout.execute(parent);
                        parallelLayout.execute(parent);

                        outputArea.append("\n");
                    }));
        } finally {
            graph.getModel().endUpdate();
        }
    }
}
