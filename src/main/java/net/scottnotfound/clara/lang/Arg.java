package net.scottnotfound.clara.lang;

import java.util.List;

abstract class Arg {

    abstract <R> R accept(IArgVisitor<R> visitor);

    static class Argument extends Arg {
        Argument(String argument) {
            this.argument = argument;
        }

        @Override
        <R> R accept(IArgVisitor<R> visitor) {
            return visitor.visitArg(this);
        }

        final String argument;
    }

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
