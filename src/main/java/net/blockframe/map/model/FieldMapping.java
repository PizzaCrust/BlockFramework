package net.blockframe.map.model;

/**
 * Represents a class field mapping.
 */
public final class FieldMapping extends AbstractObjectMapping {
    public FieldMapping(String obfName, String deobfName) {
        setDeobfuscatedName(deobfName);
        setObfuscatedName(obfName);
    }

}
