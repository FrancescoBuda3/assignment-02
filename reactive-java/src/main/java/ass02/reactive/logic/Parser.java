package ass02.reactive.logic;

import java.nio.file.Path;
import java.util.Set;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

public interface Parser {
    public class DependencyInfo {
        public final String className;
        public final String packageName;
        public final Set<String> dependencies;

        public DependencyInfo(String className, String packageName, Set<String> dependencies) {
            this.className = className;
            this.packageName = packageName;
            this.dependencies = dependencies;
        }
    }

    Flowable<DependencyInfo> analyse(Path rootFolder, FlowableEmitter<Integer> emitter);
}
