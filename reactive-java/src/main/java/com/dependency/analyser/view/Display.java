package com.dependency.analyser.view;

import com.dependency.analyser.logic.Parser;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.*;

public class Display {
    Parser parser;

    private JTextArea outputArea;
    private JLabel classCounterLabel;
    private JLabel depCounterLabel;
    private mxGraph graph;
    private Object parent;

    private int classCounter = 0;
    private Set<String> allDependencies = new HashSet<>();

    public Display(Parser parser) {
        this.parser = parser;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Dependency Analyser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JButton selectButton = new JButton("Select Project Root Folder");
        JLabel folderLabel = new JLabel("No Folder Selected");

        JButton startButton = new JButton("Start Analysis");

        classCounterLabel = new JLabel("Classes/Interfaces: 0");
        depCounterLabel = new JLabel("Dependencies found: 0");

        outputArea = new JTextArea(15, 40);
        outputArea.setEditable(false);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(selectButton);
        controlPanel.add(folderLabel);
        controlPanel.add(startButton);
        controlPanel.add(classCounterLabel);
        controlPanel.add(depCounterLabel);
        controlPanel.add(new JScrollPane(outputArea));

        frame.add(controlPanel, BorderLayout.WEST);

        graph = new mxGraph();
        parent = graph.getDefaultParent();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        frame.add(graphComponent, BorderLayout.CENTER);

        final Path[] selectedPath = new Path[1];

        selectButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                selectedPath[0] = chooser.getSelectedFile().toPath();
                folderLabel.setText(selectedPath[0].toString());
            }
        });

        startButton.addActionListener(e -> {
            if (selectedPath[0] != null) {
                startAnalysis(selectedPath[0]);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void startAnalysis(Path root) {
        graph.getModel().beginUpdate();
        try {
            outputArea.setText("");
            classCounter = 0;
            allDependencies.clear();

            this.parser.analyse(root)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(info -> SwingUtilities.invokeLater(() -> {
                    classCounter++;
                    allDependencies.addAll(info.dependencies);

                    classCounterLabel.setText("Classes/Interfaces: " + classCounter);
                    depCounterLabel.setText("Dependencies found: " + allDependencies.size());

                    String fullName = info.packageName + "." + info.className;
                    outputArea.append("📦 " + fullName + "\n");

                    Object classNode = graph.insertVertex(parent, null, fullName, 0, 0, 120, 40);
                    graph.getModel().setValue(classNode, info.className);

                    for (String dep : info.dependencies) {
                        Object depNode = graph.insertVertex(parent, null, dep, 0, 0, 120, 40);
                        graph.getModel().setValue(depNode, dep.substring(dep.lastIndexOf('.') + 1));

                        graph.insertEdge(parent, null, "", classNode, depNode);
                        outputArea.append("   ↳ " + dep + "\n");
                    }

                    outputArea.append("\n");
                }));
        } finally {
            graph.getModel().endUpdate();
        }
    }
}
