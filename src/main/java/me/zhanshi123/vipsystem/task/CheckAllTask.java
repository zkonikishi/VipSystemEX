package me.zhanshi123.vipsystem.task;

import me.zhanshi123.vipsystem.Main;
import me.zhanshi123.vipsystem.api.VipSystemAPI;
import me.zhanshi123.vipsystem.util.SchedulerCompat;

public class CheckAllTask {


    public void run() {
        VipSystemAPI.getInstance().getOnlinePlayers()
            .forEach(player -> SchedulerCompat.runPlayer(Main.getInstance(), player, () -> new CheckVipTask(player).run()));
        Main.getDataBase().getAllFunctions().forEach(
            storedFunction -> SchedulerCompat.runGlobal(Main.getInstance(), () -> new CheckFunctionTask(storedFunction).run())
        );
    }
}
