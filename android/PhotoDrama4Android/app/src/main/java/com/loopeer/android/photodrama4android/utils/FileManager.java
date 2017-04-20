package com.loopeer.android.photodrama4android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.MD5Util;
import com.loopeer.android.photodrama4android.model.Theme;

import com.loopeer.android.photodrama4android.model.Voice;
import java.io.File;
import java.io.FileInputStream;
import zlc.season.rxdownload2.RxDownload;

import static com.loopeer.android.photodrama4android.utils.PermissionUtils.EXTERNAL_STORAGE_PERMISSIONS;
import static com.loopeer.android.photodrama4android.utils.PermissionUtils.REQUEST_EXTERNAL_STORAGE_PERMISSION;

public class FileManager {
    private static FileManager instance;
    private static String photoDramaPath = Environment.getExternalStorageDirectory() +
        "/photodrama";
    private File audioDir;
    private File tempAudioDir;
    private File videoDir;
    private File dramaDir;
    public final String audioDirPath = "/audio/";
    public final String tempAudioDirPath = "/temp/audio/";
    public final String videoDirPath = "/video/";
    public final String dramaDirPath = "/drama/";
    private String audioPath = photoDramaPath + audioDirPath;
    private String videoPath = photoDramaPath + videoDirPath;
    private String dramaPath = photoDramaPath + dramaDirPath;
    private String tempAudioPath = photoDramaPath + tempAudioDirPath;

    private FileManager() {
        init();
    }

    public void init() {
        if (hasSDCard() && hasExternalStoragePermission(PhotoDramaApp.getAppContext())) {
            audioDir = createFilePath(audioPath);
            videoDir = createFilePath(videoPath);
            dramaDir = createFilePath(dramaPath);
            tempAudioDir = createFilePath(tempAudioPath);
        } else {
            audioDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + audioDirPath);
            videoDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + videoDirPath);
            tempAudioDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + tempAudioPath);
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
        if (null == instance) {
            instance = new FileManager();
        }
        return instance;
    }

    public boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public File getAudioDir() {
        return audioDir;
    }

    public File getVideoDir() {
        return videoDir;
    }

    public File getTempAudioDir() {
        return tempAudioDir;
    }

    public File getDramaDir() {
        return dramaDir;
    }

    public File getDramaPackage(Theme theme) {
        File file = new File(getDramaDir(), getDramaPackageName(theme));
        if (file != null && file.exists() && file.isDirectory()) {
            return file;
        }
        return null;
    }

    public File createDramaPackage(Theme theme) {
        File file = new File(getDramaDir(), getDramaPackageName(theme));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public String getDramaPackageName(Theme theme) {
        return "drama_" + theme.id;
    }

    public String getDramaZipPath(Theme theme) {
        File file = new File(getDramaDir(), getDramaZipName(theme));
        return file.getAbsolutePath();
    }

    public String getDramaZipName(Theme theme) {
        return "drama_" + theme.id + ".zip";
    }

    public File getDir() {
        return createFilePath(photoDramaPath);
    }

    public String createNewAudioFile() {
        return getAudioDir().getPath() + File.separator + System.currentTimeMillis() + ".m4a";
    }

    public String createNewAudioWAVFile() {
        return getAudioDir().getPath() + File.separator + System.currentTimeMillis() + ".wav";
    }

    public String createNewVideoFile() {
        return getVideoDir().getPath() + File.separator + System.currentTimeMillis() + ".mp4";
    }

    public String getDecodeAudioFilePath(MusicClip musicClip) {
        String name = MD5Util.getMD5Str(musicClip.path + "_" + musicClip.musicStartOffset + "_" +
            musicClip.musicSelectedLength);
        return getTempAudioDir().getPath() + File.separator + name;
    }

    public static String getAudioPath(Context context, Voice voice) {
        if (voice == null) return null;
        File[] file = RxDownload.getInstance(context).getRealFiles(voice.voiceUrl);
        if (file != null) {
            return file[0].getAbsolutePath();
        }
        return null;
    }

    public static void deleteFile(File file) {
        if (!file.isDirectory()) {
            file.delete();
            return;
        }
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

    public static void deleteFile(File... files) {
        for (File file : files) {
            deleteFile(file);
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
                ActivityCompat.requestPermissions(activity, EXTERNAL_STORAGE_PERMISSIONS,
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
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
