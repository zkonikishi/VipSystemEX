package me.zhanshi123.vipsystem.command.sub;

import me.zhanshi123.vipsystem.api.VipSystemAPI;
import me.zhanshi123.vipsystem.api.vip.VipData;
import me.zhanshi123.vipsystem.command.SubCommand;
import me.zhanshi123.vipsystem.command.tab.CommandTab;
import me.zhanshi123.vipsystem.command.tab.TabCompletable;
import me.zhanshi123.vipsystem.command.type.PermissionCommand;
import me.zhanshi123.vipsystem.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddTimeCommand extends SubCommand implements PermissionCommand, TabCompletable {
    public AddTimeCommand() {
        super("addtime", MessageManager.getString("Command.addtime.usage"), MessageManager.getString("Command.addtime.desc"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String playerName = args[1];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(MessageManager.getString("playerNotFound"));
            return true;
        }
        VipData vipData = VipSystemAPI.getInstance().getVipManager().getVipData(player);
        if (vipData == null) {
            sender.sendMessage(MessageManager.getString("Command.addtime.noVip"));
            return true;
        }
        if (vipData.getDuration() == -1) {
            sender.sendMessage(MessageManager.getString("Command.addtime.isPermanent"));
            return true;
        }
        long extra = VipSystemAPI.getInstance().getTimeMillis(args[2]);
        if (extra <= 0 || (extra >= 1 && extra < 60000)) {
            sender.sendMessage(MessageManager.getString("Command.give.invalidTime"));
            return true;
        }
        VipSystemAPI.getInstance().getVipManager().renewVip(player, extra);
        sender.sendMessage(MessageManager.getString("Command.addtime.success"));
        return true;
    }

    @Override
    public List<CommandTab> getArguments() {
        return Arrays.asList(new CommandTab[]{
                () -> VipSystemAPI.getInstance().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()),
                () -> Arrays.asList("1d", "7d", "30d", "180d")
        });
    }
}
