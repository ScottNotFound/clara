package net.scottnotfound.clara.lang;

import net.scottnotfound.clara.Clara;
import net.scottnotfound.clara.init.CommandOptionInit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

public class CommandOptionManager {

    private CommandLineParser parser;

    public CommandOptionManager() {
        this.parser = new DefaultParser();
    }

    public void manageInput(String inputLine) {
        manageInput(inputLine.split(" "));
    }

    public void manageInput(String[] input) {
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(CommandOptionInit.getOptions(), input);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Clara.shutdown(1);
        }

        if (commandLine.hasOption("q")) {
            Clara.shutdown(0);
        }
    }
}
