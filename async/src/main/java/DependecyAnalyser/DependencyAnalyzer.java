// package DependecyAnalyser;

// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.Set;

// public class DependencyAnalyzer {
//     public static void main(String[] args) {
//         if (args.length < 1) {
//             System.out.println("Usa: java -jar java-deps-analyzer.jar <percorso_progetto>");
//             return;
//         }

//         Path projectPath = Paths.get(args[0]);
//         Set<String> imports = ImportExtractor.extractImports(projectPath);

//         System.out.println("Import trovati:");
//         imports.forEach(System.out::println);
//     }
// }

