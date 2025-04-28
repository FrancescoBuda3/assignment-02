package DependecyAnalyser;

import java.nio.file.Path;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class AsyncParser {
    public Future<CompilationUnit> parse(String filePath, Vertx vertx) {
        return vertx.executeBlocking(() -> {
            StaticJavaParser.getParserConfiguration().setLanguageLevel(LanguageLevel.JAVA_21);
            return StaticJavaParser.parse(Path.of(filePath));
        });
    }
}
