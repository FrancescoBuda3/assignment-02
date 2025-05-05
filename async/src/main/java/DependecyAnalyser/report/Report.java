package DependecyAnalyser.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Report is a class that represents a report of dependencies in a Java project.
 */
public class Report {
    private Set<String> dependencies;
    protected String sourcePath;

    public Report(Set<String> dependencies, String sourcePath) {
        this.dependencies = dependencies;
        this.sourcePath = sourcePath;
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
