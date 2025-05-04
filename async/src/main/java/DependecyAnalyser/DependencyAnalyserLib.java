package DependecyAnalyser;

import DependecyAnalyser.report.ClassDepsReport;
import DependecyAnalyser.report.PackageDepsReport;
import DependecyAnalyser.report.ProjectDepsReport;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class DependencyAnalyserLib {

    private final ProjectFileSystem fs;
    private final ProjectParser parser;

    public DependencyAnalyserLib(Vertx vertx) {
        this.fs = new ProjectFileSystem(vertx);
        this.parser = new ProjectParser(vertx);
    }

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

    Future<ProjectDepsReport> getProjectDependencies(String projectSrcDir) {
        return this.fs.isDirectory(projectSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            }
            return fs.getDescendantsSources(projectSrcDir)
                    .compose(files -> parser.getImportsFromJavaFiles(files))
                    .compose(deps -> Future.succeededFuture(new ProjectDepsReport(deps, projectSrcDir)));
        });
    }










}
