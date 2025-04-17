package Main;

import controller.Controller;

/**
 * Entry point of the log analysis application.
 * <p>
 * This class initializes and runs the {@link controller.Controller}
 * using the path to the configuration file.
 */
public class Main {

    /**
     * Starts the log analysis program.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Controller().run("config.properties");
    }
}
