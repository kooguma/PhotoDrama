package com.loopeer.android.photodrama4android.model;

import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;

public class Series extends BaseModel {

    public String id; // ID
    public String name; // 剧名
    public String author; // 作者
    public String themes; // 模板集合
    @SerializedName("themes_count")
    public String themesCount; // 模板数量
}
