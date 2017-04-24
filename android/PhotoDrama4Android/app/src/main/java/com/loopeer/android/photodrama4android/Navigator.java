package com.loopeer.android.photodrama4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.activity.AboutActivity;
import com.loopeer.android.photodrama4android.ui.activity.BgmMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaDetailActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaPlayActivity;
import com.loopeer.android.photodrama4android.ui.activity.FeedbackActivity;
import com.loopeer.android.photodrama4android.ui.activity.FullLandscapePlayActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSegmentEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.MainActivity;
import com.loopeer.android.photodrama4android.ui.activity.MakeMovieActivity;
import com.loopeer.android.photodrama4android.ui.activity.RecordMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.SettingActivity;
import com.loopeer.android.photodrama4android.ui.activity.ShareActivity;
import com.loopeer.android.photodrama4android.ui.activity.SoundEffectActivity;
import com.loopeer.android.photodrama4android.ui.activity.SubtitleEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.TestMusicSelectedActivity;
import com.loopeer.android.photodrama4android.ui.activity.TextInputActivity;
import com.loopeer.android.photodrama4android.ui.activity.TransitionEditActivity;

public class Navigator {

    public static final String EXTRA_DRAMA = "extra_drama";
    public static final String EXTRA_VIDEO_PATH = "extra_video_path";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_MUSIC_CLIP = "extra_music_clip";
    public static final String EXTRA_REQUEST_CODE = "extra_request_code";
    public static final String EXTRA_SUBTITLE_CLIP = "extra_subtitle_clip";
    public static final int REQUEST_CODE_DRAMA_IMAGE_EDIT = 1001;
    public static final int REQUEST_CODE_DRAMA_TRANSITION_EDIT = 1002;
    public static final int REQUEST_CODE_DRAMA_SUBTITLE_EDIT = 1003;
    public static final int REQUEST_CODE_TEXT_INPUT = 1004;
    public static final int REQUEST_CODE_DRAMA_AUDIO_RECORD = 1005;
    public static final int REQUEST_CODE_DRAMA_SOUND_EFFECT = 1006;
    public static final int REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT = 1007;
    public static final int REQUEST_CODE_DRAMA_SOUND_BGM_SELECT = 1008;
    public static final int REQUEST_CODE_DRAMA_SOUND_BGM = 1009;
    public static final int REQUEST_FULL_SCREEN = 10010;
    public static final String EXTRA_THEME ="extra_theme" ;
    public static final String EXTRA_USEDTIME ="extra_usedtime" ;
    public static final String EXTRA_IS_TO_START ="extra_is_to_start" ;

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
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_IMAGE_EDIT);
    }

    public static void startTransitionEditActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, TransitionEditActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_TRANSITION_EDIT);
    }

    public static void startSubtitleEditActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, SubtitleEditActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_SUBTITLE_EDIT);
    }

    public static void startTextInputActivity(Context context, String content) {
        Intent intent = new Intent(context, TextInputActivity.class);
        intent.putExtra(EXTRA_TEXT, content);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_TEXT_INPUT);
    }

    public static void startRecordMusicActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, RecordMusicActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_AUDIO_RECORD);
    }

    public static void startSoundEffectActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, SoundEffectActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_SOUND_EFFECT);
    }

    public static void startBgmMusicActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, BgmMusicActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_SOUND_BGM);
    }

    //TODO test
    public static void startTestMusicSelectedActivity(Context context, Drama drama, int requestCode) {
        Intent intent = new Intent(context, TestMusicSelectedActivity.class);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void startFullLandscapePlayActivity(Context context, Drama drama) {
        Intent intent = new Intent(context, FullLandscapePlayActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        context.startActivity(intent);
    }

    public static void startFullLandscapePlayActivityForResult(Activity activity, Drama drama,boolean isStop,int usedTime) {
        Intent intent = new Intent(activity, FullLandscapePlayActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        intent.putExtra(EXTRA_USEDTIME,usedTime);
        intent.putExtra(EXTRA_IS_TO_START,!isStop);
        activity.startActivityForResult(intent,REQUEST_FULL_SCREEN);
    }

    public static void startDramaSelectActivity(Context context) {
        Intent intent = new Intent(context, DramaSelectActivity.class);
        context.startActivity(intent);
    }

    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void startFeedbackActivity(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    public static void startAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public static void startDramaDetailActivity(Context context, Theme theme) {
        Intent intent = new Intent(context, DramaDetailActivity.class);
        intent.putExtra(EXTRA_THEME,theme);
        context.startActivity(intent);
    }

    public static void startDramaEditActivity(Context context, Theme theme) {
        Intent intent = new Intent(context, DramaEditActivity.class);
        intent.putExtra(EXTRA_THEME,theme);
        context.startActivity(intent);
    }

    public static void startDramaPlayActivity(Context context, Theme theme) {
        Intent intent = new Intent(context, DramaPlayActivity.class);
        intent.putExtra(EXTRA_THEME,theme);
        context.startActivity(intent);
    }

    public static void startShareActivity(Context context, String path) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, path);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
