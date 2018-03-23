package net.scottnotfound.clara.lang;

import java.util.List;

/**
 * This class provides a representation for arguments passed to commands. An argument is considered
 * anything that comes after the command token until the statement is terminated by a special character.
 */
abstract class Arg {

    /**
     * Call to delegate sction to the specific visit method.
     */
    abstract <R> R accept(IArgVisitor<R> visitor);

    /**
     * Used when the argument is just an argument.
     */
    static class Argument extends Arg {
        Argument(Expr expr) {
            this.expr = expr;
        }

        @Override
        <R> R accept(IArgVisitor<R> visitor) {
            return visitor.visitArg(this);
        }

        final Expr expr;
    }

    /**
     * Used when the argument is a flag or a collection of flags.
     */
    static class Flag extends Arg {
        Flag(String flags) {
            this.flags = flags;
        }

        @Override
        <R> R accept(IArgVisitor<R> visitor) {
            return visitor.visitArg(this);
        }

        final String flags;
    }

    /**
     * Used when the argument is a parameter that takes its own arguments. Ends when a flag or
     * new parameter is encountered or the statement is terminated.
     */
    static class Parameter extends Arg {
        Parameter(String parameter, List<Argument> arguments) {
            this.parameter = parameter;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(IArgVisitor<R> visitor) {
            return visitor.visitArg(this);
        }

        final String parameter;
        final List<Argument> arguments;
    }


}
