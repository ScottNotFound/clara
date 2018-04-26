package net.scottnotfound.clara.lang;

import net.scottnotfound.clara.IModule;
import net.scottnotfound.clara.ModuleRegistry;

import java.util.Map;

class CommandDistributor {

    CommandDistributor() {

    }

    void distributeCommand(Map<String, Object> commandMap) {
        Map<String,IModule> registry = ModuleRegistry.getModuleRegistry();
        for (Map.Entry<String,IModule> entry : registry.entrySet()) {
            String moduleName = entry.getKey();
            IModule module = entry.getValue();
            if (module instanceof ICommandReceiver) {
                ICommandReceiver commandReceiver = (ICommandReceiver) module;
                commandReceiver.receiveCommand(commandMap);
            }
        }
    }

    Object haltCommand(Cmd command) {

        return null;
    }
}
