package net.scottnotfound.clara.reaction;

public class ReactionEngine {

    private static ReactionEngine RE_INSTANCE;
    private static ReactionBuilder RB_INSTANCE;

    public static ReactionEngine getInstance() {
        if (RE_INSTANCE == null) {
            RE_INSTANCE = new ReactionEngine();
        }
        return RE_INSTANCE;
    }

    private ReactionEngine() {
        RB_INSTANCE = ReactionBuilder.getInstance();
    }



}
