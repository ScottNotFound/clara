package net.scottnotfound.clara.reaction;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IReaction;

public class ReactionProfile implements IReactionProfile {

    private IReaction reaction;

    private boolean solved = false;
    private String flags;


    ReactionProfile() {
        this.reaction = new Reaction();
        this.flags = "";
    }

    ReactionProfile(IReaction reaction) {
        this.reaction = reaction;
    }

    ReactionProfile(String flags) {
        this.reaction = new Reaction();
        this.flags = flags;
    }

    ReactionProfile(IReaction reaction, String flags) {
        this.reaction = reaction;
        this.flags = flags;
    }

    @Override
    public IReactionProfile clone() {
        IReactionProfile reactionProfile = null;
        try {
            reactionProfile = (IReactionProfile) super.clone();
            reactionProfile.setReaction((IReaction) this.reaction.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return reactionProfile;
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
    public void setFlags(char... cs) {
        for (char c : cs) {
            setFlag(c);
        }
    }

    @Override
    public void setFlag(char c) {
        int i = flags.indexOf(c);
        flags = (i == -1)
                ? flags + c
                : flags;
    }

    @Override
    public void removeFlag(char c) {
        int i = flags.indexOf(c);
        flags = (i == -1)
                ? flags
                : new StringBuilder(flags).deleteCharAt(i).toString();
    }

    @Override
    public void setFlag(char c, boolean b) {
        int i = flags.indexOf(c);
        flags = (i == -1)
                ? b
                  ? flags + c
                  : flags
                : b
                  ? flags
                  : new StringBuilder(flags).deleteCharAt(i).toString();
    }

    @Override
    public void toggleFlag(char c) {
        int i = flags.indexOf(c);
        flags = (i == -1)
                ? flags + c
                : new StringBuilder(flags).deleteCharAt(i).toString();
    }

    @Override
    public boolean getFlag(char c) {
        return flags.indexOf(c) != -1;
    }

    @Override
    public String getFlags() {
        return flags;
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

}
