package net.scottnotfound.clara.lang;

interface ICmdVisitor<R> {

    R visitCmd(Cmd.Assign cmd);

    R visitCmd(Cmd.Flag cmd);

    R visitCmd(Cmd.Help cmd);

}
