package com.loopeer.android.photodrama4android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import com.google.gson.annotations.SerializedName;
import com.laputapp.model.BaseModel;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Advert extends BaseModel implements Parcelable{

    public static final int LAUNCH = 0;
    public static final int LIST = 1;

    protected Advert(Parcel in) {
        title = in.readString();
        image = in.readString();
        relType = in.readString();
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
        dest.writeString(relType);
        dest.writeString(relValue);
        dest.writeInt(index);
        dest.writeString(categoryId);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ LAUNCH, LIST })
    public @interface AdvertType {}

    public String title; //标题
    public String image; //图片
    @SerializedName("rel_type")
    public String relType; //关联类型
    @SerializedName("rel_value")
    public String relValue; //关联值
    public int index; // 0-启动页 1-剧集列表
    @SerializedName("category_id")
    public String categoryId; //所属分类
}
