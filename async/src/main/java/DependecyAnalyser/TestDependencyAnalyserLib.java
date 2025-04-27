package DependecyAnalyser;

import io.vertx.core.Vertx;

public class TestDependencyAnalyserLib {
    public static void main(String[] args) {
        // Create a Vertx instance
        Vertx vertx = Vertx.vertx();
        // Test the DependencyAnalyzerLib class
        DependencyAnalyserLib analyzer = new DependencyAnalyserLib(vertx);
        
        // Example usage of the getClassDependencies method
        String filePath = "./async/src/main/java/DependecyAnalyser/DependencyAnalyserLib.java";
        
        analyzer.getClassDependencies(filePath).onSuccess(res -> {
            System.out.println(res);
            vertx.close();
        }).onFailure(err -> {
            System.err.println("Error: " + err.getMessage());
        });

        // // Example usage of the getPackageDependencies method
        // String packagePath = "./async/src/main/java/DependecyAnalyser/reportClasses";

        // analyzer.getPackageDependencies(packagePath).onSuccess(res -> {
        //     System.out.println(res);
        // }).onFailure(err -> {
        //     System.err.println("Error: " + err.getMessage());
        // });

        // // Example usage of the getProjectDependencies method
        // String projectPath = "./async/src";

        // analyzer.getProjectDependencies(projectPath).onSuccess(res -> {
        //     System.out.println(res);
        // }).onFailure(err -> {
        //     System.err.println("Error: " + err.getMessage());
        // });
    }
}
