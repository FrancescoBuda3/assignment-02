package DependecyAnalyser;

import DependecyAnalyser.reportClasses.ClassDepsReport;
import DependecyAnalyser.reportClasses.PackageDepsReport;
import DependecyAnalyser.reportClasses.ProjectDepsReport;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * DependencyAnalyserLib is a class that provides methods to analyze dependencies in Java projects.
 */
public class DependencyAnalyserLib {

    private final ProjectFileSystem fs;
    private final ProjectParser parser;

    public DependencyAnalyserLib(Vertx vertx) {
        this.fs = new ProjectFileSystem(vertx);
        this.parser = new ProjectParser(vertx);
    }
    
    /**
     * Get the dependencies of a class file.
     * @param classSrcFile the path to the class file
     * @return a Future containing the ClassDepsReport object
     */
    Future<ClassDepsReport> getClassDependencies(String classSrcFile) {
        return this.fs.isJavaFile(classSrcFile).compose(isJavaFile -> {
            if (!isJavaFile) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a Java file"));
            } else {
                return parser
                    .getImportsFromJavaFile(classSrcFile)
                    .compose(set -> Future.succeededFuture(new ClassDepsReport(set, classSrcFile)));
            }
        });
    }

    /**
     * Get the dependencies of a package.
     * @param packageSrcDir the path to the package directory
     * @return a Future containing the PackageDepsReport object
     */
    Future<PackageDepsReport> getPackageDependencies(String packageSrcDir) {
        return this.fs.isDirectory(packageSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            } else {
                return fs.getSourcesIn(packageSrcDir)
                    .compose(files -> parser.getImportsFromJavaFiles(files))
                    .compose(deps -> Future.succeededFuture(new PackageDepsReport(deps, packageSrcDir)));
            }
        });
    }

    /**
     * Get the dependencies of a project.
     * @param projectSrcDir the path to the project directory
     * @return a Future containing the ProjectDepsReport object
     */
    Future<ProjectDepsReport> getProjectDependencies(String projectSrcDir) {
        return this.fs.isDirectory(projectSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            } else {
                return fs.getDescendantsSources(projectSrcDir)
                    .compose(files -> parser.getImportsFromJavaFiles(files))
                    .compose(deps -> Future.succeededFuture(new ProjectDepsReport(deps, projectSrcDir)));
            }
        });
    }

    

    

    

    

    
}
