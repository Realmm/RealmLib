package net.jamesandrew.realmlib.file;

import net.jamesandrew.commons.logging.Logger;
import net.jamesandrew.realmlib.RealmLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

public class YMLFile {

    private FileConfiguration data = null;
    private File file = null;
    private final String name;
    private final Plugin plugin;

    public YMLFile(String name) {
        this.plugin = RealmLib.get();
        this.name = name;
        saveDefault();
    }

    public void reload() {
        if (file == null) file = new File("plugins/" + plugin.getName(), name + ".yml");
        data = YamlConfiguration.loadConfiguration(file);

        // Look for defaults in the jar
        try {
            Reader ymlStream = new InputStreamReader(plugin.getResource(name + ".yml"), "UTF8");
            YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(ymlStream);
            data.setDefaults(ymlConfig);
        } catch (UnsupportedEncodingException e) {
            Logger.error(">> Reload file exception!");
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (data == null) reload();
        return data;
    }

    public void save() {
        try {
            if (file == null || data == null) return;
            data.save(file);
        } catch (IOException e) {
            Logger.error(">> Save file exception!");
            e.printStackTrace();
        }
    }

    public void remove(String path) {
        setSaveReload(path, null);
    }

    public void setSaveReload(String path, Object obj) {
        getConfig().set(path, obj);
        save();
        reload();
    }

    private void saveDefault() {
        if (file == null) file = new File("plugins/" + plugin.getName(), name + ".yml");
        if (!file.exists()) {
            Logger.log(">> Loading " + name + ".yml!");
            plugin.saveResource(name + ".yml", false);
            Logger.log(">> " + name + ".yml loaded!");
        }
    }

}
