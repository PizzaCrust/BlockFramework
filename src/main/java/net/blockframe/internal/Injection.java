package net.blockframe.internal;

import net.blockframe.BlockFramework;
import net.blockframe.map.MappingsRegistry;
import net.blockframe.plugin.PluginManager;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * Represents the injection to the server.
 */
public class Injection {
    public static final File PLUGINS_DIR = new File(System.getProperty("user.dir"), "plugins");
    public static MinecraftServer server;

    public static Object commandHandlerInstance;

    public static void inject(MinecraftServer server) {
        Injection.server = server;
        Logger LOGGER = BlockFramework.LOGGER;
        LOGGER.info("Injected to the Minecraft server!");
        LOGGER.info("Retrieving command manager instance...");
        try {
            Object commandManagerInstance = server.N();
            Class commandHandler = Class.forName(MappingsRegistry.getClassMapping("net.minecraft.command.CommandHandler"));
            commandHandlerInstance = commandHandler.cast(commandManagerInstance);
        } catch (Exception e) {
            LOGGER.error("Could not retrieve command manager instance!");
            e.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("Detecting plugin files...");
        File[] jarFiles = PLUGINS_DIR.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        LOGGER.info("Detected " + jarFiles.length + " plugin files!");
        HashMap<File, String> jarFileToMainClass = new HashMap<File, String>();
        for (File jarFile : jarFiles) {
            try {
                JarFile jarFileObj = new JarFile(jarFile);
                Attributes attributes = jarFileObj.getManifest().getMainAttributes();
                String mainClass = attributes.getValue("PluginClass");
                if (mainClass == null) {
                    throw new Exception();
                }
                jarFileToMainClass.put(jarFile, mainClass);
            } catch (Exception e) {
                LOGGER.error("Could not load " + jarFile.getName() + "!");
                e.printStackTrace();
            }
        }
        LOGGER.info("Starting plugin manager...");
        PluginManager.Builder builder = new PluginManager.Builder();
        for (Map.Entry<File, String> entry : jarFileToMainClass.entrySet()) {
            try {
                builder.addJarFile(entry.getValue(), entry.getKey());
            } catch (Exception e) {
                LOGGER.error("Could not load " + entry.getKey().getName() + "!");
                e.printStackTrace();
            }
        }
        PluginManager pluginManager = builder.retrieve();
        LOGGER.info("Loading plugin files...");
        try {
            pluginManager.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Injection to Minecraft server is completed.");
    }
}
