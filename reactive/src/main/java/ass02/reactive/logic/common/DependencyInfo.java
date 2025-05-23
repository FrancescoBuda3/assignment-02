package ass02.reactive.logic.common;

import java.util.List;

/**
 * This class represents the information about a dependency in a Java class.
 * It contains the class name, class type, package name, and a list of
 * dependencies.
 */
public class DependencyInfo {
    public final String className;
    public final String packageName;
    public final List<String> dependencies;

    public DependencyInfo(
            String className,
            String packageName,
            List<String> dependencies) {
        this.className = className;
        this.packageName = packageName;
        this.dependencies = dependencies;
    }
}
