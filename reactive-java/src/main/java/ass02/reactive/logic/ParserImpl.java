package ass02.reactive.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import io.reactivex.rxjava3.core.Flowable;

public class ParserImpl implements Parser {
    JavaParser parser;

    public ParserImpl() {
        this.parser = new JavaParser();
    }

    @Override
    public Flowable<DependencyInfo> analyse(Path rootFolder) {
        return Flowable.fromIterable(() -> {
            try {
                return Files.walk(rootFolder).iterator();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
                .filter(p -> p.toString().endsWith(".java"))
                .map(file -> {
                    ParseResult<CompilationUnit> result = this.parser.parse(file);
                    
                    DependencyCollector collector = new DependencyCollector();
                    if (result.isSuccessful() && result.getResult().isPresent()) {
                        CompilationUnit cu = result.getResult().get();
                        collector.visit(cu, null);
                    }

                    return collector.getInfos();
                });
    }
}
