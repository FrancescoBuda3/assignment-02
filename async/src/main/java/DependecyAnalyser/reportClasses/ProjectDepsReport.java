package DependecyAnalyser.reportClasses;

import java.util.Set;

public class ProjectDepsReport extends SimpleReport{

    public ProjectDepsReport(Set<String> dependencies) {
        super(dependencies);
    }

    @Override
    public String toString() {
        return "ProjectDepsReport{ " +
                "dependencies=" + getDependencies() +
                " }";
    }

}
