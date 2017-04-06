package com.loopeer.android.photodrama4android.utils.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class rawType = type.getRawType();
        if (rawType.isEnum()) {
            return new EnumTypeAdapter<>(rawType);
        }

        return null;
    }

    public static class EnumTypeAdapter<E extends Enum<E>> extends TypeAdapter<E> {

        private final E[] mEnumConstants;

        public EnumTypeAdapter(Class<E> eClass) {
            mEnumConstants = eClass.getEnumConstants();
        }

        @Override
        public void write(JsonWriter out, E value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.jsonValue(String.valueOf(value.ordinal()));
            }
        }

        @Override
        public E read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return mEnumConstants[in.nextInt()];
            }
        }
    }
}
