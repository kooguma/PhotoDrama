package com.loopeer.android.photodrama4android.media;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HandlerWrapper<W, T> extends Handler {

    public static final int TYPE_LOAD_IMAGE = 0;
    public static final int TYPE_LOAD_SUBTITLE = 1;
    public static final int TYPE_LOAD_IMAGE_RES = 2;

    private String key;
    private Callback<T> callback;
    private W data;
    private int type;

    public HandlerWrapper(Looper L, int type, W d, Callback<T> callback) {
        super(L);

        this.data = d;
        this.type = type;
        this.callback = callback;
        key = d.toString();
    }

    public HandlerWrapper(int type, W d, Callback<T> callback) {
        super();
        this.data = d;
        this.type = type;
        this.callback = callback;
        key = this.toString();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle b = msg.getData();
        this.callback.onResult((T) b.get(key));;
    }

    public interface Callback<U> {
        void onResult(U t);
    }

    public int getType() {
        return type;
    }

    public W getData() {
        return data;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerWrapper<?, ?> that = (HandlerWrapper<?, ?>) o;
        return key.equals(that.getKey());

    }

    @Override
    public int hashCode() {
        int result = callback != null ? callback.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }
}