package DependecyAnalyser;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class ParsingUtilities {
    public static Future<Set<String>> getImportsAsStrings(CompilationUnit cu, Vertx vertx) {
        return vertx.executeBlocking(() -> {
            return cu
                .getImports()
                .stream()
                .map(i -> i.getNameAsString())
                .collect(Collectors.toSet());
            }
        );
    }
}
