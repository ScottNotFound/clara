package net.scottnotfound.clara.lang;

interface ICmdVisitor<R> {

    /**
     * Used when the command is a reaction command.
     */
    R visitCmd(Cmd.Default cmd);

    /**
     * Used when the command is the exit command.
     */
    R visitCmd(Cmd.Exit cmd);

    /**
     * Used when the command is a help command.
     */
    R visitCmd(Cmd.Help cmd);

}
