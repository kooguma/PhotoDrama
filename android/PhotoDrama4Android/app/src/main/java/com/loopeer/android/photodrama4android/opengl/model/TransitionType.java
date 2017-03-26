package com.loopeer.android.photodrama4android.opengl.model;

import com.google.gson.annotations.SerializedName;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.opengl.render.DissolveDrawer;
import com.loopeer.android.photodrama4android.opengl.render.FadeDrawer;
import com.loopeer.android.photodrama4android.opengl.render.SlideDrawer;
import com.loopeer.android.photodrama4android.opengl.render.WipeDrawer;

import java.util.HashMap;
import java.util.Map;

public enum TransitionType {

    @SerializedName("0")
    NO(0, R.string.transition_name_no, 0, null),

    @SerializedName("1")
    SLIDE(1, R.string.transition_name_slide, R.drawable.ic_transition_slide, SlideDrawer.class),

    @SerializedName("1")
    WIPE(2, R.string.transition_name_wipe, R.drawable.ic_transition_wipe, WipeDrawer.class),

    @SerializedName("1")
    DISSOLVE(3, R.string.transition_name_dissolve, R.drawable.ic_transition_dissolve, DissolveDrawer.class),

    @SerializedName("2")
    FADE(4, R.string.transition_name_fade, R.drawable.ic_transition_fade, FadeDrawer.class);

    private final int mValue;
    private final int mIcon;
    private final int mName;
    private final Class mClass;

    TransitionType(int value, int name, int icon, Class zClass) {
        mValue = value;
        mName = name;
        mIcon = icon;
        mClass = zClass;
    }

    public int getName() {
        return mName;
    }

    public int getValue() {
        return mValue;
    }

    public int getIcon() {
        return mIcon;
    }

    public Class getDrawerClass() {
        return mClass;
    }

    private static final Map<Integer, TransitionType>
            STRING_MAPPING = new HashMap<>();

    static {
        for (TransitionType commentType : TransitionType.values()) {
            STRING_MAPPING.put(commentType.getValue(), commentType);
        }
    }

    public static TransitionType fromValue(int value) {
        return STRING_MAPPING.get(value);
    }

}