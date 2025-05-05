package DependecyAnalyser.reportClasses;

import java.util.Set;

/**
 * PackageDepsReport is a class that represents a report of a package's dependencies in a Java project.
 */
public class PackageDepsReport extends Report{

    public PackageDepsReport(Set<String> dependencies, String sourcePath) {
        super(dependencies, sourcePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- PACKAGE DEPS REPORT ---\n");
        sb.append("Source Path: ").append(sourcePath).append("\n");
        sb.append(super.toString());
        return sb.toString();
    }

}
