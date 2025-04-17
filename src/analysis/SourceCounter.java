package analysis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SourceCounter is responsible for counting how many times
 * each source appears in the log entries.
 *
 * It also provides methods for identifying the most and least common sources.
 * The class is thread-safe and suitable for parallel processing.
 */
public class SourceCounter implements LogAnalyzer {

    private final Map<String, AtomicInteger> sourceCounts = new ConcurrentHashMap<>();

    /**
     * Increments the count for the given log source.
     *
     * @param source the source string (e.g. "Server1", "Database") to be counted
     */
    @Override
    public void analyze(String source) {
        sourceCounts.putIfAbsent(source, new AtomicInteger(0));
        sourceCounts.get(source).incrementAndGet();
    }

    /**
     * Returns a map of all sources and their associated counts.
     *
     * @return a map where the key is the source name and the value is the number of times it appeared
     */
    public Map<String, Integer> getSourceCounts() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (var entry : sourceCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        return result;
    }

    /**
     * Finds and returns the name of the source that appeared most frequently.
     *
     * @return the most common source name, or null if no sources were counted
     */
    public String getMostCommonSource() {
        return sourceCounts.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().get()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Returns the number of times the most common source appeared.
     *
     * @return the highest source count, or 0 if none exist
     */
    public int getMostCommonSourceCount() {
        return sourceCounts.values().stream()
                .mapToInt(AtomicInteger::get)
                .max()
                .orElse(0);
    }

    /**
     * Finds and returns the name of the source that appeared least frequently.
     *
     * @return the least common source name, or null if no sources were counted
     */
    public String getLeastCommonSource() {
        return sourceCounts.entrySet().stream()
                .min(Comparator.comparingInt(e -> e.getValue().get()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Returns the number of times the least common source appeared.
     *
     * @return the lowest source count, or 0 if none exist
     */
    public int getLeastCommonSourceCount() {
        return sourceCounts.values().stream()
                .mapToInt(AtomicInteger::get)
                .min()
                .orElse(0);
    }
}
