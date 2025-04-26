package DependecyAnalyser.reportClasses;

import java.util.Set;

public class ClassDepsReport extends SimpleReport {

    public ClassDepsReport(Set<String> dependencies) {
        super(dependencies);
    }

    @Override
    public String toString() {
        return "ClassDepsReport{ " +
                "dependencies=" + getDependencies() +
                " }";
    }

}
