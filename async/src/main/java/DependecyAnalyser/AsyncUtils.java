package DependecyAnalyser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.vertx.core.Future;

public class AsyncUtils {
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
