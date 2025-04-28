package DependecyAnalyser;

import io.vertx.core.Vertx;

public class TestDependencyAnalyserLib {
    public static void main(String[] args) {
        // Create a Vertx instance
        Vertx vertx1 = Vertx.vertx();
        // Test the DependencyAnalyzerLib class
        DependencyAnalyserLib analyzer = new DependencyAnalyserLib(vertx1);
        
        // Example usage of the getClassDependencies method
        String filePath = "./async/src/main/java/DependecyAnalyser/DependencyAnalyserLib.java";
        
        analyzer.getClassDependencies(filePath).onSuccess(res -> {
            System.out.println(res);
            vertx1.close();
        }).onFailure(err -> {
            System.err.println("Error: " + err.getMessage());
        });

        Vertx vertx2 = Vertx.vertx();
        DependencyAnalyserLib analyzer2 = new DependencyAnalyserLib(vertx2);

        // Example usage of the getPackageDependencies method
        String packagePath = "./async/src/main/java/DependecyAnalyser/reportClasses";

        analyzer2.getPackageDependencies(packagePath).onSuccess(res -> {
            System.out.println(res);
            vertx2.close();
        }).onFailure(err -> {
            System.err.println("Error: " + err.getMessage());
        });

        Vertx vertx3 = Vertx.vertx();
        DependencyAnalyserLib analyzer3 = new DependencyAnalyserLib(vertx3);

        // Example usage of the getProjectDependencies method
        String projectPath = "./reactive-java";

        analyzer3.getProjectDependencies(projectPath).onSuccess(res -> {
            System.out.println(res);
            vertx3.close();
        }).onFailure(err -> {
            System.err.println("Error: " + err.getMessage());
        });
    }
}
