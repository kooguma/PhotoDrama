package com.loopeer.android.photodrama4android.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.R;

import java.io.File;
import java.util.List;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class ShareUtils {

    public static final String SHARE_TYPE_WEICHAT = "com.tencent.mm.ui.tools.ShareImgUI";
    public static final String SHARE_TYPE_QQ = "com.tencent.mobileqq.activity.JumpActivity";

    public static void startShare(Context context, String type, String filePath) {
        if (TextUtils.isEmpty(type)) {
            startShareMore(context, filePath);
            return;
        }
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("video/*");

        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, PackageManager.MATCH_DEFAULT_ONLY);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.name.contains(type)) {
                    share.putExtra(Intent.EXTRA_STREAM, getFileUri(context, filePath));
                    share.setPackage(info.activityInfo.packageName);
                    share.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    found = true;
                    break;
                }
            }
            if (!found) {
                showToast(R.string.share_no_activity);
                return;
            }
            context.startActivity(share);
        }
    }

    private static Uri getFileUri(Context context, String filePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(filePath));
        } else{
            uri = Uri.fromFile(new File(filePath));
        }
        return uri;
    }

    private static void startShareMore(Context context, String filePath) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("video/*");
        share.putExtra(Intent.EXTRA_STREAM, getFileUri(context, filePath));
        context.startActivity(share);
    }
}
