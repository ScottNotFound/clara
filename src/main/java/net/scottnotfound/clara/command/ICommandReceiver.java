package net.scottnotfound.clara.command;

/**
 * The class implementing this will be sent commands
 */
public interface ICommandReceiver {

    void executeCommand();

    void refuseCommand();

}
