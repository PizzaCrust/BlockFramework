package net.blockframe;


import javassist.ClassClassPath;
import javassist.ClassPool;
import net.blockframe.internal.FrameworkTransformer;
import net.blockframe.map.MappingsRegistry;
import net.blockframe.map.model.ClassMapping;
import net.blockframe.map.model.MethodMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the tweak of the framework, BlockFramework.
 */
public final class BlockFramework {
    public static final Logger LOGGER = LogManager.getLogger("BlockFramework");
    private final ArrayList<String> args = new ArrayList<String>();
    public static final ClassPool classPath = ClassPool.getDefault();

    public static ClassMapping[] getDefaultMappings() {
        //start server
        ClassMapping minecraftServer = new ClassMapping("net.minecraft.server.MinecraftServer", "net.minecraft.server.MinecraftServer");
        minecraftServer.getMethods().add(new MethodMapping("G", "getMinecraftVersion")); // no parameters
        // end server

        // start d-server
        ClassMapping dedicatedServer = new ClassMapping("la", "net.minecraft.server.dedicated.DedicatedServer");
        dedicatedServer.getMethods().add(new MethodMapping("j", "startServer"));
        // end d-server

        // start icommandsender
        ClassMapping iCommandSender = new ClassMapping("m", "net.minecraft.command.ICommandSender");
        iCommandSender.getMethods().add(new MethodMapping("a", "addChatMessage")); //text component
        // end icommandsender

        // start commandexception
        ClassMapping commandException = new ClassMapping("bz", "net.minecraft.command.CommandException");
        // end commandexception

        // start textcomponentstring (constructor: string)
        ClassMapping textComponentString = new ClassMapping("fa", "net.minecraft.util.text.TextComponentString");
        // end textcomponentstring

        // start icommand
        ClassMapping iCommand = new ClassMapping("k", "net.minecraft.command.ICommand");
        iCommand.getMethods().add(new MethodMapping("c", "getCommandName")); // string
        iCommand.getMethods().add(new MethodMapping("b", "getCommandUsage")); //string
        iCommand.getMethods().add(new MethodMapping("b", "getCommandAliases")); // arraylist<string>
        iCommand.getMethods().add(new MethodMapping("a", "execute")); // minecraftserver, icommandsender, string[] args
        iCommand.getMethods().add(new MethodMapping("a", "checkPermission")); // minecraftserver, icommandsender
        iCommand.getMethods().add(new MethodMapping("a", "getTabCompletionOptions"));  // minecraftserver, icommandsender, string[] args, blockpos
        iCommand.getMethods().add(new MethodMapping("b", "isUsernameIndex")); // string[] args, int index
        // end icommand

        // start servercommandmanager
        ClassMapping serverCommandManager = new ClassMapping("bc", "net.minecraft.command.ServerCommandManager");
        serverCommandManager.getMethods().add(new MethodMapping("a", "registerCommand")); //icommand
        // end servercommandmanager

        return new ClassMapping[]{
                minecraftServer,
                dedicatedServer,
                iCommandSender,
                commandException,
                textComponentString,
                iCommand,
                serverCommandManager,
        };
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        LOGGER.info("Registering needed obfuscation mappings to registry...");
        MappingsRegistry.addClassMappings(getDefaultMappings());
        FrameworkTransformer transformer = new FrameworkTransformer();
        LOGGER.info("Transforming DedicatedServer...");
        try {
            transformer.transformDedicatedServer();
        } catch (Exception e) {
            LOGGER.error("Failed to transform DedicatedServer!");
            e.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("Transforming ServerCommandManager...");
        try {
            transformer.transformServerCommandManager();
        } catch (Exception e) {
            LOGGER.error("Failed to transform ServerCommandManager!");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
