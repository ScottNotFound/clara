package net.scottnotfound.clara.reaction;

import org.openscience.cdk.interfaces.IReaction;

class ReactionEngine {

    private static ReactionEngine RE_INSTANCE;


    static ReactionEngine getInstance() {
        if (RE_INSTANCE == null) {
            RE_INSTANCE = new ReactionEngine();
        }
        return RE_INSTANCE;
    }

    private ReactionEngine() {

    }

    IReaction solveReaction(IReaction unsolvedReaction) {

        return unsolvedReaction;
    }

}
