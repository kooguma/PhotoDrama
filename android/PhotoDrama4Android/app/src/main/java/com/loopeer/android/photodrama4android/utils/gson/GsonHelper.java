package com.loopeer.android.photodrama4android.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    private static Gson sGson;
    public static Gson getDefault() {
        if (sGson == null) {
            //BooleanTypeAdapter booleanTypeAdapter = new BooleanTypeAdapter();
            sGson = new GsonBuilder()
                    //.registerTypeAdapter(Boolean.class, booleanTypeAdapter)
                    //.registerTypeAdapter(boolean.class, booleanTypeAdapter)
                    .create();
        }
        return sGson;
    }
}
