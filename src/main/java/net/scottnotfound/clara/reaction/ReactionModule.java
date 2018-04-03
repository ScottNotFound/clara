package net.scottnotfound.clara.reaction;

import net.scottnotfound.clara.IModule;
import net.scottnotfound.clara.ModuleBase;
import net.scottnotfound.clara.ModuleRegistry;
import net.scottnotfound.clara.RegisterModule;
import net.scottnotfound.clara.lang.ICommandReceiver;
import org.openscience.cdk.interfaces.IReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactionModule extends ModuleBase implements ICommandReceiver, IModule {

    /* Singleton instances */
    private static ReactionModule RM_INSTANCE;
    private static ReactionEngine RE_INSTANCE;
    private static ReactionBuilder RB_INSTANCE;

    /* flags */


    /* reaction being operated on */
    private IReaction reaction = null;

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

    @RegisterModule(moduleName = "Reaction")
    public static void registerModule() {
        ModuleRegistry.registerModule("Reaction", getInstance());
    }

    private IReaction prepareReaction(List<String> reactants) {
        return RB_INSTANCE.build(reactants);
    }

    private IReaction solveReaction(IReaction unsolvedReaction) {
        return RE_INSTANCE.solveReaction(unsolvedReaction);
    }

    private void setFlags(String flagSequence) {

    }

    private void handleReactContent(List<String> reactants) {
        reaction = prepareReaction(reactants);
        reaction = solveReaction(reaction);
    }

    @Override
    public void executeCommand(Map<String, Object> commandMap) {

        for (Map.Entry<String,Object> entry : commandMap.entrySet()) {

            String descriptor = entry.getKey();
            Object object = entry.getValue();

            if (descriptor.contains("flag")) {

                if (object instanceof String) {
                    String flagSequence = (String) object;
                    setFlags(flagSequence);
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

                            List<String> reactants = new ArrayList<>();
                            for (Object item : list) {
                                reactants.add((String) item);
                            }

                            handleReactContent(reactants);

                        } else {
                            continue;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void refuseCommand(Map<String, Object> commandMap) {

    }
}
