package net.blockframe.map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.blockframe.map.model.ClassMapping;
import net.blockframe.map.model.MethodMapping;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A registry for obfuscation to de-obfuscated or de-obfuscated to obfuscation for classes/methods/fields.
 */
public final class MappingsRegistry {
    private static final ArrayList<ClassMapping> classes = new ArrayList<ClassMapping>();
    private static ClassPool classPath = ClassPool.getDefault();

    public static void addClassMappings(ClassMapping... classMappings) {
        Collections.addAll(classes, classMappings);
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
            if (methodMapping.getDeobfuscatedName().equals(deobfClassName)) {
                return methodMapping;
            }
        }
        return null;
    }
}
