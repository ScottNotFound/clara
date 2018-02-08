package net.scottnotfound.clara.proxy;

import net.scottnotfound.clara.init.CommandOptionInit;

public class CommandOptionProxy {



    public void initCommandOptions() {

        CommandOptionInit.initReactionCommandOptions();
        CommandOptionInit.initInstanceCommandOptions();

    }

    public void execute(String commandLine) {

    }

    public void execute(String[] commandLineArgs) {

    }
}
