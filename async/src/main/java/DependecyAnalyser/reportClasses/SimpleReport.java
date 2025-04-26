package DependecyAnalyser.reportClasses;

import java.util.HashSet;
import java.util.Set;

public class SimpleReport implements Report {
    private Set<String> dependencies;

    public SimpleReport(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public Set<String> getDependencies() {
        return new HashSet<>(dependencies);
    }

    @Override
    public void addDependencies(Set<String> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    @Override
    public String toString() {
        return "SimpleReport{ " +
                "dependencies=" + dependencies +
                " }";
    }
    
}
