package com.minelittlepony.bigpony;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.minelittlepony.bigpony.scale.ClientPlayerScale;
import com.minelittlepony.bigpony.scale.IPlayerScale;

public class JsonConfig {

    public static JsonConfig of(Path file) {
        return new JsonConfig().load(file);
    }

    static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    protected Path configFile;

    public IPlayerScale data;

    public void save() {
        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(configFile))) {
            writer.setIndent("    ");

            gson.toJson(this, getClass(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected JsonConfig load(Path file) {
        try {
            if (Files.exists(file)) {
                try (BufferedReader s = Files.newBufferedReader(file)) {
                    data = gson.fromJson(s, ClientPlayerScale.class);
                } catch (IOException ignored) {
                    data = null;
                }
            }
        } finally {
            if (data == null) {
                data = new ClientPlayerScale();
            }

            configFile = file;
            save();
        }

        return this;
    }
}
