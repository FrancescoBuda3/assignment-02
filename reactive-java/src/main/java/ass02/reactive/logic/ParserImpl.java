package ass02.reactive.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

public class ParserImpl implements Parser {
    JavaParser parser;

    public ParserImpl() {
        this.parser = new JavaParser();
    }

    @Override
    public List<DependencyInfo> analyse(Path rootFolder) {
        try {
            return Files.walk(rootFolder)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(file -> {
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

                        return collector.getInfos();
                    }).toList();
        } catch (IOException e) {
            new RuntimeException();
            return null;
        }
    }
}
