package DependecyAnalyser.reportClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleReport implements Report {
    private Set<String> dependencies;
    protected String sourcePath;

    public SimpleReport(Set<String> dependencies, String sourcePath) {
        this.dependencies = dependencies;
        this.sourcePath = sourcePath;
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
        // sort the dependencies in alphabetical order
        List<String> depList = new ArrayList<String>(dependencies);
        Collections.sort(depList);
        // pritty print the dependencies as a bullet list
        StringBuilder sb = new StringBuilder();
        for (String dependency : depList) {
            sb.append(" - ").append(dependency).append("\n");
        }
        return sb.toString();
    }
    
}
