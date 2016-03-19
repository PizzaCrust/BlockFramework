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
                method.insertAt(1041, "net.blockframe.internal.Injection.inject();"); // there is no bytecode on this line, so easy injection.
            }
        }
        dedicatedServer.toClass();
    }

    public void transformMinecraftServer() throws Exception {
        CtClass minecraftServer = BlockFramework.classPath.getCtClass("net.minecraft.server.MinecraftServer");
        for (CtMethod method : minecraftServer.getDeclaredMethods()) {
            if (method.getName().equals("getServerModName")) {
                method.setBody("return \"blockframe\";");
            }
        }
        return;
    }
}
