package com.loopeer.android.photodrama4android.model;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;
import zlc.season.rxdownload2.RxDownload;

public class Voice extends BaseModel {

    public String name; // 名称
    @SerializedName("voice_url")
    public String voiceUrl; // 下载链接

}
