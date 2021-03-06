package com.loopeer.android.photodrama4android.model;

import android.content.Context;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.R;
import java.util.List;

public class Series extends BaseModel {

    public String name; // 剧名
    public String subtitle; //副标题
    public String author; // 作者
    public List<Theme> themes; // 模板集合
    @SerializedName("themes_count")
    public int themesCount; // 模板数量
    @SerializedName("cover_image")
    public String coverImage; //封面图
    public String description; //描述
    @SerializedName("used_count")
    public String usedCount; // 使用次数

    public String formatSeriesIndex(Context context, Theme theme) {
        if(theme == null) return "";
        return context.getString(R.string.drama_series_format,
            Integer.parseInt(theme.episodeNumber), themesCount);
    }

    public Theme firstTheme() {
        if (themes == null || themes.size() == 0) {
            return null;
        }else {
            return themes.get(0);
        }
    }
}
