package com.dependency.analyser;

import javax.swing.SwingUtilities;

import com.dependency.analyser.logic.Parser;
import com.dependency.analyser.view.Display;

public class DependencyAnalyser {
    public static void main(String[] args) {
        Parser parser = new Parser();
        Display display = new Display(parser);
        SwingUtilities.invokeLater(() -> display.createAndShowGUI());
    }
}