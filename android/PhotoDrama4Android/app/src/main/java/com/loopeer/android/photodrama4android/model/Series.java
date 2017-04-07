package com.loopeer.android.photodrama4android.model;

import android.content.Context;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.R;
import java.util.List;

public class Series extends BaseModel {

    public String name; // 剧名
    public String author; // 作者
    public List<Theme> themes; // 模板集合
    @SerializedName("themes_count")
    public int themesCount; // 模板数量


    public String formatSeriesIndex(Context context,Theme theme){
        return context.getString(R.string.drama_series_format,themes.indexOf(theme),themesCount);
    }
}
