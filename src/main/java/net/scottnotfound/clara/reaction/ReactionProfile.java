package net.scottnotfound.clara.reaction;

import org.openscience.cdk.interfaces.IReaction;

import java.util.HashMap;
import java.util.Map;

class ReactionProfile implements IReactionProfile {

    private IReaction reaction;

    /* flags */
    private Map<String,Boolean> flags = new HashMap<>();

    private boolean solved = false;


    ReactionProfile() {
        this.reaction = null;
    }

    ReactionProfile(IReaction reaction) {
        this.reaction = reaction;
    }

    ReactionProfile(IReaction reaction, String flagSequence) {
        this.reaction = reaction;
        setFlags(flagSequence);
    }

    @Override
    public IReaction getReaction() {
        return reaction;
    }

    @Override
    public void setReaction(IReaction reaction) {
        this.reaction = reaction;
    }

    @Override
    public void setFlags(String flagSequence) {
        for (char f : flagSequence.toCharArray()) {
            switch (f) {
                case ('p') : {
                    flags.put("print", true);
                }
            }
        }
    }


    @Override
    public boolean isPrintFlag() {
        return flags.getOrDefault("print", false);
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    public void setPrintFlag(boolean b) {
        if (flags.containsKey("print")) {
            flags.replace("print", b);
        } else {
            flags.put("print", b);
        }
        //flags.put("print", b);
    }
}
