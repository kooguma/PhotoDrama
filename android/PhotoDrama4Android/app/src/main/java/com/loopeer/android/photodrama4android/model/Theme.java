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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Theme theme = (Theme) o;

        if (seriesId != null ? !seriesId.equals(theme.seriesId) : theme.seriesId != null)
            return false;
        if (name != null ? !name.equals(theme.name) : theme.name != null) return false;
        if (usedCount != null ? !usedCount.equals(theme.usedCount) : theme.usedCount != null) {
            return false;
        }
        if (description != null
            ? !description.equals(theme.description)
            : theme.description != null) {
            return false;
        }
        if (episodeNumber != null
            ? !episodeNumber.equals(theme.episodeNumber)
            : theme.episodeNumber != null) {
            return false;
        }
        if (coverImage != null ? !coverImage.equals(theme.coverImage) : theme.coverImage != null) {
            return false;
        }
        if (previewLink != null
            ? !previewLink.equals(theme.previewLink)
            : theme.previewLink != null) {
            return false;
        }
        return zipLink != null ? zipLink.equals(theme.zipLink) : theme.zipLink == null;

    }

    @Override public int hashCode() {
        int result = seriesId != null ? seriesId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (usedCount != null ? usedCount.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (episodeNumber != null ? episodeNumber.hashCode() : 0);
        result = 31 * result + (coverImage != null ? coverImage.hashCode() : 0);
        result = 31 * result + (previewLink != null ? previewLink.hashCode() : 0);
        result = 31 * result + (zipLink != null ? zipLink.hashCode() : 0);
        return result;
    }
}
