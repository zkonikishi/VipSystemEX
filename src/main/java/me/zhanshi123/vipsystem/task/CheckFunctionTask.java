package me.zhanshi123.vipsystem.task;

import me.zhanshi123.vipsystem.Main;
import me.zhanshi123.vipsystem.custom.StoredFunction;
import me.zhanshi123.vipsystem.util.SchedulerCompat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CheckFunctionTask {
    private StoredFunction function;

    public CheckFunctionTask(StoredFunction function) {
        this.function = function;
    }

    public void run() {
        if (function == null) {
            return;
        }
        if (function.getTimeToExpire() >= 1000 * 60L) {
            return;
        }
        if (function.getAwaitingPlayer() != null) {
            String awaitingName = function.getAwaitingPlayer();
            Player awaitingPlayer = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(awaitingName))
                    .findFirst().orElse(null);
            if (awaitingPlayer == null) {
                return;
            }
        }
        long temp = function.getTimeToExpire();
        if (temp < 0) {
            temp = 0;
        }
        SchedulerCompat.runGlobalLater(Main.getInstance(), () -> new DelayedExecuteFunctionTask(function).run(), temp / 1000 * 20);
    }
}
