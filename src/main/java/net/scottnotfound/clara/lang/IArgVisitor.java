package net.scottnotfound.clara.lang;

public interface IArgVisitor<R> {

    /**
     * Used when the argument is just an argument.
     */
    R visitArg(Arg.Argument arg);

    /**
     * Used when the argument is a flag or a collection of flags.
     */
    R visitArg(Arg.Flag arg);

    /**
     * Used when the argument is a parameter that takes its own arguments. Ends when a flag or
     * new parameter is encountered or the statement is terminated.
     */
    R visitArg(Arg.Parameter arg);

}
