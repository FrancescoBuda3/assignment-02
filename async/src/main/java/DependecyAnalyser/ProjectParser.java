package DependecyAnalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class ProjectParser {
    AsyncParser parser;
    Vertx vertx;

    public ProjectParser(Vertx vertx){
        this.vertx = vertx;
        this.parser = new AsyncParser();
    }

    public Future<CompilationUnit> parse(String filePath) {
        return this.parser.parse(filePath, vertx);
    }

    public Future<Set<String>> getImportsFromJavaFile(String file) {
        return this.parse(file)
            .compose(ast -> this.vertx
                .executeBlocking(() -> {
                    DependencyCollector collector = new DependencyCollector();
                    collector.visit(ast, null);
                    return collector.getInfos().dependencies.stream().collect(Collectors.toSet());
                }
                )
            );
    }

    public Future<Set<String>> getImportsFromJavaFiles(Set<String> javaFiles) {
        List<Future<Set<String>>> futures = new ArrayList<>();
        for (String file : javaFiles) {
            futures.add(this.getImportsFromJavaFile(file));
        }
        return AsyncUtils.flat(futures);
    }
}
