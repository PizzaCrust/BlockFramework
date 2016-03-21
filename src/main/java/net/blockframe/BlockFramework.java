package net.blockframe;


import javassist.ClassClassPath;
import javassist.ClassPool;
import net.blockframe.internal.FrameworkTransformer;
import net.blockframe.internal.Injection;
import net.blockframe.map.MappingsRegistry;
import net.blockframe.map.model.ClassMapping;
import net.blockframe.map.model.MethodMapping;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import static net.blockframe.internal.Injection.PLUGINS_DIR;

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
        minecraftServer.getMethods().add(new MethodMapping("i", "createNewCommandManager"));
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

        //start commandhandler
        ClassMapping commandHandler = new ClassMapping("j", "net.minecraft.command.CommandHandler");
        //end commandhandler

        return new ClassMapping[]{
                minecraftServer,
                dedicatedServer,
                iCommandSender,
                commandException,
                textComponentString,
                iCommand,
                serverCommandManager,
                commandHandler,
        };
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        LOGGER.info("Checking for a plugins directory...");
        if (!PLUGINS_DIR.exists()) {
            LOGGER.info("Plugins directory isn't created, creating one.");
            PLUGINS_DIR.mkdir();
        }
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
        LOGGER.info("Searching for transformer plugins...");
        File[] transformerPlugins = PLUGINS_DIR.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".transformerplugin");
            }
        });
        LOGGER.info("Detected " + transformerPlugins.length + " transformer plugins.");
        LOGGER.info("Loading transformer plugins...");
        for (File transformerPlugin : transformerPlugins) {
            try {
                JarFile jarFile = new JarFile(transformerPlugin);
                Attributes attributes = jarFile.getManifest().getMainAttributes();
                String transformerClass = attributes.getValue("TransformerClass");
                if (transformerClass == null) {
                    throw new Exception();
                }
                URLClassLoader classLoader = new URLClassLoader(new URL[] { transformerPlugin.toURI().toURL() });
                Class theClass = classLoader.loadClass(transformerClass);
                Method blockFrameMethod = theClass.getDeclaredMethod("blockFrame", new Class[] {});
                blockFrameMethod.invoke(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Finished loading transformer plugins...");
    }
}
