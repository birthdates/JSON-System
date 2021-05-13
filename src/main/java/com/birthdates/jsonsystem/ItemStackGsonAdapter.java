package com.birthdates.jsonsystem;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ItemStackGsonAdapter extends TypeAdapter<ItemStack> {

    private static final Type sereType = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Override
    public void write(JsonWriter jsonWriter, ItemStack itemStack) throws IOException {
        if (itemStack == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(getRaw(itemStack));
    }

    @Override
    public ItemStack read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return fromRaw(jsonReader.nextString());
    }

    private String getRaw(ItemStack item) {
        Map<String, Object> serial = item.serialize();

        if (serial.get("meta") != null) {
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            Map<String, Object> originalMeta = itemMeta.serialize();
            Map<String, Object> meta = new HashMap<>();
            for (Map.Entry<String, Object> entry : originalMeta.entrySet())
                meta.put(entry.getKey(), entry.getValue());
            Object o;
            for (Map.Entry<String, Object> entry : meta.entrySet()) {
                o = entry.getValue();
                if (o instanceof ConfigurationSerializable) {
                    ConfigurationSerializable serializable = (ConfigurationSerializable) o;
                    Map<String, Object> serialized = recursiveSerialization(serializable);
                    meta.put(entry.getKey(), serialized);
                }
            }
            serial.put("meta", meta);
        }

        return new Gson().toJson(serial);
    }

    private Map<String, Object> recursiveSerialization(ConfigurationSerializable o) {
        Map<String, Object> originalMap = o.serialize();
        Map<String, Object> map = new HashMap<>();
        String CLASS_KEY = "SERIAL-ADAPTER-CLASS-KEY";
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            Object o2 = entry.getValue();
            if (o2 instanceof ConfigurationSerializable) {
                ConfigurationSerializable serializable = (ConfigurationSerializable) o2;
                Map<String, Object> newMap = recursiveSerialization(serializable);
                newMap.put(CLASS_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
                map.put(entry.getKey(), newMap);
            }
        }
        map.put(CLASS_KEY, ConfigurationSerialization.getAlias(o.getClass()));
        return map;
    }

    private Map<String, Object> recursiveDoubleToInteger(Map<String, Object> originalMap) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            Object o = entry.getValue();
            if (o instanceof Double) {
                Double d = (Double) o;
                Integer i = d.intValue();
                map.put(entry.getKey(), i);
            } else if (o instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) o;
                map.put(entry.getKey(), recursiveDoubleToInteger(subMap));
            } else {
                map.put(entry.getKey(), o);
            }
        }
        return map;
    }

    private ItemStack fromRaw(String raw) {
        Map<String, Object> keys = new Gson().fromJson(raw, sereType);

        if (keys.get("amount") != null) {
            Double d = (Double) keys.get("amount");
            Integer i = d.intValue();
            keys.put("amount", i);
        }

        ItemStack item;
        try {
            item = ItemStack.deserialize(keys);
        } catch (Exception e) {
            return null;
        }

        if (keys.containsKey("meta")) {
            Map<String, Object> itemMeta = (Map<String, Object>) keys.get("meta");
            itemMeta = recursiveDoubleToInteger(itemMeta);
            ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(itemMeta, ConfigurationSerialization.getClassByAlias("ItemMeta"));
            item.setItemMeta(meta);
        }

        return item;
    }
}
