package analysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LevelCounter is responsible for counting the occurrences
 * of different log levels (e.g. INFO, WARNING, ERROR).
 *
 * This implementation is thread-safe using {@link ConcurrentHashMap} and {@link AtomicInteger},
 * making it suitable for use in concurrent log processing environments.
 */
public class LevelCounter implements LogAnalyzer {

    private final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();

    /**
     * Processes a single log level string and increments its count.
     *
     * @param level the log level to be counted (case-insensitive)
     */
    @Override
    public void analyze(String level) {
        counts.putIfAbsent(level.toLowerCase(), new AtomicInteger(0));
        counts.get(level.toLowerCase()).incrementAndGet();
    }

    /**
     * Returns a map of log levels to their respective counts.
     *
     * @return a map where the key is the log level (in lowercase)
     *         and the value is the number of times it appeared
     */
    public Map<String, Integer> getCounts() {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        for (var entry : counts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        return result;
    }
}
