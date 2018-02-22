package net.scottnotfound.clara.lang;

interface ICmdVisitor<R> {

    R visitCmd(Cmd.Default cmd);

    R visitCmd(Cmd.Exit cmd);

    R visitCmd(Cmd.Help cmd);

}
