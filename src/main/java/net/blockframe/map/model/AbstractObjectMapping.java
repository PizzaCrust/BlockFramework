package net.blockframe.map.model;

/**
 * Represents a object's mapping.
 */
public class AbstractObjectMapping {
    private String deobf = null;
    private String obf = null;

    public void setDeobfuscatedName(String deobf) {
            this.deobf = deobf;
    }

    public void setObfuscatedName(String obf) {
            this.obf = obf;
    }

    public String getObfuscatedName() {
        return obf;
    }

    public String getDeobfuscatedName() {
        return deobf;
    }
}
