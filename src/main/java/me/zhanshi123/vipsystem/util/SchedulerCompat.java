package me.zhanshi123.vipsystem.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SchedulerCompat {
    private static final Set<Object> TRACKED_TASKS = ConcurrentHashMap.newKeySet();

    private SchedulerCompat() {
    }

    public static boolean isFolia() {
        try {
            Bukkit.getServer().getClass().getMethod("getAsyncScheduler");
            Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    public static Object runGlobal(Plugin plugin, Runnable runnable) {
        if (!isPluginRunnable(plugin)) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(Bukkit.getServer(), "getGlobalRegionScheduler");
                Object task = invokeByName(scheduler, "run", plugin, asConsumer(runnable));
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static Object runGlobalLater(Plugin plugin, Runnable runnable, long delayTicks) {
        if (!isPluginRunnable(plugin)) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(Bukkit.getServer(), "getGlobalRegionScheduler");
                Object task = invokeByName(scheduler, "runDelayed", plugin, asConsumer(runnable), delayTicks);
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
    }

    public static Object runAsync(Plugin plugin, Runnable runnable) {
        if (!isPluginRunnable(plugin)) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(Bukkit.getServer(), "getAsyncScheduler");
                Object task = invokeByName(scheduler, "runNow", plugin, asConsumer(runnable));
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static Object runAsyncLater(Plugin plugin, Runnable runnable, long delayTicks) {
        if (!isPluginRunnable(plugin)) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(Bukkit.getServer(), "getAsyncScheduler");
                long delayMillis = ticksToMillis(delayTicks);
                Object task = invokeByName(scheduler, "runDelayed", plugin, asConsumer(runnable), delayMillis, TimeUnit.MILLISECONDS);
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks);
    }

    public static Object runAsyncRepeating(Plugin plugin, Runnable runnable, long delayTicks, long periodTicks) {
        if (!isPluginRunnable(plugin)) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(Bukkit.getServer(), "getAsyncScheduler");
                long delayMillis = ticksToMillis(delayTicks);
                long periodMillis = ticksToMillis(periodTicks);
                Object task = invokeByName(scheduler, "runAtFixedRate", plugin, asConsumer(runnable), delayMillis, periodMillis, TimeUnit.MILLISECONDS);
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, periodTicks);
    }

    public static Object runPlayer(Plugin plugin, Player player, Runnable runnable) {
        if (!isPluginRunnable(plugin) || player == null || !player.isOnline()) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(player, "getScheduler");
                Object task = invokeByName(scheduler, "run", plugin, asConsumer(runnable), null);
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static Object runPlayerLater(Plugin plugin, Player player, Runnable runnable, long delayTicks) {
        if (!isPluginRunnable(plugin) || player == null || !player.isOnline()) {
            return null;
        }
        if (isFolia()) {
            try {
                Object scheduler = invoke(player, "getScheduler");
                Object task = invokeByName(scheduler, "runDelayed", plugin, asConsumer(runnable), null, delayTicks);
                track(task);
                return task;
            } catch (Exception ignored) {
            }
        }
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
    }

    public static void cancelTask(Object taskHandle) {
        if (taskHandle == null) {
            return;
        }
        TRACKED_TASKS.remove(taskHandle);
        if (taskHandle instanceof BukkitTask bukkitTask) {
            bukkitTask.cancel();
            return;
        }
        try {
            invokeByName(taskHandle, "cancel");
        } catch (Exception ignored) {
        }
    }

    public static void cancelAllTasks(Plugin plugin) {
        if (!isFolia()) {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
        TRACKED_TASKS.forEach(SchedulerCompat::cancelTask);
        TRACKED_TASKS.clear();
    }

    private static boolean isPluginRunnable(Plugin plugin) {
        return plugin != null && plugin.isEnabled();
    }

    private static void track(Object task) {
        if (task != null) {
            TRACKED_TASKS.add(task);
        }
    }

    private static long ticksToMillis(long ticks) {
        return Math.max(0L, ticks) * 50L;
    }

    private static Consumer<Object> asConsumer(Runnable runnable) {
        return ignored -> runnable.run();
    }

    private static Object invoke(Object target, String methodName, Object... args) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        return method.invoke(target, args);
    }

    private static Object invokeByName(Object target, String methodName, Object... args) throws Exception {
        Method method = Arrays.stream(target.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == args.length)
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException(methodName + " with args " + args.length));
        return method.invoke(target, args);
    }
}