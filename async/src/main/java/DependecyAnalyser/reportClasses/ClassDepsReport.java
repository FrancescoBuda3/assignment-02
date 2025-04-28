package DependecyAnalyser.reportClasses;

import java.util.Set;

public class ClassDepsReport extends SimpleReport {

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
