package net.scottnotfound.clara.reaction;

import org.openscience.cdk.interfaces.IReaction;

public interface IReactionProfile {

    IReaction getReaction();

    void setReaction(IReaction reaction);

    void setFlags(String flagSequence);


    boolean isPrintFlag();


    boolean isSolved();

}
