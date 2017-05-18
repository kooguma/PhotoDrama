package com.loopeer.android.photodrama4android.model;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.MusicInfoUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import zlc.season.rxdownload2.RxDownload;

public class Voice extends BaseModel {
    public String name; // 名称
    @SerializedName("voice_url")
    public String voiceUrl; // 下载链接
    public String duration;

    public Voice(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Voice fromFile(File file) {
        String fileName = file.getName();
        String[] names = fileName.split("_");
        if (names.length == 2) {
            String id = names[1];
            String name = names[0];
            return new Voice(id, name);
        } else {
            return null;
        }
    }

    public String getSaveName() {
        return name + '_' + id;
    }

    public String getFormatDuration() {
       return MusicInfoUtils.getFormatDuration(duration);
    }

    public String getFormatDurationFromLocal(Context context) {
        return MusicInfoUtils.getFormatDurationFromLocal(context,this);
    }

}
