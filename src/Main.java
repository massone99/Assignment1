import lineCounter.LineCounterCallable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String dir = args[0];

        int numInt = Integer.parseInt(args[1]);
        int maxLines = Integer.parseInt(args[2]);

        // Create a list of intervals
        List<Interval> intervals = initIntervals(numInt, maxLines);

        // Create a Map with num of values contained in each interval (init to 0)
        Map<Interval, Integer> intervalToCount = IntStream
                .range(0, intervals.size())
                .boxed()
                .collect(Collectors.toMap(intervals::get, i -> 0));

        // Create a list of source files from the given directory
        File path = new File(dir);
        List<Path> sourceFiles = getSourceFiles(path);

        // Create ExecutorService with a thread for each source file
        ExecutorService executor = Executors.newFixedThreadPool(sourceFiles.size());

        // Create a list of LineCounterCallable for each source file
        List<LineCounterCallable> lineCounterCallables = sourceFiles
                .stream()
                .map(p -> new LineCounterCallable(p.toFile()))
                .toList();

        List<Future<Long>> futureLines = lineCounterCallables.stream().map(executor::submit).toList();

        Thread valueChecker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (futureLines.stream().allMatch(Future::isDone)) {
                        // Notify the main thread that all the values have been collected
                        synchronized (this) {
                            this.notify();
                        }
                    }
                }
            }
        });

        /*CountDownLatch latch = new CountDownLatch(sourceFiles.size());

        Map<Path, LineCounterWorker> pathsToThreads = getPathToThreadMap(sourceFiles, latch);

        pathsToThreads
                .values()
                .forEach(LineCounterWorker::run);

        // Collect numLines from each thread
        List<Long> linesValues = pathsToThreads
                .values()
                .stream()
                .map(LineCounterWorker::getNumberOfLines)
                .toList();

        // Count the number of values contained in each interval
        linesValues
                .forEach(l -> {
                    for (Interval i : intervals) {
                        if (i.contains(l.intValue())) {
                            intervalToCount.put(i, intervalToCount.get(i) + 1);
                        }
                    }
                });

        // Print the result for each interval
        intervalToCount
                .forEach((k, v) -> System.out.println(k + " -> " + v));*/
    }

    private static List<Interval> initIntervals(int numInt, int maxLines) {
        return IntStream
                .range(0, numInt)
                .mapToObj(i -> {
                    int div = numInt - 1;
                    int upperBound = ((i + 1) * (maxLines / div) - 1);
                    int lowerBound = i * (maxLines / div);
                    boolean bounded = i != numInt - 1;
                    return new Interval(
                            lowerBound,
                            upperBound,
                            bounded);
                })
                .toList();
    }

    private static Map<Path, LineCounterCallable> getPathToThreadMap(List<Path> sourceFiles) {
        Map<Path, LineCounterCallable> lineCounters = new HashMap<>();
        for (Path p : sourceFiles) {
            LineCounterCallable worker = new LineCounterCallable(p.toFile());
            lineCounters.put(p, worker);
        }
        return lineCounters;
    }

    private static List<Path> getSourceFiles(File path) {
        List<Path> sourceFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path.getAbsolutePath()))) {
            sourceFiles = paths
                    .filter(p -> p.toString().endsWith(".java"))
                    .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceFiles;
    }
}
