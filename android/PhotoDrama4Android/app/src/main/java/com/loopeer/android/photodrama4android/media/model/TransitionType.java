package com.loopeer.android.photodrama4android.media.model;

import com.google.gson.annotations.SerializedName;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.programs.DissolveShaderProgram;
import com.loopeer.android.photodrama4android.media.programs.FadeShaderProgram;
import com.loopeer.android.photodrama4android.media.programs.ImageClipShaderProgram;
import com.loopeer.android.photodrama4android.media.programs.WipeShaderProgram;
import com.loopeer.android.photodrama4android.media.render.DissolveDrawer;
import com.loopeer.android.photodrama4android.media.render.FadeDrawer;
import com.loopeer.android.photodrama4android.media.render.SlideDrawer;
import com.loopeer.android.photodrama4android.media.render.WipeDrawer;

import java.util.HashMap;
import java.util.Map;

public enum TransitionType {

    @SerializedName("0")
    NO(0, R.string.transition_name_no, 0, null, null),

    @SerializedName("3")
    DISSOLVE(1, R.string.transition_name_dissolve, R.drawable.ic_transition_dissolve, DissolveDrawer.class, DissolveShaderProgram.class),

    @SerializedName("4")
    FADE(2, R.string.transition_name_fade, R.drawable.ic_transition_fade, FadeDrawer.class, FadeShaderProgram.class),

    @SerializedName("1")
    SLIDE(3, R.string.transition_name_slide, R.drawable.ic_transition_slide, SlideDrawer.class, ImageClipShaderProgram.class),

    @SerializedName("2")
    WIPE(4, R.string.transition_name_wipe, R.drawable.ic_transition_wipe, WipeDrawer.class, WipeShaderProgram.class);

    private final int mValue;
    private final int mIcon;
    private final int mName;
    private final Class mClass;
    private final Class mShaderClass;

    TransitionType(int value, int name, int icon, Class zClass, Class shaderProgram) {
        mValue = value;
        mName = name;
        mIcon = icon;
        mClass = zClass;
        mShaderClass = shaderProgram;
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

    public Class getShaderClass() {
        return mShaderClass;
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