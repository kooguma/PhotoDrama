package com.loopeer.android.photodrama4android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Advert extends BaseModel implements Parcelable {

    protected Advert(Parcel in) {
        title = in.readString();
        image = in.readString();
        relType = in.readInt();
        relValue = in.readString();
        index = in.readInt();
        categoryId = in.readString();
    }

    public static final Creator<Advert> CREATOR = new Creator<Advert>() {
        @Override
        public Advert createFromParcel(Parcel in) {
            return new Advert(in);
        }

        @Override
        public Advert[] newArray(int size) {
            return new Advert[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeInt(relType);
        dest.writeString(relValue);
        dest.writeInt(index);
        dest.writeString(categoryId);
    }

    public static final int LAUNCH = 0;
    public static final int LIST = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ LAUNCH, LIST })
    public @interface AdvertType {}

    public static final int REL_TYPE_NONE = 0;
    public static final int REL_TYPE_URL = 1;
    public static final int REL_TYPE_SERIES = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ REL_TYPE_NONE, REL_TYPE_URL, REL_TYPE_SERIES })
    public @interface RelTye {}

    public String title; //标题
    public String subtitle; //副标题
    public String image; //图片
    @SerializedName("rel_type")
    public int relType; //关联类型 0-无 1-url 2-剧集
    @SerializedName("rel_value")
    public String relValue; //关联值
    public int index; // 0-启动页 1-剧集列表
    @SerializedName("category_id")
    public String categoryId; //所属分类
}
