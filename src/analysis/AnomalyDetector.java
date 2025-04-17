package analysis;

import model.LogEntry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AnomalyDetector identifies bursts of log entries based on configured severity levels
 * within a given time window. It tracks logs per file and detects timestamps
 * where anomalies (spikes) occur.
 */
public class AnomalyDetector implements LogAnalyzer {
    private final Set<String> levelsToDetect;
    private final int windowInSeconds;
    private final int threshold;

    private final Map<String, List<LogEntry>> entriesPerFile = new ConcurrentHashMap<>();

    private static final java.time.format.DateTimeFormatter formatter =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs an AnomalyDetector with the given configuration parameters.
     *
     * @param levelsToDetect   log levels to monitor for anomalies (e.g. ERROR, WARNING)
     * @param windowInSeconds  the time window in seconds in which multiple entries are considered a burst
     * @param threshold        the minimum number of log entries in the window to consider it an anomaly
     */
    public AnomalyDetector(Set<String> levelsToDetect, int windowInSeconds, int threshold) {
        this.levelsToDetect = levelsToDetect;
        this.windowInSeconds = windowInSeconds;
        this.threshold = threshold;
    }

    /**
     * Required method from LogAnalyzer interface.
     * Not used in this class since anomaly detection is based on full LogEntry data.
     *
     * @param value ignored in this implementation
     */
    @Override
    public void analyze(String value) {
        // Not used
    }

    /**
     * Analyzes a single log entry and stores it per file if it matches a tracked level.
     *
     * @param filename the name of the log file where the entry came from
     * @param entry    the log entry to analyze
     */
    public void analyze(String filename, LogEntry entry) {
        if (levelsToDetect.contains(entry.getLevel().toUpperCase())) {
            entriesPerFile.putIfAbsent(filename, new CopyOnWriteArrayList<>());
            entriesPerFile.get(filename).add(entry);
        }
    }

    /**
     * Detects anomalies in all processed log entries.
     * An anomaly is detected if there are {@code threshold} log entries or more
     * within {@code windowInSeconds}, starting from a specific timestamp.
     *
     * @return a map where the key is the log filename, and the value is a list of timestamps where anomalies begin
     */
    public Map<String, List<String>> detectAnomalies() {
        Map<String, List<String>> result = new HashMap<>();

        for (var fileEntry : entriesPerFile.entrySet()) {
            List<LogEntry> entries = fileEntry.getValue();
            entries.sort(Comparator.comparing(LogEntry::getTimestamp));

            List<String> anomalies = new ArrayList<>();

            for (int i = 0; i <= entries.size() - threshold; i++) {
                LocalDateTime start = entries.get(i).getTimestamp();
                LocalDateTime end = entries.get(i + threshold - 1).getTimestamp();

                if (Duration.between(start, end).getSeconds() <= windowInSeconds) {
                    anomalies.add(start.format(formatter));
                }
            }

            if (!anomalies.isEmpty()) {
                result.put(fileEntry.getKey(), anomalies);
            }
        }

        return result;
    }
}
