package DependecyAnalyser.reportClasses;

import java.util.Set;

public class ProjectDepsReport extends SimpleReport{

    public ProjectDepsReport(Set<String> dependencies, String sourcePath) {
        super(dependencies, sourcePath);
    }

    @Override
    public String toString() {
        return "ProjectDepsReport{ " +
                "dependencies=" + getDependencies() +
                " }";
    }

}
