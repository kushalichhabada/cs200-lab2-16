package chocan;

import chocan.gui.ChocAnGui;
import chocan.ui.ChocAnApp;
import chocan.ui.ConsoleIO;
import chocan.ui.InputEndedException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Program entry point. Loads the saved data (or creates the sample data on the
 * first run) and starts either the console interface (default) or the Swing
 * GUI.
 *
 * <p>Options: {@code --gui} opens the graphical interface; {@code --reset}
 * throws away the saved data and starts over.
 *
 * @author [TEAM MEMBER NAME]
 */
public class Main {

    private static final Path DATA_FILE = Paths.get("data", "chocan.dat");
    private static final Path REPORT_DIR = Paths.get("reports");

    public static void main(String[] args) {
        List<String> options = Arrays.asList(args);
        boolean gui = options.contains("--gui");
        boolean reset = options.contains("--reset");

        AppContext context;
        try {
            context = AppContext.open(DATA_FILE, REPORT_DIR, reset);
        } catch (IOException e) {
            System.err.println("Fatal file error: " + e.getMessage());
            System.err.println("If the saved data is damaged, run again with --reset.");
            return;
        }

        if (gui) {
            ChocAnGui.launch(context);
        } else {
            runConsole(context, reset);
        }
    }

    private static void runConsole(AppContext context, boolean reset) {
        ConsoleIO io = new ConsoleIO(System.in, System.out);
        if (reset) {
            io.println("Saved data deleted; starting with fresh sample data.");
        }
        io.println(context.isSampleDataCreated()
                ? "No saved data found - created sample data in " + DATA_FILE + "."
                : "Loaded saved data from " + DATA_FILE + ".");
        try {
            new ChocAnApp(io,
                    context.getAuth(),
                    context.getMembers(),
                    context.getProviders(),
                    context.getBilling(),
                    context.getReports(),
                    context.getAccounting(),
                    context.getDirectory()).run();
        } catch (InputEndedException e) {
            io.println("");
            io.println("Input closed. Exiting.");
        }
    }
}
