package DependecyAnalyser.report;

import java.util.Set;

/**
 * ClassDepsReport is a class that represents a report of a class' dependencies in a Java project.
 */
public class ClassDepsReport extends Report {

    public ClassDepsReport(Set<String> dependencies, String sourcePath) {
        super(dependencies, sourcePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- CLASS DEPS REPORT ---\n");
        sb.append("Source Path: ").append(sourcePath).append("\n");
        sb.append(super.toString());
        return sb.toString();
    }
}
