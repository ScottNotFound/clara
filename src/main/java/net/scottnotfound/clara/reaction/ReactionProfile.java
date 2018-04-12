package net.scottnotfound.clara.reaction;

import org.openscience.cdk.interfaces.IReaction;

public class ReactionProfile {

    private IReaction reaction;


    ReactionProfile() {

    }

    ReactionProfile(IReaction reaction) {
        this.reaction = reaction;
    }

    public IReaction getReaction() {
        return reaction;
    }


}
