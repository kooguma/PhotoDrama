package com.loopeer.android.photodrama4android.model;

import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;

public class Theme extends BaseModel {

    @SerializedName("series_id")
    public String seriesId; // 剧集id
    public String name; // 名称
    @SerializedName("used_count")
    public String usedCount; // 使用次数
    public String description; // 简介
    @SerializedName("episode_number")
    public String episodeNumber; // 剧集编号
    @SerializedName("cover_image")
    public String coverImage; // 封面图
    @SerializedName("preview_link")
    public String previewLink; // 预览视频链接
    @SerializedName("zip_link")
    public String zipLink; // 压缩包链接
}
