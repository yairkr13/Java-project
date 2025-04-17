package controller;

import analysis.AnomalyDetector;
import analysis.SourceCounter;
import analysis.LevelCounter;
import analysis.LogAnalyzer;
import analysis.AnalyzerFactory;
import config.ConfigManager;
import processing.LogFileProcessor;
import report.ReportBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The main controller responsible for coordinating the log analysis process.
 * <p>
 * This class handles configuration loading, analyzer instantiation,
 * concurrent log file processing, and report generation.
 */
public class Controller {

    /**
     * Runs the log analysis process using the configuration provided at the given path.
     * <p>
     * The process includes:
     * <ul>
     *   <li>Loading configuration settings</li>
     *   <li>Scanning the logs directory for .log files</li>
     *   <li>Creating analyzers dynamically based on config</li>
     *   <li>Processing logs in parallel using a thread pool</li>
     *   <li>Aggregating results and printing them</li>
     *   <li>Saving final report as a JSON file</li>
     * </ul>
     *
     * @param configPath the path to the configuration properties file
     */
    public void run(String configPath) {
        ConfigManager config = new ConfigManager(configPath);
        File logFolder = new File(config.getLogDirectory());

        if (!logFolder.exists() || !logFolder.isDirectory()) {
            System.out.println("invalid log path");
            return;
        }

        File[] logFiles = logFolder.listFiles((dir, name) -> name.endsWith(".log"));
        if (logFiles == null || logFiles.length == 0) {
            System.out.println("No log files found.");
            return;
        }

        // יצירת האנלייזרים לפי config
        List<LogAnalyzer> analyzers = AnalyzerFactory.createAnalyzers(config);

        // הפניה לאובייקטים לפי סוג
        LevelCounter levelAnalyzer = null;
        SourceCounter sourceAnalyzer = null;
        AnomalyDetector anomalyAnalyzer = null;

        for (LogAnalyzer analyzer : analyzers) {
            if (analyzer instanceof LevelCounter)
                levelAnalyzer = (LevelCounter) analyzer;
            else if (analyzer instanceof SourceCounter)
                sourceAnalyzer = (SourceCounter) analyzer;
            else if (analyzer instanceof AnomalyDetector)
                anomalyAnalyzer = (AnomalyDetector) analyzer;
        }

        // Thread pool
        ExecutorService executor = Executors.newFixedThreadPool(config.getThreadPoolSize());
        for (File logFile : logFiles) {
            executor.submit(new LogFileProcessor(logFile, analyzers));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // הדפסות לפי מה שקיים
        if (levelAnalyzer != null) {
            System.out.println("Log level counts:");
            for (Map.Entry<String, Integer> entry : levelAnalyzer.getCounts().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        if (sourceAnalyzer != null) {
            System.out.println("\nSources:");
            for (Map.Entry<String, Integer> entry : sourceAnalyzer.getSourceCounts().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            System.out.println("Most common source: " + sourceAnalyzer.getMostCommonSource());
            System.out.println("Least common source: " + sourceAnalyzer.getLeastCommonSource());
        }

        Map<String, List<String>> anomalies = new HashMap<>();
        if (anomalyAnalyzer != null) {
            anomalies = anomalyAnalyzer.detectAnomalies();
            if (!anomalies.isEmpty()) {
                System.out.println("\nAnomalies Detected:");
                for (var entry : anomalies.entrySet()) {
                    System.out.println("File: " + entry.getKey());
                    for (String timestamp : entry.getValue()) {
                        System.out.println("  -> " + timestamp);
                    }
                }
            }
        }

        // שמירת הדו"ח ל־JSON
        ReportBuilder.saveFullReport(
                levelAnalyzer,
                sourceAnalyzer,
                anomalies,
                config.getOutputFile()
        );
    }
}
