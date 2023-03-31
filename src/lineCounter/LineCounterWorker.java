package lineCounter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class LineCounterWorker implements Runnable {
    private final File file;
    private final CountDownLatch latch;
    private long numLines;


    public LineCounterWorker(File file, CountDownLatch latch) {
        this.file = file;
        this.latch = latch;
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

    public long getNumberOfLines() {
        return this.numLines;
    }

    @Override
    public synchronized void run() {
        this.numLines = countNumberOfLines(file);
        this.latch.countDown();
        if (this.latch.getCount() == 0) {
            printTerminalMessage();
            printResult();
            // Wakes up all threads
            notifyAll();
        } else {
            printResult();
        }
    }

    private void printTerminalMessage() {
        System.out.println("All threads have finished");
        System.out.println("Printing results");
        System.out.println("-----------------------------");
    }

    private void printResult() {
        System.out.println(
                file.getName() +
                        "\n----Num lines: " + this.numLines
        );
    }
}