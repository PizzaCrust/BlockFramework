package net.blockframe.map.model;

import java.util.ArrayList;

/**
 * Represents a class mapping.
 */
public final class ClassMapping extends AbstractObjectMapping {
    private final ArrayList<MethodMapping> methods = new ArrayList<MethodMapping>();
    private final ArrayList<FieldMapping> fields = new ArrayList<FieldMapping>();

    public ClassMapping(String obfName, String deobfName) {
        setDeobfuscatedName(deobfName);
        setObfuscatedName(obfName);
    }

    public ArrayList<FieldMapping> getFields() {
        return fields;
    }

    public ArrayList<MethodMapping> getMethods() {
        return methods;
    }
}
