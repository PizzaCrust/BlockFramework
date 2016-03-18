package net.blockframe.map.model;

/**
 * Represents a class method mapping.
 */
public final class MethodMapping extends AbstractObjectMapping {
    public MethodMapping(String obfName, String deobfName) {
        setDeobfuscatedName(deobfName);
        setObfuscatedName(obfName);
    }
}
