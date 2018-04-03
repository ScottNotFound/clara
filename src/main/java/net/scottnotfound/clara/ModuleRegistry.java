package net.scottnotfound.clara;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModuleRegistry {

    private static final Map<String,IModule> registeredModules = new HashMap<>();

    private ModuleRegistry() {

    }

    public static Map<String, IModule> getModuleRegistry() {
        return Collections.unmodifiableMap(registeredModules);
    }

    public static void registerModule(String moduleName, IModule module) {
        registeredModules.put(moduleName, module);
    }


}
