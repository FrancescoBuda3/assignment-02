package DependecyAnalyser;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final AsyncParser asyncParser;

    public DependencyAnalyserLib(Vertx vertx) {
        this.vertx = vertx;
        this.fs = vertx.fileSystem();
        this.asyncParser = new AsyncParser();
    }

    Future<ClassDepsReport> getClassDependencies(String classSrcFile){
        return isJavaFile(classSrcFile).compose(isJavaFile -> {
            if (!isJavaFile) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a Java file"));
            } else {
                return asyncParser
                    .parse(classSrcFile, vertx)
                    .compose(ast -> ParsingUtilities.getImportsAsStrings(ast, vertx))
                    .compose(set -> Future.succeededFuture(new ClassDepsReport(set, classSrcFile)));
            }
        });
    }

    Future<PackageDepsReport> getPackageDependencies(String packageSrcDir) {
        return isDirectory(packageSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            } else {
                return getJavaFilesFromDirectory(packageSrcDir)
                    .compose(files -> getImportsFromJavaFiles(files))
                    .compose(deps -> Future.succeededFuture(new PackageDepsReport(deps, packageSrcDir)));
            } 
        });
    }

    Future<ProjectDepsReport> getProjectDependencies(String projectSrcDir) {
        return isDirectory(projectSrcDir).compose(isDirectory -> {
            if (!isDirectory) {
                return Future.failedFuture(new IllegalArgumentException("Path is not a directory"));
            }
            return getJavaFilesFromDirectoryAndSubdirectories(Path.of(projectSrcDir))
                    .compose(files -> getImportsFromJavaFiles(files))
                    .compose(deps -> Future.succeededFuture(new ProjectDepsReport(deps, projectSrcDir)));
        });
    }

    private Future<Boolean> isJavaFile(String path) {
        if (path == null || !path.endsWith(EXTENSION)) {
            return Future.succeededFuture(false);
        } else {
            return fs
                .exists(path)
                .compose(exists -> exists ? Future.succeededFuture(true) : Future.succeededFuture(false));
        }
    }

    private Future<Boolean> isDirectory(String path) {
        if (path == null) {
            return Future.succeededFuture(false);
        } else {
            return fs
                .exists(path)
                .compose(exists -> exists ? fs.props(path).map(props -> props.isDirectory()) : Future.succeededFuture(false));
        }
    }

    private Future<Set<String>> getFromDirectory(String path, Function<String, Future<Boolean>> filterFunction) {
        Set<String> filteredFiles = new HashSet<>();
        fs.readDir(path)
            .onSuccess(files -> {
                files.forEach(file -> {
                    filterFunction.apply(file).onSuccess(isFiltered -> {
                        if (isFiltered) {
                            filteredFiles.add(file);
                        }
                    });
                });
            });
        return Future.succeededFuture(filteredFiles);
    }

    private Future<Set<String>> getJavaFilesFromDirectory(String path) {
        return getFromDirectory(path, this::isJavaFile).compose(files -> {
            return Future.succeededFuture(files);
        });
    }

    private Future<Set<String>> getDirectoiesFromDirectory(String path) {
        return getFromDirectory(path, this::isDirectory).compose(files -> {
            return Future.succeededFuture(files);
        });
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
