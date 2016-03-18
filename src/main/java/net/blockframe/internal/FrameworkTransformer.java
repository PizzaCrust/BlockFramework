package net.blockframe.internal;

import javassist.CtClass;
import javassist.CtMethod;
import net.blockframe.BlockFramework;
import net.blockframe.map.MappingsRegistry;

/**
 * Transforms classes for the framework.
 */
public class FrameworkTransformer {

    String dedicatedServerClass = MappingsRegistry.getClassMapping("net.minecraft.server.dedicated.DedicatedServer");

    public void transformDedicatedServer() throws Exception {
        CtClass dedicatedServer = BlockFramework.classPath.getCtClass(dedicatedServerClass);
        for (CtMethod method : dedicatedServer.getDeclaredMethods()) {
            String startServerMethod = MappingsRegistry.getMethodMapping("net.minecraft.server.dedicated.DedicatedServer", "startServer").getObfuscatedName();
            if (method.getName().equals(startServerMethod) && method.getReturnType() == CtClass.booleanType) {
                method.insertAt(1041, "System.out.println(\"Injected!\");"); // there is no bytecode on this line, so easy injection.
            }
        }
        dedicatedServer.toClass();
    }
}
