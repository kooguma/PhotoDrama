package com.loopeer.android.photodrama4android;


import android.content.Context;
import android.content.Intent;

import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.ui.activity.ImageSegmentEditActivity;
import com.loopeer.android.photodrama4android.ui.activity.ImageSelectActivity;
import com.loopeer.android.photodrama4android.ui.activity.MakeMovieActivity;

public class Navigator {

    public static final String EXTRA_DRAMA = "extra_drama";

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
        context.startActivity(intent);
    }
}
