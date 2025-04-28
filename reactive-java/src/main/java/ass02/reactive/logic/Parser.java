package ass02.reactive.logic;

import java.nio.file.Path;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

public interface Parser {

    Flowable<DependencyInfo> analyse(Path rootFolder, FlowableEmitter<Integer> emitter);

}
