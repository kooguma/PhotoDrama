package com.loopeer.android.photodrama4android.opengl.cache;

import android.content.Context;

import com.loopeer.android.photodrama4android.opengl.model.TransitionType;
import com.loopeer.android.photodrama4android.opengl.programs.ShaderProgram;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class ShaderProgramCache {

    private static volatile ShaderProgramCache sDefaultInstance;
    private HashMap<String, ShaderProgram> mShaderProgramHashMap;

    private ShaderProgramCache() {
        mShaderProgramHashMap = new HashMap<>();
    }

    public static ShaderProgramCache getInstance() {
        if (sDefaultInstance == null) {
            synchronized (ShaderProgramCache.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new ShaderProgramCache();
                }
            }
        }
        return sDefaultInstance;
    }

    public void init(Context context) {
        for (TransitionType type :
                TransitionType.values()) {
            try {
                if (type == TransitionType.SLIDE) {
                    Constructor<ShaderProgram> constructor = type.getShaderClass().getConstructor(Context.class);
                    ShaderProgram drawer = constructor.newInstance(context);
                    sDefaultInstance.mShaderProgramHashMap.put(String.valueOf(type.getValue()) + "_0", drawer);
                    ShaderProgram drawer1 = constructor.newInstance(context);
                    sDefaultInstance.mShaderProgramHashMap.put(String.valueOf(type.getValue()) + "_1", drawer1);
                } else {
                    Constructor<ShaderProgram> constructor = type.getShaderClass().getConstructor(Context.class);
                    ShaderProgram drawer = constructor.newInstance(context);
                    sDefaultInstance.mShaderProgramHashMap.put(String.valueOf(type.getValue()), drawer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ShaderProgram getTextureId(String key) {
        if (mShaderProgramHashMap == null) return null;
        ShaderProgram i = mShaderProgramHashMap.get(key);
        return i;
    }

    public void addIdToCache(String key, ShaderProgram id) {
        mShaderProgramHashMap.put(key, id);
    }

}
