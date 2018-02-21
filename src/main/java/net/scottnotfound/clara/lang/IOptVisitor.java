package net.scottnotfound.clara.lang;

public interface IOptVisitor<R> {

    R visitOpt(Opt.Argument opt);

    R visitOpt(Opt.Arguments opt);

    R visitOpt(Opt.Assign opt);

    R visitOpt(Opt.Flag opt);

    R visitOpt(Opt.Parameter opt);

}
