package me.zhanshi123.vipsystem.data.connector;

import me.zhanshi123.vipsystem.Main;

import java.io.File;
import java.util.List;

public class ConnectionData {
    private boolean useMySQL;
    private List<String> mysql;
    private String sqlite;

    public ConnectionData(boolean useMySQL, List<String> mysql) {
        this.useMySQL = useMySQL;
        this.mysql = mysql;
        File dbFile = new File(Main.getInstance().getDataFolder(), "database.db");
        this.sqlite = "jdbc:sqlite:" + dbFile.getPath().replace('\\', '/');
    }

    public boolean isUseMySQL() {
        return useMySQL;
    }

    public void setUseMySQL(boolean useMySQL) {
        this.useMySQL = useMySQL;
    }

    List<String> getMysql() {
        return mysql;
    }

    public void setMysql(List<String> mysql) {
        this.mysql = mysql;
    }

    String getSqlite() {
        return sqlite;
    }

    public void setSqlite(String sqlite) {
        this.sqlite = sqlite;
    }
}
