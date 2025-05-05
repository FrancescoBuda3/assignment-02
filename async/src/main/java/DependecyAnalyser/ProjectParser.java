package DependecyAnalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * ProjectParser is a class that provides asynchronous methods for parsing Java files and collecting dependencies using Vertx.
 */
public class ProjectParser {
    AsyncParser parser;
    Vertx vertx;

    public ProjectParser(Vertx vertx){
        this.vertx = vertx;
        this.parser = new AsyncParser();
    }

    /**
     * Parse a Java file and return the AST.
     * @param filePath the path to the Java file
     * @return a Future containing the CompilationUnit object
     */
    public Future<CompilationUnit> parse(String filePath) {
        return this.parser.parse(filePath, vertx);
    }

    /**
     * Parse a Java file and return the dependencies.
     * @param file the path to the Java file
     * @return a Future containing the set of dependencies
     */
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

    /**
     * Parse a set of Java files and return the dependencies.
     * @param javaFiles the set of Java files
     * @return a Future containing the set of dependencies
     */
    public Future<Set<String>> getImportsFromJavaFiles(Set<String> javaFiles) {
        List<Future<Set<String>>> futures = new ArrayList<>();
        for (String file : javaFiles) {
            futures.add(this.getImportsFromJavaFile(file));
        }
        return AsyncUtils.flat(futures);
    }
}
