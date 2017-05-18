package com.loopeer.android.photodrama4android.media.model;


import com.laputapp.model.BaseModel;

import java.util.UUID;

public class Clip extends BaseModel {
    public int startTime = 0;
    public int showTime = 2000;

    public Clip() {
        id = UUID.randomUUID().toString();
    }

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    @Override
    public String toString() {
        return "Clip{" +
                "startTime=" + startTime +
                ", showTime=" + showTime +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        return id.equals(((Clip)obj).id);
    }

    public String getKey() {
        return id;
    }
}
