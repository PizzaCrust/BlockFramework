package net.blockframe;

import javassist.CtClass;
import javassist.CtMethod;
import net.blockframe.map.MappingsRegistry;
import net.blockframe.map.model.ClassMapping;
import net.blockframe.map.model.MethodMapping;
import net.blockframe.util.Logger;

import java.lang.instrument.Instrumentation;

/**
 * Represents the entry point of the Java agent, BlockFramework.
 */
public final class BlockFramework {

    public static ClassMapping[] getDefaultMappings() {
        //start server
        ClassMapping minecraftServer = new ClassMapping("net.minecraft.server.MinecraftServer", "net.minecraft.server.MinecraftServer");
        minecraftServer.getMethods().add(new MethodMapping("G", "getMinecraftVersion")); // no parameters
        // end server

        // start d-server
        ClassMapping dedicatedServer = new ClassMapping("la", "net.minecraft.server.dedicated.DedicatedServer");
        dedicatedServer.getMethods().add(new MethodMapping("j", "startServer"));
        // end d-server

        return new ClassMapping[]{
                minecraftServer,
                dedicatedServer,
        };
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        Logger.info("Registering required framework mappings to BlockFramework...");
        MappingsRegistry.addClassMappings(getDefaultMappings());
        CtClass serverClass = MappingsRegistry.getCtClass("net.minecraft.server.DedicatedServer");
        try {
            for (CtMethod method : serverClass.getDeclaredMethods()) {
                if (method.getName().equals(MappingsRegistry.getMethodMapping("net.minecraft.server.DedicatedServer", "startServer")) && method.getReturnType() == CtClass.booleanType) {
                    System.out.println("found startserver method");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
