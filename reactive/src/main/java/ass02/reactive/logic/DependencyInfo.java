package ass02.reactive.logic;

import java.util.List;

public class DependencyInfo {
    public final String className;
    public final ClassType classType;
    public final String packageName;
    public final List<String> dependencies;

    public DependencyInfo(
            String className, 
            ClassType classType, 
            String packageName, 
            List<String> dependencies
        ) {
        this.className = className;
        this.classType = classType;
        this.packageName = packageName;
        this.dependencies = dependencies;
    }
}
