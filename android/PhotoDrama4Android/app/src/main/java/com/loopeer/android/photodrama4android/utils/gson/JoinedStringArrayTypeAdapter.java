package com.loopeer.android.photodrama4android.utils.gson;

import android.text.TextUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class JoinedStringArrayTypeAdapter implements JsonSerializer<String[]>, JsonDeserializer<String[]> {

    private static final String DELIMITER = ",";

    @Override
    public JsonElement serialize(String[] src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(TextUtils.join(DELIMITER, src));
    }

    @Override
    public String[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String s = json.getAsString();
        return !TextUtils.isEmpty(s) ? s.split(DELIMITER) : null;
    }
}
