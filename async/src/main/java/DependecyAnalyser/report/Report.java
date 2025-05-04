package DependecyAnalyser.report;

import java.util.Set;

public interface Report {
    Set<String> getDependencies();
    void addDependencies(Set<String> dependencies);
}
