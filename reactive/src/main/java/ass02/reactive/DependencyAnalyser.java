package ass02.reactive;

import ass02.reactive.logic.ReactiveParser;
import ass02.reactive.logic.ReactiveParserImpl;
import ass02.reactive.view.AnalyserView;

/**
 * Main class for the Dependency Analyser application.
 * It initializes the ReactiveParser and AnalyserView components.
 */
public class DependencyAnalyser {
    public static void main(String[] args) {
        ReactiveParser parser = new ReactiveParserImpl();
        AnalyserView view = new AnalyserView(parser);
        view.show();
    }
}