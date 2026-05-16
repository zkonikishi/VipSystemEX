package me.zhanshi123.vipsystem.task;

import com.google.gson.Gson;
import me.zhanshi123.vipsystem.Main;
import me.zhanshi123.vipsystem.manager.MessageManager;
import me.zhanshi123.vipsystem.util.SchedulerCompat;
import me.zhanshi123.vipsystem.util.Update;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateCheckTask {
    public void run() {
        Update update = null;
        try {
            URL url = URI.create("https://service.zhanshi123.me/update/index.php?name=VipSystemRecode").toURL();
            InputStream in = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String json = br.readLine();
            br.close();
            in.close();
            update = new Gson().fromJson(json, Update.class);
        } catch (Exception e) {
            SchedulerCompat.runGlobal(Main.getInstance(), () -> {
                Bukkit.getConsoleSender().sendMessage(MessageFormat.format(MessageManager.getString("updateCheckFailure"), e.getMessage()));
                e.printStackTrace();
            });
        }
        if (update == null) {
            return;
        }
        Main.getUpdateManager().setUpdate(update);
        Update finalUpdate = update;
        SchedulerCompat.runGlobal(Main.getInstance(), () -> {
            String localVersion = Main.getInstance().getDescription().getVersion();
            String remoteVersion = String.valueOf(finalUpdate.getVersion());
            int compare = compareVersion(remoteVersion, localVersion);
            if (compare <= 0) {
                Bukkit.getConsoleSender().sendMessage(MessageManager.getString("latestVersion"));
            } else {
                Bukkit.getConsoleSender().sendMessage(MessageFormat.format(MessageManager.getString("newUpdate"), remoteVersion, finalUpdate.getMessage()));
            }
        });
    }

    private static int compareVersion(String a, String b) {
        List<Integer> av = parseVersionParts(a);
        List<Integer> bv = parseVersionParts(b);
        int len = Math.max(av.size(), bv.size());
        for (int i = 0; i < len; i++) {
            int ai = i < av.size() ? av.get(i) : 0;
            int bi = i < bv.size() ? bv.get(i) : 0;
            if (ai != bi) {
                return Integer.compare(ai, bi);
            }
        }
        return 0;
    }

    private static List<Integer> parseVersionParts(String version) {
        List<Integer> parts = new ArrayList<>();
        if (version == null) {
            return parts;
        }
        Matcher matcher = Pattern.compile("\\d+").matcher(version);
        while (matcher.find()) {
            try {
                parts.add(Integer.parseInt(matcher.group()));
            } catch (NumberFormatException ignored) {
            }
        }
        return parts;
    }
}
