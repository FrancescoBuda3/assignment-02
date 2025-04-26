package DependecyAnalyser.reportClasses;

import java.util.Set;

public interface Report {
    Set<String> getDependencies();
    void addDependencies(Set<String> dependencies);
}
