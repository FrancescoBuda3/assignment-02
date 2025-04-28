package ass02.reactive.logic;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface Parser {

    void analyse(Path rootFolder, Consumer<DependencyInfo> sendUpdate);

}
