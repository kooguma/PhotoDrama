package com.loopeer.android.photodrama4android.ui.hepler;


import android.content.Context;
import android.view.View;

import com.loopeer.android.photodrama4android.R;

public class FullBottomLayoutHelper {

    public static void updateBottomLayoutPadding(Context context, View view) {
        int padding = context.getResources().getDimensionPixelSize(R.dimen.xsmall_padding);
        int paddingRight = context.getResources().getDimensionPixelSize(R.dimen.large_padding);
        int paddingLeft = 0;
        view.setPadding(
                paddingLeft, padding, paddingRight, padding);
    }
}
