package net.scottnotfound.clara.reaction;

import net.scottnotfound.clara.IModule;
import net.scottnotfound.clara.ModuleBase;
import net.scottnotfound.clara.lang.ICommandReceiver;
import org.openscience.cdk.interfaces.IReaction;

import java.util.List;

public class ReactionModule extends ModuleBase implements ICommandReceiver, IModule {

    private static ReactionModule RM_INSTANCE;
    private static ReactionEngine RE_INSTANCE;
    private static ReactionBuilder RB_INSTANCE;

    public static ReactionModule getInstance() {
        if (RM_INSTANCE == null) {
            RM_INSTANCE = new ReactionModule();
        }
        return RM_INSTANCE;
    }

    private ReactionModule() {
        RE_INSTANCE = ReactionEngine.getInstance();
        RB_INSTANCE = ReactionBuilder.getInstance();
    }

    private void prepareReaction(List<String> reactants) {
        IReaction reaction = RB_INSTANCE.build(reactants);
    }

    private IReaction solveReaction(IReaction unsolvedReaction) {
        return RE_INSTANCE.solveReaction(unsolvedReaction);
    }


    @Override
    public void executeCommand() {

    }

    @Override
    public void refuseCommand() {

    }
}
