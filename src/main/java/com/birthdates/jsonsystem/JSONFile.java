package com.birthdates.jsonsystem;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;

public class JSONFile {

    private final String fileName;
    private final File file;
    private final Plugin plugin;
    private final Gson gson;
    private final boolean usePluginFolder;

    public JSONFile(Plugin plugin, String name, boolean usePluginFolder) {
        this(plugin, name, new File(plugin.getDataFolder(), name + ".json"), usePluginFolder);
    }

    public JSONFile(Plugin plugin, String name) {
        this(plugin, name, true);
    }

    public JSONFile(Plugin plugin, String name, File file, boolean usePluginFolder) {
        this.fileName = name + (name.endsWith(".json") ? "" : ".json");
        this.plugin = plugin;
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(new ExclusionStrategy() {
            public boolean shouldSkipField(FieldAttributes f) {
                return f.hasModifier(0x00000080); //is transient
            }

            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).registerTypeAdapter(ItemStack.class, new ItemStackGsonAdapter()).create();
        this.usePluginFolder = usePluginFolder;
        tryLoadFile();
    }

    private void tryLoadFile() {
        if (!this.file.exists() && usePluginFolder) {
            plugin.saveResource(fileName, false);
        }
    }

    private String getFilePath() {
        return usePluginFolder ? plugin.getDataFolder() + "/" + fileName : fileName;
    }

    public void save(Object data) {
        try {
            FileWriter fw = new FileWriter(getFilePath());
            gson.toJson(data, fw);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getData(Type type) {
        try {
            return gson.fromJson(new FileReader(getFilePath()), type);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                log(ChatColor.DARK_RED + "ERROR reading one of your JSON files:" + e.getLocalizedMessage().split(":")[1]);
                log(ChatColor.DARK_RED + "Please verify your JSON at https://jsonlint.com before reporting this to the developer!");
            } else {
                e.printStackTrace();
                log(ChatColor.DARK_RED + "ERROR initializing `" + fileName + "` read the error above. If you cannot make sense of it, please contact the developer!");
            }
        }
        return null;
    }

    public File getFile() {
        return file;
    }


    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}
