package net.scottnotfound.clara.lang;

import java.util.Map;

/**
 * The class implementing this will be sent commands
 */
public interface ICommandReceiver {

    void receiveCommand(Map<String, Object> commandMap);

    void refuseCommand(Map<String, Object> commandMap);

}
