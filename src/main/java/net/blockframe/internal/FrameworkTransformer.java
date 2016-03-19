package net.blockframe.internal;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import net.blockframe.BlockFramework;
import net.blockframe.map.MappingsRegistry;

/**
 * Transforms classes for the framework.
 */
public class FrameworkTransformer {

    String dedicatedServerClass = MappingsRegistry.getClassMapping("net.minecraft.server.dedicated.DedicatedServer");
    String serverCommandManagerClass = MappingsRegistry.getClassMapping("net.minecraft.command.ServerCommandManager");

    public void transformDedicatedServer() throws Exception {
        CtClass dedicatedServer = BlockFramework.classPath.getCtClass(dedicatedServerClass);
        for (CtMethod method : dedicatedServer.getDeclaredMethods()) {
            String startServerMethod = MappingsRegistry.getMethodMapping("net.minecraft.server.dedicated.DedicatedServer", "startServer").getObfuscatedName();
            if (method.getName().equals(startServerMethod) && method.getReturnType() == CtClass.booleanType) {
                method.insertAt(1041, "net.blockframe.internal.Injection.inject(this);"); // there is no bytecode on this line, so easy injection.
            }
        }
        dedicatedServer.toClass();
    }

    public void transformServerCommandManager() throws Exception {
        CtClass serverCommandManager = BlockFramework.classPath.getCtClass(serverCommandManagerClass);
        createAboutCommand();
        String registerCommandMethod = MappingsRegistry.getMethodMapping("net.minecraft.command.ServerCommandManager", "registerCommand").getObfuscatedName();
        String iCommand = MappingsRegistry.getClassMapping("net.minecraft.command.ICommand");
        CtConstructor ctConstructor = serverCommandManager.getDeclaredConstructor(new CtClass[]{ BlockFramework.classPath.getCtClass("net.minecraft.server.MinecraftServer") });
        ctConstructor.insertAt(30, "this." + registerCommandMethod + "(new net.minecraft.command.AboutCommand());");
        serverCommandManager.toClass();
    }

    // returns about class name
    private String createAboutCommand() throws Exception {
        CtClass aboutCommand = BlockFramework.classPath.makeClass("net.minecraft.command.AboutCommand");

        String getCommandName = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "getCommandName").getObfuscatedName();
        aboutCommand.addMethod(CtNewMethod.make("public java.lang.String " + getCommandName + "() { return \"about\"; }", aboutCommand));

        String getCommandUsage = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "getCommandUsage").getObfuscatedName();
        aboutCommand.addMethod(CtNewMethod.make("public java.lang.String " + getCommandUsage + "() { return \"about\"; }", aboutCommand));

        String getCommandAliases = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "getCommandAliases").getObfuscatedName();
        aboutCommand.addMethod(CtNewMethod.make("public java.util.List " + getCommandAliases + "() { java.util.ArrayList aliases = new java.util.ArrayList(); aliases.add(\"abt\"); return aliases; }", aboutCommand));

        String cmdMessagePlayer = '\u00a7' + "9This server is currently using: " + '\u00a7' + "cBlockFramework 1.9.1-SNAPSHOT";
        String execute = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "execute").getObfuscatedName();
        String addChatMessage = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommandSender", "addChatMessage").getObfuscatedName();
        String iCommandSender = MappingsRegistry.getClassMapping("net.minecraft.command.ICommandSender");
        String chatComponentText = MappingsRegistry.getClassMapping("net.minecraft.util.text.TextComponentString");
        aboutCommand.addMethod(CtNewMethod.make("public void " + execute + "(net.minecraft.server.MinecraftServer server, " + iCommandSender + " sender, java.lang.String[] args) { sender." + addChatMessage + "(new " + chatComponentText + "(\""  + cmdMessagePlayer + "\")); }", aboutCommand));

        String checkPermission = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "checkPermission").getObfuscatedName();
        aboutCommand.addMethod(CtNewMethod.make("public boolean " + checkPermission + "(net.minecraft.server.MinecraftServer server, " + iCommandSender + " sender) { return true; }", aboutCommand));

        String getTabCompletionOptions = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "getTabCompletionOptions").getObfuscatedName();
        aboutCommand.addMethod(CtNewMethod.make("public java.util.List " + getTabCompletionOptions + "(net.minecraft.server.MinecraftServer server, " + iCommandSender + " sender, java.lang.String[] args) { return null; }", aboutCommand));

        String isUsernameIndex = MappingsRegistry.getMethodMapping("net.minecraft.command.ICommand", "isUsernameIndex").getObfuscatedName();
        aboutCommand.addMethod(CtNewMethod.make("public boolean " + isUsernameIndex + "(java.lang.String[] args, int index){ return false; }", aboutCommand));

        String iCommand = MappingsRegistry.getClassMapping("net.minecraft.command.ICommand");
        aboutCommand.addMethod(CtNewMethod.make("public int compareTo(" + iCommand + " o){ return 0; }", aboutCommand));

        aboutCommand.setInterfaces(new CtClass[]{BlockFramework.classPath.getCtClass(iCommand)});

        aboutCommand.toClass();
        return "net.minecraft.command.AboutCommand";
    }
}
