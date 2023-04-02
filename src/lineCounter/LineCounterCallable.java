package lineCounter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class LineCounterCallable implements Callable<Long> {
    private final File file;


    public LineCounterCallable(File file) {
        this.file = file;
    }

    private long countNumberOfLines(File file) {
        int numLines;
        try (Stream<String> lines = Files.lines(file.toPath())) {
            numLines = (int) lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return numLines;
    }

    @Override
    public Long call() {
        return countNumberOfLines(file);
    }
}