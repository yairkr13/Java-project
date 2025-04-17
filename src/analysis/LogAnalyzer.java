package analysis;

/**
 * An interface for analyzing log entries.
 * Implementing classes define how to handle a single log value,
 * such as a log level or log source.
 */
public interface LogAnalyzer {

    /**
     * Analyzes a single string value from a log entry.
     * The meaning of the value depends on the implementation
     * (e.g., a log level, a log source, etc.).
     *
     * @param level the log-related value to be analyzed
     */
    void analyze(String level);
}
