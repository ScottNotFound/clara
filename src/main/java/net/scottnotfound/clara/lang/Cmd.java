package net.scottnotfound.clara.lang;


/**
 * This class provides a representation for various commands that may be found.
 * accept() should be called when you wish to do something with a command. What is
 * done with the command is determined by the implementations of ICmdVisitor interface.
 * The different command types are constructed by the parser based on the tokens.
 */
abstract class Cmd {


    abstract <R> R accept(ICmdVisitor<R> visitor);


    static class Help extends Cmd {
        Help() {

        }

        <R> R accept(ICmdVisitor<R> visitor) {
            return visitor.visitCmd(this);
        }


    }

}
