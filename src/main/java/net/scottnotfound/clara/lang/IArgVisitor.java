package net.scottnotfound.clara.lang;

public interface IArgVisitor<R> {

    R visitArg(Arg.Argument arg);

    R visitArg(Arg.Flag arg);

    R visitArg(Arg.Parameter arg);

}
