package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single log entry parsed from a log file.
 * Each entry includes a timestamp, log level, source, and message.
 */
public class LogEntry {

    private final LocalDateTime timestamp;
    private final String level;
    private final String source;
    private final String message;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a new {@code LogEntry} using the given string values.
     *
     * @param timestampStr the timestamp in format "yyyy-MM-dd HH:mm:ss"
     * @param level         the log level (e.g., INFO, ERROR, WARNING)
     * @param source        the origin/source of the log (e.g., Server1)
     * @param message       the message text of the log
     */
    public LogEntry(String timestampStr, String level, String source, String message) {
        this.timestamp = LocalDateTime.parse(timestampStr, formatter);
        this.level = level;
        this.source = source;
        this.message = message;
    }

    /**
     * Returns the parsed timestamp of the log entry.
     *
     * @return a {@link LocalDateTime} representing the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the log level of the entry.
     *
     * @return the log level (e.g., "INFO")
     */
    public String getLevel() {
        return level;
    }

    /**
     * Returns the source of the log entry.
     *
     * @return the log source (e.g., "Server1")
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the log message.
     *
     * @return the message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of the log entry in its original format.
     *
     * @return formatted string representing the log line
     */
    @Override
    public String toString() {
        return "[" + timestamp + "] [" + level + "] [" + source + "] [" + message + "]";
    }
}
