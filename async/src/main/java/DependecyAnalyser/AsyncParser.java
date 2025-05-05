package DependecyAnalyser;

import java.nio.file.Path;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * * AsyncParser is a class that provides asynchronous parsing of Java files using Vertx.
 */
public class AsyncParser {
    /**
     * * Parse a Java file and return the AST as a Future.
     * @param filePath the path to the Java file
     * @param vertx the Vertx instance
     * @return a Future containing the CompilationUnit object
     */
    public Future<CompilationUnit> parse(String filePath, Vertx vertx) {
        return vertx.executeBlocking(() -> {
            StaticJavaParser.getParserConfiguration().setLanguageLevel(LanguageLevel.JAVA_21);
            return StaticJavaParser.parse(Path.of(filePath));
        });
    }
}
