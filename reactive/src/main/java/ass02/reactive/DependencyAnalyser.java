package ass02.reactive;

import ass02.reactive.logic.Parser;
import ass02.reactive.logic.ParserImpl;
import ass02.reactive.view.AnalyserView;

public class DependencyAnalyser {
    public static void main(String[] args) {
        Parser parser = new ParserImpl();
        AnalyserView view = new AnalyserView(parser);
        view.show();
    }
}