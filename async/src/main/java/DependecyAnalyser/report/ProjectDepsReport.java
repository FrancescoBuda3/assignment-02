package DependecyAnalyser.report;

import java.util.Set;

/**
 * ProjectDepsReport is a class that represents a report of a java project's dependencies.
 */
public class ProjectDepsReport extends Report{

    public ProjectDepsReport(Set<String> dependencies, String sourcePath) {
        super(dependencies, sourcePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- PROJECT DEPS REPORT ---\n");
        sb.append("Source Path: ").append(sourcePath).append("\n");
        sb.append(super.toString());
        return sb.toString();
    }

}
