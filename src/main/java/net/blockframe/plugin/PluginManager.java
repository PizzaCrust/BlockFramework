package net.blockframe.plugin;

import net.blockframe.BlockFramework;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the plugin manager for loading plugins.
 */
public class PluginManager {
    private URLClassLoader pluginClassLoader;
    private HashMap<File, String> jarUrls = new HashMap<File, String>();
    private Logger LOGGER = BlockFramework.LOGGER;

    private PluginManager(HashMap<File, String> jarUrls, URLClassLoader classLoader) { this.jarUrls = jarUrls; this.pluginClassLoader = classLoader; }

    public void init() throws Exception {
        for (Map.Entry<File, String> entry : jarUrls.entrySet()) {
            LOGGER.info("Loading file " + entry.getKey().getName() + "...");
            Class mainClass = pluginClassLoader.loadClass(entry.getValue());
            Method blockFrameMethod = mainClass.getDeclaredMethod("blockFrame", new Class[]{});
            blockFrameMethod.invoke(null, null); // public static void blockFrame
        }
    }

    public static class Builder {
        private HashMap<File, String> jarUrls = new HashMap<File, String>();
        private ArrayList<URL> urls = new ArrayList<URL>();

        public Builder(){}

        public void addJarFile(String mainClass, File jar) throws Exception {
            if (!jar.getName().endsWith(".jar")) {
                return;
            }
            urls.add(jar.toURI().toURL());
            jarUrls.put(jar, mainClass);
        }

        public PluginManager retrieve() {
            return new PluginManager(jarUrls, new URLClassLoader(urls.toArray(new URL[urls.size()])));
        }
    }
}
