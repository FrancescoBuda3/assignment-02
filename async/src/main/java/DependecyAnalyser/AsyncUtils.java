package DependecyAnalyser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.vertx.core.Future;

public class AsyncUtils {
    /**
     * * a utility method that takes a list of futures of sets and returns a future of a set containing all the elements of the original sets.
     * @param <T> the type of the elements in the sets
     * @param coll the list of futures of set
     * @return a future of a set containing all the elements of the original sets
     */
    public static <T> Future<Set<T>> flat(List<Future<Set<T>>> coll) {
        return Future
            .all(coll)
            .map(results -> {
                Set<T> ret = new HashSet<>();
                for (int i = 0; i < results.size(); i++) {
                    Set<T> result = results.resultAt(i);
                    ret.addAll(result);
                }
                return ret;
            });
    }
}
