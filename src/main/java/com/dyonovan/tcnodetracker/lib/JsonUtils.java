package com.dyonovan.tcnodetracker.lib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

    private static boolean needsSaving = false;

    private static class InstantSerializer implements JsonSerializer<Instant> {

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(src));
        }
    }

    private static class InstantDeserializer implements JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return DateTimeFormatter.ISO_INSTANT.parse(json.getAsString(), Instant::from);
            } catch (DateTimeParseException ignored) {
                needsSaving = true;
                try {
                    return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                            .parse(json.getAsString(), Instant::from);
                } catch (DateTimeParseException ignored2) {
                    TCNodeTracker.LOGGER.warn("Could not parse saved datetime: " + json);
                    // Give up without throwing
                    return Instant.now();
                }
            }
        }
    }

    public static void writeJson() {

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Instant.class, new InstantSerializer())
                .create();
        String json = gson.toJson(TCNodeTracker.nodelist);

        try {
            FileWriter fw = new FileWriter(TCNodeTracker.hostName + "/nodes.json");
            fw.write(json);
            fw.close();
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println(Constants.MODID + ": Could not write to nodes.json!");
        }
    }

    public static void readJson() {

        try {
            BufferedReader br = new BufferedReader(new FileReader(TCNodeTracker.hostName + "/nodes.json"));
            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantDeserializer()).create();
            // TCNodeTracker.nodelist = gson.fromJson(br, TCNodeTracker.nodelist.getClass());
            needsSaving = false;
            TCNodeTracker.nodelist = gson.fromJson(br, new TypeToken<List<NodeList>>() {}.getType());
            if (needsSaving) {
                needsSaving = false;
                writeJson();
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            System.out.println(Constants.MODID + ": No nodes.json file found.");
        }
    }
}
