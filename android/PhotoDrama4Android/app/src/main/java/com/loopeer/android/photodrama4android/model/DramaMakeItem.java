package com.loopeer.android.photodrama4android.model;


import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.ui.activity.BgmMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.RecordMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.SoundEffectActivity;
import com.loopeer.android.photodrama4android.ui.activity.SubtitleEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.TransitionEditActivity;

import java.util.ArrayList;
import java.util.List;

public class DramaMakeItem {

    public int icon;
    public int text;
    public Class targetClass;
    public static List<DramaMakeItem> sDramaMakeItems;

    public DramaMakeItem(int icon, int text, Class targetClass) {
        this.icon = icon;
        this.text = text;
        this.targetClass = targetClass;
    }

    static {
        sDramaMakeItems = new ArrayList<>();
        sDramaMakeItems.add(new DramaMakeItem(R.drawable.ic_subtitle, R.string.drama_make_subtitle, SubtitleEditActivity.class));
        sDramaMakeItems.add(new DramaMakeItem(R.drawable.ic_record, R.string.drama_make_record, RecordMusicActivity.class));
        sDramaMakeItems.add(new DramaMakeItem(R.drawable.ic_clip_time, R.string.drama_make_clip_time, SubtitleEditActivity.class));
        sDramaMakeItems.add(new DramaMakeItem(R.drawable.ic_transition, R.string.drama_make_transition, TransitionEditActivity.class));
        sDramaMakeItems.add(new DramaMakeItem(R.drawable.ic_music, R.string.drama_make_music, BgmMusicActivity.class));
        sDramaMakeItems.add(new DramaMakeItem(R.drawable.ic_sound, R.string.drama_make_sound, SoundEffectActivity.class));
    }
}
