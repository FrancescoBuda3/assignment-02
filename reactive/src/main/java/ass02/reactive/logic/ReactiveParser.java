package ass02.reactive.logic;

import java.nio.file.Path;
import java.util.function.Consumer;

import ass02.reactive.logic.common.DependencyInfo;

/**
 * ReactiveParser is an interface for analyzing Java files in a given
 * directory. It provides a method to analyze the files and send updates about
 * the dependency information to a consumer.
 */
public interface ReactiveParser {

    /**
     * Analyzes the files in the specified root folder and sends updates to the
     * provided consumer.
     *
     * @param rootFolder The root folder containing the files to analyze.
     * @param sendUpdate A consumer that receives updates about the dependency
     *                   information.
     */
    void analyse(Path rootFolder, Consumer<DependencyInfo> sendUpdate);

}
