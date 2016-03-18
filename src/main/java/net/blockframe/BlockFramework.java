package net.blockframe;


import javassist.ClassClassPath;
import javassist.ClassPool;
import net.blockframe.internal.FrameworkTransformer;
import net.blockframe.map.MappingsRegistry;
import net.blockframe.map.model.ClassMapping;
import net.blockframe.map.model.MethodMapping;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the tweak of the framework, BlockFramework.
 */
public final class BlockFramework implements ITweaker{
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

        return new ClassMapping[]{
                minecraftServer,
                dedicatedServer,
        };
    }

    @Override
    public void acceptOptions(List<String> inArgs, File gameDir, File assetsDir, String profile) {
        LOGGER.info("Processing arguments...");
        this.args.clear();
        for (Iterator<String> it = inArgs.iterator(); it.hasNext();) {
            String arg = it.next();
            args.add(arg);
        }
        if (profile != null) {
            this.args.add("--version");
            this.args.add(profile);
        }
        if (assetsDir != null) {
            this.args.add("--assetsDir");
            this.args.add(assetsDir.getPath());
        }
        if (gameDir == null) gameDir = new File(".");
        LOGGER.info("Registering needed obfuscation mappings to registry...");
        MappingsRegistry.addClassMappings(getDefaultMappings());
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        FrameworkTransformer transformer = new FrameworkTransformer();
        LOGGER.info("Transforming DedicatedServer...");
        try {
            transformer.transformDedicatedServer();
        } catch (Exception e) {
            LOGGER.error("Failed to transform DedicatedServer!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(new String[args.size()]);
    }
}
