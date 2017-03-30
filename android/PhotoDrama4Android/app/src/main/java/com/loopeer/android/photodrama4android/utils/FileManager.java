package com.loopeer.android.photodrama4android.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.loopeer.android.photodrama4android.PhotoDramaApp;

import java.io.File;
import java.io.FileInputStream;

import static com.loopeer.android.photodrama4android.utils.PermissionUtils.EXTERNAL_STORAGE_PERMISSIONS;
import static com.loopeer.android.photodrama4android.utils.PermissionUtils.REQUEST_EXTERNAL_STORAGE_PERMISSION;

public class FileManager {
    private boolean permissionToWriteAccepted = false;

    private static FileManager instance;
    private static String photoDramaPath = Environment.getExternalStorageDirectory() + "/photodrama";
    private static String audioPath = photoDramaPath + "/audio/";
    private File audioDir;
    public final String audioDirPath = "/audio/";

    private FileManager() {
        if (permissionToWriteAccepted)
            init();
    }

    private void init() {
        if (hasSDCard() && permissionToWriteAccepted) {
            audioDir = createFilePath(audioPath);
        } else {
            audioDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + audioDirPath);
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

    public String createNewAudioFile() {
        return getAudioDir().getPath() + File.separator + System.currentTimeMillis() + ".aac";
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
            permissionToWriteAccepted = true;
            init();
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                permissionToWriteAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionToWriteAccepted == true) {
            init();
        }
        return permissionToWriteAccepted;
    }
}
