package net.scottnotfound.clara.lang;

interface ICmdVisitor<R> {

    R visitCmd(Cmd.Help cmd);

}