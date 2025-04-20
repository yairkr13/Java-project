package report;

import analysis.SourceCounter;
import analysis.LevelCounter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for generating and saving a JSON report
 * based on the results of the log analysis.
 * <p>
 * The report includes:
 * <ul>
 *   <li>{@code COUNT_LEVELS} – frequency of each log level</li>
 *   <li>{@code FIND_COMMON_SOURCE} – most/least common log sources</li>
 *   <li>{@code DETECT_ANOMALIES} – detected anomalies grouped by file</li>
 * </ul>
 */
public class ReportBuilder {

    /**
     * Creates and saves a full JSON report containing:
     * <ul>
     *   <li>Log level counts (if available)</li>
     *   <li>Most and least common sources (if available)</li>
     *   <li>Anomalies per file (if any)</li>
     * </ul>
     *
     * @param levelAnalyzer   an optional {@link LevelCounter} with level counts
     * @param sourceAnalyzer  an optional {@link SourceCounter} with source counts
     * @param anomalies       a map of detected anomalies: filename → list of timestamps
     * @param outputPath      the file path to write the report to (as JSON)
     */
    public static void saveFullReport(LevelCounter levelAnalyzer,
                                      SourceCounter sourceAnalyzer,
                                      Map<String, List<String>> anomalies,
                                      String outputPath) {
        JSONObject report = new JSONObject();

        //  COUNT_LEVELS
        if (levelAnalyzer != null) {
            JSONObject countLevels = new JSONObject();
            for (var entry : levelAnalyzer.getCounts().entrySet()) {
                countLevels.put(entry.getKey(), entry.getValue());
            }
            report.put("COUNT_LEVELS", countLevels);
        }

        //  FIND_COMMON_SOURCE
        if (sourceAnalyzer != null) {
            JSONObject commonSource = new JSONObject();
            var sourceCounts = sourceAnalyzer.getSourceCounts();

            List<String> sources = sourceCounts.keySet().stream().toList();
            List<Integer> counts = sources.stream().map(sourceCounts::get).toList();

            commonSource.put("sources", sources.toString());
            commonSource.put("source_counts", counts.toString());
            commonSource.put("most_common_source", sourceAnalyzer.getMostCommonSource());
            commonSource.put("most_common_source_count", sourceAnalyzer.getMostCommonSourceCount());
            commonSource.put("least_common_source", sourceAnalyzer.getLeastCommonSource());
            commonSource.put("least_common_source_count", sourceAnalyzer.getLeastCommonSourceCount());

            report.put("FIND_COMMON_SOURCE", commonSource);
        }

        //  DETECT_ANOMALIES
        if (anomalies != null && !anomalies.isEmpty()) {
            JSONObject allAnomalies = new JSONObject();

            for (Map.Entry<String, List<String>> entry : anomalies.entrySet()) {
                String filename = entry.getKey();
                List<String> times = entry.getValue();

                JSONObject fileObj = new JSONObject();
                fileObj.put("anomalies", times);
                fileObj.put("anomalies_count", times.size());

                allAnomalies.put(filename, fileObj);
            }

            JSONArray anomaliesArray = new JSONArray();
            anomaliesArray.put(allAnomalies);

            report.put("DETECT_ANOMALIES", anomaliesArray);
        } else {
            report.put("DETECT_ANOMALIES", new JSONArray());
        }

        try (FileWriter file = new FileWriter(outputPath)) {
            file.write(report.toString(4)); // with indentation
        } catch (IOException e) {
            System.out.println("error saving report");
        }
    }
}
