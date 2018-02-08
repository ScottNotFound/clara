package net.scottnotfound.clara.init;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.LinkedList;
import java.util.List;

public class CommandOptionInit {

    private List<Options> optionsList = new LinkedList<>();

    private static final CommandOptionInit instance = new CommandOptionInit();

    private CommandOptionInit(){}

    public static CommandOptionInit getInstance() {
        return instance;
    }

    public static void initReactionCommandOptions() {

        Options reactionOptions = new Options();

        Option reactOption = Option.builder()
                .argName("react")
                .longOpt("react")
                .hasArg()
                .build();
        Option withOption = Option.builder()
                .argName("with")
                .longOpt("with")
                .hasArg()
                .build();


        reactionOptions.addOption(reactOption);
        reactionOptions.addOption(withOption);

        instance.optionsList.add(reactionOptions);
    }

    public static void initInstanceCommandOptions() {

        Options instanceOptions = new Options();

        Option exitOption = Option.builder("q")
                .argName("exit")
                .longOpt("exit")
                .longOpt("quit")
                .build();

        instanceOptions.addOption(exitOption);

        instance.optionsList.add(instanceOptions);
    }

    public static Options getOptions() {
        Options options = new Options();
        for (Options options1 : instance.optionsList) {
            for (Option option1 : options1.getOptions()) {
                options.addOption(option1);
            }
        }
        return options;
    }
}
