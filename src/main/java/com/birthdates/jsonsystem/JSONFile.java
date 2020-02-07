package com.birthdates.jsonsystem;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;

public class JSONFile {

    private String fileName;
    private File file;
    private Plugin plugin;

    public JSONFile(Plugin plugin, String name) {
        this.fileName = name + ".json";
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), this.fileName);
        tryLoadFile();
    }

    private void tryLoadFile() {
        if (!this.file.exists()) {
            plugin.saveResource(fileName + ".json", false);
        }
    }

    public void save(Object data) {
        try {
            FileWriter fw = new FileWriter(plugin.getDataFolder() + "/" + fileName);
            new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(new ExclusionStrategy() {
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(GsonIgnore.class) != null;
                }
                
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).create().toJson(data, fw);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public <T> T getData(Type type) {
        try {
            return new GsonBuilder().setPrettyPrinting().create().fromJson(new FileReader(plugin.getDataFolder() + "/" + fileName), type);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                log(ChatColor.DARK_RED + "ERROR reading one of your JSON files: " + e.getLocalizedMessage().split(":")[1]);
                log(ChatColor.DARK_RED + "Please verify your JSON at https://jsonlint.com before reporting this to the developer!");
            } else {
                e.printStackTrace();
                log(ChatColor.DARK_RED + "ERROR initializing `" + fileName + "` read the error above. If you cannot make sense of it, please contact the developer!");
            }
        }
        return null;
    }


    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}
