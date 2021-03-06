package com.loopeer.android.photodrama4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Advert;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.model.Series;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.activity.AboutActivity;
import com.loopeer.android.photodrama4android.ui.activity.AddMusicClipActivity;
import com.loopeer.android.photodrama4android.ui.activity.BgmMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaDetailActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaPlayActivity;
import com.loopeer.android.photodrama4android.ui.activity.DramaSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.FeedbackActivity;
import com.loopeer.android.photodrama4android.ui.activity.FullLandscapePlayActivity;
import com.loopeer.android.photodrama4android.ui.activity.GuideActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSegmentEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.MainActivity;
import com.loopeer.android.photodrama4android.ui.activity.MakeMovieActivity;
import com.loopeer.android.photodrama4android.ui.activity.MusicDownloadActivity;
import com.loopeer.android.photodrama4android.ui.activity.RecordMusicActivity;
import com.loopeer.android.photodrama4android.ui.activity.SettingActivity;
import com.loopeer.android.photodrama4android.ui.activity.ShareActivity;
import com.loopeer.android.photodrama4android.ui.activity.SoundEffectActivity;
import com.loopeer.android.photodrama4android.ui.activity.SubtitleEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.TestMusicSelectedActivity;
import com.loopeer.android.photodrama4android.ui.activity.TextInputActivity;
import com.loopeer.android.photodrama4android.ui.activity.TransitionEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.WebActivity;

import static android.content.Intent.CATEGORY_ALTERNATIVE;
import static android.content.Intent.EXTRA_TITLE;

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
    public static final int REQUEST_CODE_DRAMA_MAKE_EDIT = 10011;
    public static final String EXTRA_THEME = "extra_theme";
    public static final String EXTRA_SERIES = "extra_series";
    public static final String EXTRA_SERIES_ID = "extra_series_id";
    public static final String EXTRA_USEDTIME = "extra_usedtime";
    public static final String EXTRA_IS_TO_START = "extra_is_to_start";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_URL = "extra_url";

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

    public static void startDramaEditItemActivity(Context context, Drama drama, Class zclass) {
        Intent intent = new Intent(context, zclass);
        intent.putExtra(EXTRA_DRAMA, drama);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DRAMA_MAKE_EDIT);
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

    public static void startFullLandscapePlayActivityForResult(Activity activity, Drama drama, boolean isStop, int usedTime) {
        Intent intent = new Intent(activity, FullLandscapePlayActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        intent.putExtra(EXTRA_USEDTIME, usedTime);
        intent.putExtra(EXTRA_IS_TO_START, !isStop);
        activity.startActivityForResult(intent, REQUEST_FULL_SCREEN);
    }

    public static void startFullLandscapePlayActivityForResult(Activity activity, Drama drama, boolean isStop, int usedTime, Theme theme) {
        Intent intent = new Intent(activity, FullLandscapePlayActivity.class);
        intent.putExtra(EXTRA_DRAMA, drama);
        intent.putExtra(EXTRA_USEDTIME, usedTime);
        intent.putExtra(EXTRA_IS_TO_START, !isStop);
        intent.putExtra(EXTRA_THEME, theme);
        activity.startActivityForResult(intent, REQUEST_FULL_SCREEN);
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
        intent.putExtra(EXTRA_THEME, theme);
        context.startActivity(intent);
    }

    public static void startDramaDetailActivity(Context context, Series series) {
        Intent intent = new Intent(context, DramaDetailActivity.class);
        intent.putExtra(EXTRA_SERIES, series);
        context.startActivity(intent);
    }

    public static void startDramaDetailActivity(Context context, String seriesId) {
        Intent intent = new Intent(context, DramaDetailActivity.class);
        intent.putExtra(EXTRA_SERIES_ID, seriesId);
        context.startActivity(intent);
    }

    public static void startDramaEditActivity(Context context, Theme theme) {
        Intent intent = new Intent(context, DramaEditActivity.class);
        intent.putExtra(EXTRA_THEME, theme);
        context.startActivity(intent);
    }

    public static void startDramaPlayActivity(Context context, Theme theme) {
        Intent intent = new Intent(context, DramaPlayActivity.class);
        intent.putExtra(EXTRA_THEME, theme);
        context.startActivity(intent);
    }

    public static void startShareActivity(Context context, String path) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, path);
        context.startActivity(intent);
    }

    public static void startShareActivity(Context context, String path, Theme theme) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, path);
        intent.putExtra(EXTRA_THEME, theme);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startGuideActivity(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    public static void startMusicDownloadActivity(Context context, MusicClip.MusicType type, Category category) {
        Intent intent = new Intent(context, MusicDownloadActivity.class);
        intent.putExtra(Navigator.EXTRA_CATEGORY, category);
        intent.putExtra(Navigator.EXTRA_MUSIC_CLIP, type);
        context.startActivity(intent);
    }

    public static void addAddMusicActivity(Context context, MusicClip.MusicType type) {
        Intent intent = new Intent(context, AddMusicClipActivity.class);
        intent.putExtra(Navigator.EXTRA_MUSIC_CLIP, type);
        ((Activity) context).startActivityForResult(intent, type == MusicClip.MusicType.BGM ?
                                                            REQUEST_CODE_DRAMA_SOUND_BGM_SELECT :
                                                            REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT);
    }

    public static void startWebActivity(Context context, String url) {
        Intent i = new Intent(context, WebActivity.class);
        i.putExtra(EXTRA_URL, url);
        context.startActivity(i);
    }

    public static void startWebActivity(Context context, String url, @StringRes int stringRes) {
        Intent i = new Intent(context, WebActivity.class);
        i.putExtra(EXTRA_URL, url);
        i.putExtra(Intent.EXTRA_TITLE, context.getString(stringRes));
        context.startActivity(i);
    }

    public static void startWebActivity(Context context, String url, String title) {
        Intent i = new Intent(context, WebActivity.class);
        i.putExtra(EXTRA_URL, url);
        i.putExtra(EXTRA_TITLE, title);
        context.startActivity(i);
    }

    public static void startActivityFromDramaDetailAdverts(Context context, Advert advert) {
        if (advert.relType == Advert.REL_TYPE_URL) {
            Navigator.startWebActivity(context,advert.relValue,R.string.label_detail);
        } else if (advert.relType == Advert.REL_TYPE_SERIES) {
            Navigator.startDramaDetailActivity(context,advert.relValue);
        } else {
            //do nothing
        }
    }

}
