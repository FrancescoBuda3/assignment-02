package ass02.reactive.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

public class ParserImpl implements Parser {
    JavaParser parser;

    public ParserImpl() {
        this.parser = new JavaParser();
    }

    @Override
    public Flowable<DependencyInfo> analyse(Path rootFolder, FlowableEmitter<Integer> emitter) {
        AtomicLong totalFiles = new AtomicLong(0);
        try {
            totalFiles.set(Files.walk(rootFolder).filter(p -> p.toString().endsWith(".java")).count());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicLong processedFiles = new AtomicLong(0);

        return Flowable.fromIterable(() -> {
            try {
                return Files.walk(rootFolder).iterator();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
                .filter(p -> p.toString().endsWith(".java"))
                .map(file -> {
                    ParseResult<CompilationUnit> result = this.parser.parse(file);
                    
                    DependencyCollector collector = new DependencyCollector();
                    if (result.isSuccessful() && result.getResult().isPresent()) {
                        CompilationUnit cu = result.getResult().get();
                        collector.visit(cu, null);
                    }

                    long processedCount = processedFiles.incrementAndGet();
                    int progress = (int) (100 * processedCount / totalFiles.get());
                    emitter.onNext(progress);

                    return collector.getInfos();
                });
    }
}
