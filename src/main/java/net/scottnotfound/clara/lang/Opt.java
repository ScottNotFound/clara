package net.scottnotfound.clara.lang;

import java.util.Arrays;
import java.util.List;

/**
 * This class provides a representation for various command options that may be found.
 * accept() should be called when you wish to do something with a command option. What is
 * done with the command option is determined by the implementations of ICmdVisitor interface.
 * The different command option types are constructed by the parser based on the tokens.
 */
abstract class Opt {

    /**
     * Call to delegate action to the specific visit method.
     */
    abstract <R> R accept(IOptVisitor<R> visitor);


    /**
     * Used when option is an argument for a parameter.
     */
    static class Argument extends Opt {
        Argument(Token token) {
            this.token = token;
        }

        @Override
        <R> R accept(IOptVisitor<R> visitor) {
            return visitor.visitOpt(this);
        }

        final Token token;
    }

    /**
     * Used when there are multiple arguments for a parameter.
     */
    static class Arguments extends Opt {
        Arguments(Token... tokens) {
            this.tokens = Arrays.asList(tokens);
        }

        @Override
        <R> R accept(IOptVisitor<R> visitor) {
            return visitor.visitOpt(this);
        }

        final List<Token> tokens;
    }

    /**
     * Used when option is an assignment.
     */
    static class Assign extends Opt {
        Assign(Parameter parameter, Argument argument) {
            this.parameter = parameter;
            this.argument = argument;
        }

        @Override
        <R> R accept(IOptVisitor<R> visitor) {
            return visitor.visitOpt(this);
        }

        final Parameter parameter;
        final Argument argument;
    }

    /**
     * Used when option is a standalone flag.
     */
    static class Flag extends Opt {
        Flag(Token token) {
            this.token = token;
        }

        @Override
        <R> R accept(IOptVisitor<R> visitor) {
            return visitor.visitOpt(this);
        }

        final Token token;
    }

    /**
     * Used when option is a parameter that requires an argument or multiple arguments.
     */
    static class Parameter extends Opt {
        Parameter(Token... tokens) {
            this.tokens = Arrays.asList(tokens);
        }

        @Override
        <R> R accept(IOptVisitor<R> visitor) {
            return visitor.visitOpt(this);
        }

        final List<Token> tokens;
    }


}
