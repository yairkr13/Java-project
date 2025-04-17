package analysis;

import config.ConfigManager;
import java.util.*;

/**
 * A factory class for creating a list of {@link LogAnalyzer} objects
 * based on the analysis types specified in the configuration.
 */
public class AnalyzerFactory {

    /**
     * Creates and returns a list of analyzers according to the configuration settings.
     * Supported analysis types include:
     * <ul>
     *     <li>COUNT_LEVELS – to count the frequency of each log level</li>
     *     <li>FIND_COMMON_SOURCE – to find the most/least common log sources</li>
     *     <li>DETECT_ANOMALIES – to detect spikes in specific log levels</li>
     * </ul>
     *
     * @param config the configuration object containing analysis options and parameters
     * @return a list of initialized {@link LogAnalyzer} instances based on the configuration
     */
    public static List<LogAnalyzer> createAnalyzers(ConfigManager config) {
        List<LogAnalyzer> analyzers = new ArrayList<>();

        Set<String> types = new HashSet<>();
        for (String type : config.getAnalysisTypes()) {
            types.add(type.trim().toUpperCase());
        }

        if (types.contains("COUNT_LEVELS")) {
            analyzers.add(new LevelCounter());
        }

        if (types.contains("FIND_COMMON_SOURCE")) {
            analyzers.add(new SourceCounter());
        }

        if (types.contains("DETECT_ANOMALIES")) {
            analyzers.add(new AnomalyDetector(
                    config.getAnomalyLevels(),
                    config.getAnomalyWindow(),
                    config.getAnomalyThreshold()
            ));
        }

        return analyzers;
    }
}
