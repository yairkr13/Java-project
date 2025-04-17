package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * ConfigManager is responsible for loading and providing access
 * to the configuration settings from a properties file.
 *
 * It provides default values for all supported keys and handles invalid input gracefully.
 */
public class ConfigManager {

    private static final String DEFAULT_LOG_DIRECTORY = "logs";
    private static final int DEFAULT_THREAD_POOL_SIZE = 5;
    private static final String DEFAULT_ANOMALY_WINDOW = "60";
    private static final String DEFAULT_ANOMALY_THRESHOLD = "5";
    private static final String DEFAULT_OUTPUT_FILE = "output.json";
    private static final String DEFAULT_ANALYSIS = "COUNT_LEVELS";

    private final Properties props = new Properties();

    /**
     * Loads configuration from the specified properties file path.
     *
     * @param path the path to the properties file
     */
    public ConfigManager(String path) {
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("error loading configuration");
        }
    }

    /**
     * Returns the configured directory path for log files.
     *
     * @return the log directory path, or the default ("logs") if not set
     */
    public String getLogDirectory() {
        return props.getProperty("log.directory", DEFAULT_LOG_DIRECTORY);
    }

    /**
     * Returns the configured thread pool size.
     *
     * @return the number of threads to use, or the default (5) if invalid or missing
     */
    public int getThreadPoolSize() {
        try {
            return Integer.parseInt(props.getProperty("thread.pool.size", String.valueOf(DEFAULT_THREAD_POOL_SIZE)));
        } catch (NumberFormatException e) {
            return DEFAULT_THREAD_POOL_SIZE;
        }
    }

    /**
     * Returns the configured output file name for the JSON report.
     *
     * @return the file name, or "output.json" if not defined
     */
    public String getOutputFile() {
        return props.getProperty("output.file", DEFAULT_OUTPUT_FILE);
    }

    /**
     * Returns the list of requested analysis types from the configuration.
     *
     * @return an array of analysis type strings
     */
    public String[] getAnalysisTypes() {
        return props.getProperty("log.analysis", DEFAULT_ANALYSIS).split(",");
    }

    /**
     * Returns the log levels to be used for anomaly detection.
     *
     * @return a set of log levels (e.g., "ERROR", "WARNING")
     */
    public Set<String> getAnomalyLevels() {
        String val = props.getProperty("log.analysis.anomalies.levels", "ERROR");
        String[] parts = val.split(",");
        Set<String> levels = new HashSet<>();
        for (String p : parts) {
            levels.add(p.trim().toUpperCase());
        }
        return levels;
    }

    /**
     * Returns the time window (in seconds) for anomaly detection.
     *
     * @return the time window, or 60 seconds if not defined or invalid
     */
    public int getAnomalyWindow() {
        try {
            return Integer.parseInt(props.getProperty("log.analysis.anomalies.window", DEFAULT_ANOMALY_WINDOW));
        } catch (NumberFormatException e) {
            return Integer.parseInt(DEFAULT_ANOMALY_WINDOW);
        }
    }

    /**
     * Returns the threshold for number of entries to qualify as an anomaly.
     *
     * @return the minimum number of entries, or 5 if not defined or invalid
     */
    public int getAnomalyThreshold() {
        try {
            return Integer.parseInt(props.getProperty("log.analysis.anomalies.threshold", DEFAULT_ANOMALY_THRESHOLD));
        } catch (NumberFormatException e) {
            return Integer.parseInt(DEFAULT_ANOMALY_THRESHOLD);
        }
    }
}
