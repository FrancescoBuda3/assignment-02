package ass02.reactive.view;

import ass02.reactive.logic.Parser;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.awt.BorderLayout;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
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

    private int classCounter = 0;
    private Set<String> allDependencies = new HashSet<>();

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
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(selectButton);
        controlPanel.add(folderLabel);
        controlPanel.add(startButton);
        controlPanel.add(this.classCounterLabel);
        controlPanel.add(this.depCounterLabel);
        controlPanel.add(new JScrollPane(this.outputArea));
        frame.add(controlPanel);

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
        frame.setSize(500, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startAnalysis(Path root) {
        outputArea.setText("");
        classCounter = 0;
        allDependencies.clear();

        Flowable.fromIterable(this.parser.analyse(root))
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

                    for (String dep : info.dependencies) {
                        outputArea.append("   ↳ " + dep + "\n");
                    }

                    outputArea.append("\n");
                }));
    }
}
