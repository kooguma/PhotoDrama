package com.loopeer.android.photodrama4android.model;


import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;

public class Version extends BaseModel {

    @SerializedName("version_code") public int versionCode;// 123,
    public String url;// "http://www.baidu.com/",
    public String message;// "test",
    public String description;// "description"
}
