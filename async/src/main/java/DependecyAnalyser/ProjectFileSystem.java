package DependecyAnalyser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

public class ProjectFileSystem {
    private static final String EXTENSION = ".java";

    private FileSystem fs;

    public ProjectFileSystem(Vertx vertx){
        this.fs = vertx.fileSystem();
    }

    public Future<Boolean> isJavaFile(String path) {
        if (path == null || !path.endsWith(EXTENSION)) {
            return Future.succeededFuture(false);
        } else {
            return fs
                .exists(path)
                .compose(exists -> exists ? Future.succeededFuture(true) : Future.succeededFuture(false));
        }
    }

    public Future<Boolean> isDirectory(String path) {
        if (path == null) {
            return Future.succeededFuture(false);
        } else {
            return fs
                .exists(path)
                .compose(exists -> exists ? fs.props(path).map(props -> props.isDirectory()) : Future.succeededFuture(false));
        }
    }

    private Future<Set<String>> getFromDirectory(String path, Function<String, Future<Boolean>> filterFunction) {
        return fs.readDir(path).compose(files -> {
            List<Future<Boolean>> futures = new ArrayList<>();
            for (String file : files) {
                futures.add(filterFunction.apply(file));
            }
            return Future
                .all(futures)
                .map(results -> {
                    Set<String> filteredFiles = new HashSet<>();
                    for (int i = 0; i < files.size(); i++) {
                        Boolean isFiltered = results.resultAt(i);
                        if (isFiltered) {
                            filteredFiles.add(files.get(i));
                        }
                    }
                    return filteredFiles;
                });
        });
    }

    public Future<Set<String>> getSourcesIn(String directory) {
        return getFromDirectory(directory, this::isJavaFile).compose(files -> {
            return Future.succeededFuture(files);
        });
    }

    public Future<Set<String>> getDescendantsSources(String directory) {
        return this.fs
            .readDir(directory)
            .compose(files -> {
                List<Future<Set<String>>> futures = new ArrayList<>();
                for (String file : files) {
                    futures.add(this.isDirectory(file)
                        .compose(isDirectory -> {
                            if(isDirectory) {
                                return this.getDescendantsSources(file);
                            } else {
                                return this
                                    .isJavaFile(file)
                                    .compose(isJavaFile -> Future.succeededFuture(isJavaFile ? Set.of(file) : new HashSet<String>()));
                            }
                        }));
                    }
                return AsyncUtils.flat(futures);
            });
    }
}
