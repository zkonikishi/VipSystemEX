package me.zhanshi123.vipsystem;

import me.zhanshi123.vipsystem.api.VipSystemAPI;
import me.zhanshi123.vipsystem.command.CommandHandler;
import me.zhanshi123.vipsystem.convert.ConvertManager;
import me.zhanshi123.vipsystem.custom.CustomManager;
import me.zhanshi123.vipsystem.customcommand.CustomCommandManager;
import me.zhanshi123.vipsystem.data.Cache;
import me.zhanshi123.vipsystem.data.Database;
import me.zhanshi123.vipsystem.manager.ConfigManager;
import me.zhanshi123.vipsystem.manager.MessageManager;
import me.zhanshi123.vipsystem.manager.UpdateManager;
import me.zhanshi123.vipsystem.metrics.CStats;
import me.zhanshi123.vipsystem.metrics.Metrics;
import me.zhanshi123.vipsystem.script.ScriptManager;
import me.zhanshi123.vipsystem.task.CheckAllTask;
import me.zhanshi123.vipsystem.util.SchedulerCompat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin {
    private static Main instance;
    private static ConfigManager configManager;
    private static UpdateManager updateManager;
    private static Database database;
    private static Permission permission;
    private static CommandHandler commandHandler;
    private static Metrics metrics;
    private static Cache cache;
    private static ConvertManager convertManager;
    private static CustomCommandManager customCommandManager;
    private static CustomManager customManager;
    private static ScriptManager scriptManager;
    private static boolean enableCustomFunction = false;
    private Object checkAllTaskHandle;

    public static Main getInstance() {
        return instance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static UpdateManager getUpdateManager() {
        return updateManager;
    }

    public static Database getDataBase() {
        return database;
    }

    public static Permission getPermission() {
        return permission;
    }

    public static boolean hasPermission(CommandSender sender, String node) {
        if (permission != null) {
            return permission.has(sender, node);
        }
        return sender.hasPermission(node);
    }

    public static String getPrimaryGroup(Player player) {
        if (permission != null) {
            String group = permission.getPrimaryGroup(player);
            if (group != null) {
                return group;
            }
        }
        String fallback = configManager != null ? configManager.getDefaultGroup() : null;
        return fallback != null ? fallback : "default";
    }

    public static String[] getAllGroups() {
        if (permission != null) {
            return permission.getGroups();
        }
        return new String[0];
    }

    public static CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public static Metrics getMetrics() {
        return metrics;
    }

    public static Cache getCache() {
        return cache;
    }

    public static CustomCommandManager getCustomCommandManager() {
        return customCommandManager;
    }

    public static ConvertManager getConvertManager() {
        return convertManager;
    }

    public static CustomManager getCustomManager() {
        return customManager;
    }

    public static ScriptManager getScriptManager() {
        return scriptManager;
    }

    public static boolean isEnableCustomFunction() {
        return enableCustomFunction;
    }

    @Override
    public void onEnable() {
        instance = this;
        minecraftVersion = detectMinecraftVersion();
        convertManager = new ConvertManager();
        customCommandManager = new CustomCommandManager();
        configManager = new ConfigManager();
        MessageManager.init();
        updateManager = new UpdateManager();
        updateManager.checkUpdate();
        database = new Database();
        if (!database.isAvailable()) {
            Bukkit.getConsoleSender().sendMessage(MessageManager.getString("fatalError"));
            setEnabled(false);
            return;
        }
        database.prepare();
        if (!setupPermissions()) {
            getLogger().warning("Error when hooking Vault! Stop Loading");
            setEnabled(false);
            return;
        }
        scriptManager = new ScriptManager();
        enableCustomFunction = true;
        customManager = new CustomManager();
        commandHandler = new CommandHandler("vipsys");
        metrics = new Metrics(instance);
        new CStats(instance);
        cache = new Cache();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new VipSystemExpansion().register();
        }
        startCheckAllTask();
        VipSystemAPI.getInstance().getOnlinePlayers().forEach(player -> cache.cache(player));
    }

    @Override
    public void onDisable() {
        VipSystemAPI.getInstance().getOnlinePlayers().forEach(player -> Main.getCache().deCache(player));
        stopAllTasks();
        database.release();
    }

    private boolean setupPermissions() {
        if (configManager.isDisableVault()) {
            getLogger().info("groupCommands.enable=true, skip Vault permission provider requirement.");
            return true;
        }
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public void reload() {
        VipSystemAPI.getInstance().getOnlinePlayers().forEach(player -> Main.getCache().deCache(player));
        stopAllTasks();
        database.release();
        //disable & enable
        customCommandManager = new CustomCommandManager();
        configManager = new ConfigManager();
        MessageManager.init();
        database = new Database();
        if (!database.isAvailable()) {
            Bukkit.getConsoleSender().sendMessage(MessageManager.getString("fatalError"));
            setEnabled(false);
            return;
        }
        database.prepare();
        commandHandler = new CommandHandler("vipsys");
        cache = new Cache();
        scriptManager = new ScriptManager();
        customManager = new CustomManager();
        startCheckAllTask();
        VipSystemAPI.getInstance().getOnlinePlayers().forEach(player -> cache.cache(player));
    }

    private static int minecraftVersion = 0;

    public static int getMinecraftVersion() {
        return minecraftVersion;
    }

    private void startCheckAllTask() {
        checkAllTaskHandle = SchedulerCompat.runAsyncRepeating(instance, () -> new CheckAllTask().run(), 0L, 20L * 60L);
    }

    private void stopAllTasks() {
        SchedulerCompat.cancelTask(checkAllTaskHandle);
        checkAllTaskHandle = null;
        SchedulerCompat.cancelAllTasks(instance);
    }

    private int detectMinecraftVersion() {
        try {
            String mcVersion = Bukkit.getMinecraftVersion();
            int major = parseMajorVersion(mcVersion);
            if (major > 0) {
                return major;
            }
        } catch (NoSuchMethodError ignored) {
        }
        String bukkitVersion = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("(\\d+)\\.").matcher(bukkitVersion);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private int parseMajorVersion(String version) {
        if (version == null || version.isEmpty()) {
            return 0;
        }
        String[] split = version.split("\\.");
        if (split.length == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(split[0]);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public void debug(String text) {
        if (configManager.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§c[VipSystem-Debug] §f" + text);
        }
    }
}
