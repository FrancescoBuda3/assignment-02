package DependecyAnalyser;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ImportExtractor {
    public static Set<String> extractImports(Path rootDir) {
        Set<String> imports = new HashSet<>();

        try (Stream<Path> files = Files.walk(rootDir)) {
            files.filter(p -> p.toString().endsWith(".java"))
                .forEach(file -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(file);
                        cu.getImports().forEach(importDecl -> {
                            imports.add(importDecl.getNameAsString());
                        });
                    } catch (IOException e) {
                        System.err.println("Errore nel parsing di: " + file);
                    }
                });
        } catch (IOException e) {
            System.err.println("Errore durante la scansione della directory.");
        }

        return imports;
    }
}
