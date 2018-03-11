package net.scottnotfound.clara.lang;

import java.util.List;

/**
 * This class provides a representation for various commands that may be found.
 * accept() should be called when you wish to do something with a command. What is
 * done with the command is determined by the implementations of ICmdVisitor interface.
 * The different command types are constructed by the parser based on the tokens.
 */
abstract class Cmd {

    /**
     * Call to delegate action to the specific visit method.
     */
    abstract <R> R accept(ICmdVisitor<R> visitor);


    /**
     * Used when the command is a reaction command.
     */
    static class Default extends Cmd {
        Default(Token command, List<Arg> args) {
            this.command = command;
            this.args = args;
        }

        @Override
        <R> R accept(ICmdVisitor<R> visitor) {
            return visitor.visitCmd(this);
        }

        final Token command;
        final List<Arg> args;
    }

    /**
     * Used when the command is the exit command.
     */
    static class Exit extends Cmd {
        Exit() {

        }

        <R> R accept(ICmdVisitor<R> visitor) {
            return visitor.visitCmd(this);
        }
    }

    /**
     * Used when the command is a help command.
     */
    static class Help extends Cmd {
        Help(Token command) {
            this.command = command;
        }

        @Override
        <R> R accept(ICmdVisitor<R> visitor) {
            return visitor.visitCmd(this);
        }

        final Token command;
    }


}
