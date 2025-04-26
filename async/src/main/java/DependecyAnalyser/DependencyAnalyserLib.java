package DependecyAnalyser;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.javaparser.StaticJavaParser;
import DependecyAnalyser.reportClasses.ClassDepsReport;
import DependecyAnalyser.reportClasses.PackageDepsReport;
import DependecyAnalyser.reportClasses.ProjectDepsReport;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

public class DependencyAnalyserLib {

    private static final String EXTENSION = ".java";

    private final Vertx vertx;
    private final FileSystem fs;

    public DependencyAnalyserLib(Vertx vertx) {
        this.vertx = vertx;
        this.fs = vertx.fileSystem();
    }

    Future<ClassDepsReport> getClassDependencies(String classSrcFile){
        return isJavaFile(classSrcFile).compose(isJavaFile -> {
            if (!isJavaFile) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a Java file"));
            }
            return this.vertx
            .executeBlocking(() -> {
                return StaticJavaParser
                    .parse(Path.of(classSrcFile))
                    .getImports()
                    .stream()
                    .map(importDecl -> importDecl.getNameAsString())
                    .collect(Collectors.toSet());
            })
            .compose(dependencies -> Future.succeededFuture(new ClassDepsReport(dependencies)))
            .onFailure(err -> err.printStackTrace());
        });
        
        
    }

    Future<PackageDepsReport> getPackageDependencies(String packageSrcDir){
        return isDirectory(packageSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            }
            return getJavaFilesFromDirectory(Path.of(packageSrcDir))
            .compose(files -> getImportsFromJavaFiles(files))
            .compose(dependencies -> {
                return Future.succeededFuture(new PackageDepsReport(dependencies));
            });
        }); 
    }

    Future<ProjectDepsReport> getProjectDependencies(String projectSrcDir){
        return isDirectory(projectSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            }
            return getJavaFilesFromDirectoryAndSubdirectories(Path.of(projectSrcDir))
            .compose(files -> getImportsFromJavaFiles(files))
            .compose(dependencies -> {
                return Future.succeededFuture(new ProjectDepsReport(dependencies));
            });
        }); 
        
    }

    private Future<Boolean> isJavaFile(String filePath) {
        if (filePath == null || !filePath.endsWith(EXTENSION)) {
          return Future.succeededFuture(false);
        }
      
        return fs.exists(filePath).compose(exists -> {
          if (!exists) {
            return Future.succeededFuture(false);
          } else {
            return Future.succeededFuture(true);
          }
        });
    }

    private Future<Boolean> isDirectory(String directoryPath) {
        if (directoryPath == null) {
            return Future.succeededFuture(false);
        }
      
        return fs.exists(directoryPath).compose(exists -> {
          if (!exists) {
            return Future.succeededFuture(false);
          } else {
            return fs.props(directoryPath)
                .map(props -> props.isDirectory())
                .onFailure(err -> {
                    err.printStackTrace();
                });
          }
        });
    }

    private Future<Set<String>> getJavaFilesFromDirectory(Path directory) {
        return fs.readDir(directory.toString()).map(files -> files.stream()
            .filter(file -> file.endsWith(EXTENSION))
            .collect(Collectors.toSet())
        );
    }

    private Future<Set<String>> getJavaFilesFromDirectoryAndSubdirectories(Path directory) {
        return fs.readDir(directory.toString()).compose(files -> {
            List<Future<Set<String>>> futures = files.stream()
                .map(file -> {
                    if (file.endsWith(EXTENSION)) {
                        return Future.succeededFuture(Set.of(file));
                    } else {
                        return getJavaFilesFromDirectoryAndSubdirectories(Path.of(file));
                    }
                })
                .collect(Collectors.toList());
    
            // Combine all futures into one
            return Future.all(futures).map(completedFutures -> {
                return completedFutures.list().stream()
                    .flatMap(set -> ((Set<String>) set).stream())
                    .filter(file -> file.endsWith(EXTENSION))
                    .collect(Collectors.toSet());
            });
        });
    }

    private Future<Set<String>> getImportsFromJavaFiles(Set<String> javaFiles) {
        List<Future<ClassDepsReport>> classDependenciesFutures = javaFiles.stream()
                    .map(file -> getClassDependencies(file))
                    .collect(Collectors.toList());

                return Future.all(classDependenciesFutures).map(futures -> {
                    return futures.list().stream()
                        .flatMap(report -> ((ClassDepsReport) report).getDependencies().stream())
                        .collect(Collectors.toSet());
                });
    }
}
