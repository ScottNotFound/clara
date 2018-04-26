package net.scottnotfound.clara.reaction;

import org.openscience.cdk.interfaces.IReaction;

public interface IReactionProfile extends Cloneable {

    /** Force the implementing class to implement clone(). */
    IReactionProfile clone();

    /** Should get a reference to a reaction. */
    IReaction getReaction();

    /** Change the reaction to the given reaction. */
    void setReaction(IReaction reaction);

    /** Sets the sequence of flags to true. */
    void setFlags(char... cs);

    /** Sets the flag to true. */
    void setFlag(char c);

    /** Removes the flag or sets flag to false. */
    void removeFlag(char c);

    /** Sets flag to true or false. */
    void setFlag(char c, boolean b);

    /** Toggles the state fo the flag. */
    void toggleFlag(char c);

    boolean getFlag(char c);

    String getFlags();

    boolean isSolved();

}
