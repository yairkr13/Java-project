package processing;

import analysis.AnomalyDetector;
import analysis.LevelCounter;
import analysis.LogAnalyzer;
import analysis.SourceCounter;
import model.LogEntry;

import java.io.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for processing a single log file and delegating each line
 * to the appropriate {@link LogAnalyzer} implementations.
 * <p>
 * This class implements {@link Callable} to support parallel execution
 * using thread pools.
 */
public class LogFileProcessor implements Callable<Void> {

    private final File file;
    private final List<LogAnalyzer> analyzers;

    // Regular expression to parse a log line: [timestamp] [level] [source] [message]
    private static final Pattern logPattern = Pattern.compile("^\\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\]$");

    /**
     * Constructs a new LogFileProcessor.
     *
     * @param file      the log file to be processed
     * @param analyzers the list of analyzers to apply on each log line
     */
    public LogFileProcessor(File file, List<LogAnalyzer> analyzers) {
        this.file = file;
        this.analyzers = analyzers;
    }

    /**
     * Reads the log file line by line and applies each analyzer
     * to the relevant part of the parsed log entry.
     * <p>
     * Each line is parsed using a regular expression. If it matches,
     * a {@link LogEntry} is created and passed to the analyzers based on their type.
     * If a line does not match, it is skipped and a warning is printed.
     *
     * @return null (void), as required by {@link Callable}
     */
    @Override
    public Void call() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = logPattern.matcher(line);
                if (matcher.matches()) {
                    String timestamp = matcher.group(1);
                    String level = matcher.group(2);
                    String source = matcher.group(3);
                    String message = matcher.group(4);

                    LogEntry entry = new LogEntry(timestamp, level, source, message);

                    for (LogAnalyzer analyzer : analyzers) {
                        if (analyzer instanceof AnomalyDetector detector) {
                            detector.analyze(file.getName(), entry);
                        } else if (analyzer instanceof LevelCounter) {
                            analyzer.analyze(level);
                        } else if (analyzer instanceof SourceCounter) {
                            analyzer.analyze(source);
                        }
                    }

                } else {
                    System.out.println("unexpected input " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("error processing file " + file.getName());
        }

        return null;
    }
}
