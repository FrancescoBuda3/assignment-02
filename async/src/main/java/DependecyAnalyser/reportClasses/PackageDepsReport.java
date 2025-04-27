package DependecyAnalyser.reportClasses;

import java.util.Set;

public class PackageDepsReport extends SimpleReport{

    public PackageDepsReport(Set<String> dependencies, String sourcePath) {
        super(dependencies, sourcePath);
    }

    @Override
    public String toString() {
        return "PackageDepsReport{ " +
                "dependencies=" + getDependencies() +
                " }";
    }

}
