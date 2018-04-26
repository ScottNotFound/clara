package net.scottnotfound.clara.reaction;

import net.scottnotfound.clara.IModule;
import net.scottnotfound.clara.ModuleBase;
import net.scottnotfound.clara.ModuleRegistry;
import net.scottnotfound.clara.RegisterModule;
import net.scottnotfound.clara.lang.ICommandReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactionModule extends ModuleBase implements ICommandReceiver, IModule {

    /* Singleton instances */
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

    @RegisterModule(moduleName = "Reaction")
    public static void registerModule() {
        ModuleRegistry.registerModule("Reaction", getInstance());
    }

    private void solveReaction(IReactionProfile profile) {
        profile.setReaction(RE_INSTANCE.solveReaction(profile.getReaction()));
    }

    private void handleReactContent(List<String> reactants, List<String> agents, String flagSequence) {
        ReactionProfileBuilder builder = new ReactionProfileBuilder(flagSequence);
        IReactionProfile profile = builder
                .addReactants(reactants)
                .addAgents(agents)
                .buildProfile();

        solveReaction(profile);
    }

    @Override
    public void receiveCommand(Map<String, Object> commandMap) {

        String flagSequence = "";
        List<String> reactants = new ArrayList<>();
        List<String> agents = new ArrayList<>();

        for (Map.Entry<String,Object> entry : commandMap.entrySet()) {

            String descriptor = entry.getKey();
            Object object = entry.getValue();

            if (descriptor.contains("flag")) {

                if (object instanceof String) {
                    flagSequence = (String) object;
                } else {
                    // this should be impossible
                    continue;
                }

            }

            if (descriptor.contains("react")) {

                if (object instanceof List<?>) {

                    List list = (List<?>) object;

                    if (list.isEmpty()) {
                        continue;
                    } else {

                        if (list.toArray()[0] instanceof String) {

                            for (Object item : list) {
                                reactants.add((String) item);
                            }

                        } else {
                            continue;
                        }
                    }
                }
            }

            if (descriptor.contains("agent")) {

                //

            }
        }

        handleReactContent(reactants, agents, flagSequence);

    }

    @Override
    public void refuseCommand(Map<String, Object> commandMap) {

    }
}
