package net.scottnotfound.clara;

import net.scottnotfound.clara.interpret.CommandOptionManager;
import net.scottnotfound.clara.proxy.CommandOptionProxy;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class Clara {

    public static final String APP_NAME = "Clara";
    public static final String APP_VERSION = "0.0.001";
    public static final boolean isSNAPSHOT = true;

    private static Clara instance;
    private static CommandOptionProxy commandOptionProxy = new CommandOptionProxy();

    private Clara(){}


    public static void main(String[] args) throws ParseException, IOException {

        commandOptionProxy.initCommandOptions();
        CommandOptionManager commandOptionManager = new CommandOptionManager();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String input = reader.readLine();
            commandOptionManager.manageInput(input);
        }

    }

    public static void shutdown() {
        System.exit(0);
    }

    public static void shutdown(int status) {
        System.exit(status);
    }

    public static Clara getInstance() {
        if (instance == null) {
            instance = new Clara();
        }
        return instance;
    }
}
