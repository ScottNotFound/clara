package net.scottnotfound.clara.reaction;

import net.scottnotfound.clara.IModule;
import net.scottnotfound.clara.ModuleBase;
import net.scottnotfound.clara.lang.ICommandReceiver;

public class ReactionModule extends ModuleBase implements ICommandReceiver, IModule {

    private static ReactionModule RM_INSTANCE;
    private static ReactionEngine RE_INSTANCE;

    public static ReactionModule getInstance() {
        if (RM_INSTANCE == null) {
            RM_INSTANCE = new ReactionModule();
        }
        return RM_INSTANCE;
    }

    private ReactionModule() {
        RE_INSTANCE = ReactionEngine.getInstance();
    }


    @Override
    public void executeCommand() {

    }

    @Override
    public void refuseCommand() {

    }
}
