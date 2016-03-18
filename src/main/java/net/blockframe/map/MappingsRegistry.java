package net.blockframe.map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import net.blockframe.BlockFramework;
import net.blockframe.map.model.ClassMapping;
import net.blockframe.map.model.MethodMapping;

import java.util.ArrayList;

/**
 * A registry for obfuscation to de-obfuscated or de-obfuscated to obfuscation for classes/methods/fields.
 */
public final class MappingsRegistry {
    private static final ArrayList<ClassMapping> classes = new ArrayList<ClassMapping>();
    private static ClassPool classPath = ClassPool.getDefault();

    public static void addClassMappings(ClassMapping... classMappings) {
       for (ClassMapping classMapping : classMappings) {
           BlockFramework.LOGGER.info("Registering class " + classMapping.getDeobfuscatedName() + " (" + classMapping.getObfuscatedName() + ")...");
           classes.add(classMapping);
       }
    }

    public static String getClassMapping(String deobfuscatedName) {
        for (ClassMapping mapping : classes) {
            if (mapping.getDeobfuscatedName().equals(deobfuscatedName)) {
                return mapping.getObfuscatedName();
            }
        }
        return null;
    }

    public static CtClass getCtClass(String deobfuscatedName) {
        String obfName = getClassMapping(deobfuscatedName);
        try {
            return classPath.getCtClass(obfName);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public static MethodMapping[] getMethodMappings(String deobfClassName) {
        for (ClassMapping classMapping : classes) {
            if (classMapping.getDeobfuscatedName().equals(deobfClassName)) {
                return classMapping.getMethods().toArray(new MethodMapping[classMapping.getMethods().size()]);
            }
        }
        return null;
    }

    public static MethodMapping getMethodMapping(String deobfClassName, String deobfMethodName) {
        for (MethodMapping methodMapping : getMethodMappings(deobfClassName)) {
            if (methodMapping.getDeobfuscatedName().equals(deobfMethodName)) {
                return methodMapping;
            }
        }
        return null;
    }
}
