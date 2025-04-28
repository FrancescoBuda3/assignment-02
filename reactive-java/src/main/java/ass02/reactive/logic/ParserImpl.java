package ass02.reactive.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

public class ParserImpl implements Parser {
    JavaParser parser;

    public ParserImpl() {
        this.parser = new JavaParser();
    }

    @Override
    public void analyse(Path rootFolder, Consumer<DependencyInfo> sendUpdate) {
        try {
            Files.walk(rootFolder)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(file -> {
                        ParseResult<CompilationUnit> result = null;
                        try {
                            result = this.parser.parse(file);
                        } catch (IOException e) {
                            new RuntimeException();
                        }

                        DependencyCollector collector = new DependencyCollector();
                        if (result.isSuccessful() && result.getResult().isPresent()) {
                            CompilationUnit cu = result.getResult().get();
                            collector.visit(cu, null);
                        }

                        sendUpdate.accept(collector.getInfos());
                    });
        } catch (IOException e) {
            new RuntimeException();
        }
    }
}
