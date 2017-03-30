package com.loopeer.android.photodrama4android;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.ui.activity.ImageSegmentEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.MakeMovieActivity;
import com.loopeer.android.photodrama4android.ui.activity.RecordMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.SoundEffectActivity;
import com.loopeer.android.photodrama4android.ui.activity.SubtitleEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.TestMusicSelectedActivity;
import com.loopeer.android.photodrama4android.ui.activity.TextInputActivity;
import com.loopeer.android.photodrama4android.ui.activity.TransitionEditActivity;

public class Navigator {

    public static final String EXTRA_DRAMA = "extra_drama";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_MUSIC_CLIP = "extra_music_clip";
    public static final String EXTRA_SUBTITLE_CLIP = "extra_subtitle_clip";
    public static final int REQUEST_CODE_DRAMA_IMAGE_EDIT = 1001;
    public static final int REQUEST_CODE_DRAMA_TRANSITION_EDIT = 1002;
    public static final int REQUEST_CODE_DRAMA_SUBTITLE_EDIT = 1003;
    public static final int REQUEST_CODE_TEXT_INPUT = 1004;
    public static final int REQUEST_CODE_DRAMA_AUDIO_RECORD = 1005;
    public static final int REQUEST_CODE_DRAMA_SOUND_EFFECT = 1006;
    public static final int REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT = 1007;

    public static void startImageSelectActivity(Context context) {
        Intent intent = new Intent(context, ImageSelectActivity.class);
        context.startActivity(intent);
    }

    public static void startMakeMovieActivity(Context context) {
        Intent intent = new Intent(context, MakeMovieActivity.class);
        context.startActivity(intent);
    }

    public static void startMakeMovieActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, MakeMovieActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        context.startActivity(intent);
    }

    public static void startImageClipEditActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, ImageSegmentEditActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_DRAMA_IMAGE_EDIT);
    }

    public static void startTransitionEditActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, TransitionEditActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_DRAMA_TRANSITION_EDIT);
    }

    public static void startSubtitleEditActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, SubtitleEditActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_DRAMA_SUBTITLE_EDIT);
    }

    public static void startTextInputActivity(Context context, String content) {
        Intent intent = new Intent(context, TextInputActivity.class);
        intent.putExtra(EXTRA_TEXT, content);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_TEXT_INPUT);
    }

    public static void startRecordMusicActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, RecordMusicActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_DRAMA_AUDIO_RECORD);
    }

    public static void startSoundEffectActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, SoundEffectActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_DRAMA_SOUND_EFFECT);
    }

    //TODO test
    public static void startTestMusicSelectedActivity(Context context) {
        Intent intent = new Intent(context, TestMusicSelectedActivity.class);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT);
    }

}
