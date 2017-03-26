package com.loopeer.android.photodrama4android;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.ui.activity.ImageSegmentEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.MakeMovieActivity;
import com.loopeer.android.photodrama4android.ui.activity.TransitionEditActivity;

public class Navigator {

    public static final String EXTRA_DRAMA = "extra_drama";
    public static final int REQUEST_CODE_DRAMA_IMAGE_EDIT = 1001;
    public static final int REQUEST_CODE_DRAMA_TRANSITION_EDIT = 1002;

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
}
