package ass02.reactive.logic;

import java.nio.file.Path;
import java.util.List;

public interface Parser {

    List<DependencyInfo> analyse(Path rootFolder);

}
