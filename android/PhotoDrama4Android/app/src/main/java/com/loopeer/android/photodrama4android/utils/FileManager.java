package com.loopeer.android.photodrama4android.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.loopeer.android.photodrama4android.PhotoDramaApp;

import java.io.File;
import java.io.FileInputStream;

import static com.loopeer.android.photodrama4android.utils.PermissionUtils.EXTERNAL_STORAGE_PERMISSIONS;
import static com.loopeer.android.photodrama4android.utils.PermissionUtils.REQUEST_EXTERNAL_STORAGE_PERMISSION;

public class FileManager {
    private static FileManager instance;
    private static String photoDramaPath = Environment.getExternalStorageDirectory() + "/photodrama";
    private static String audioPath = photoDramaPath + "/audio/";
    private static String videoPath = photoDramaPath + "/video/";
    private File audioDir;
    private File videoDir;
    public final String audioDirPath = "/audio/";
    public final String videoDirPath = "/video/";

    private FileManager() {
        init();
    }

    public void init() {
        if (hasSDCard() && hasExternalStoragePermission(PhotoDramaApp.getAppContext())) {
            audioDir = createFilePath(audioPath);
            videoDir = createFilePath(videoPath);
        } else {
            audioDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + audioDirPath);
            videoDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + videoDirPath);
        }
    }

    private File createFilePath(String filePath) {
        return createFilePath(new File(filePath));
    }

    private File createFilePath(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static FileManager getInstance() {
        if (null == instance)
            instance = new FileManager();
        return instance;
    }

    public boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public File getAudioDir() {
        return audioDir;
    }

    public File getVideoDirDir() {
        return videoDir;
    }

    public String createNewAudioFile() {
        return getAudioDir().getPath() + File.separator + System.currentTimeMillis() + ".m4a";
    }

    public String createNewVideoFile() {
        return getVideoDirDir().getPath() + File.separator + System.currentTimeMillis() + ".mp4";
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists() && file.list() != null) {
            for (File item : file.listFiles()) {
                if (item.isDirectory()) {
                    deleteFile(item);
                } else {
                    item.delete();
                }
            }
        }
    }

    public static long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    public void requestPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity, EXTERNAL_STORAGE_PERMISSIONS, REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            init();
        }
    }

    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSIONS[0]);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
