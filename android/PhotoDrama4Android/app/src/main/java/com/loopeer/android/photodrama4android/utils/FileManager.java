package com.loopeer.android.photodrama4android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.DateUtils;
import com.loopeer.android.photodrama4android.media.utils.MD5Util;
import com.loopeer.android.photodrama4android.model.Theme;

import com.loopeer.android.photodrama4android.model.Voice;
import java.io.File;
import java.io.FileInputStream;

import java.io.FilenameFilter;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import retrofit2.http.Field;
import zlc.season.rxdownload2.RxDownload;

import static com.loopeer.android.photodrama4android.utils.PermissionUtils.EXTERNAL_STORAGE_PERMISSIONS;
import static com.loopeer.android.photodrama4android.utils.PermissionUtils.REQUEST_EXTERNAL_STORAGE_PERMISSION;

public class FileManager {
    public final boolean DEBUG = BuildConfig.DEBUG;
    private static FileManager instance;
    private static String photoDramaPath = Environment.getExternalStorageDirectory() +
        "/photodrama";
    private File audioDir;
    private File recordDir;
    private File bgmDir;
    private File effectDir;
    private File tempAudioDir;
    private File videoDir;
    private File dramaDir;
    public final String audioDirPath = "/audio/";
    public final String recordDirPath = "record/";
    public final String bgmDirPath = "bgm/";
    public final String effectDirPath = "effect/";
    public final String tempAudioDirPath = "/temp/audio/";
    public final String videoDirPath = "/video/";
    public final String dramaDirPath = "/drama/";
    private String audioPath = photoDramaPath + audioDirPath;
    private String recordPath = photoDramaPath + audioDirPath + recordDirPath;
    private String bgmPath = photoDramaPath + audioDirPath + bgmDirPath;
    private String effectPath = photoDramaPath + audioDirPath + effectDirPath;
    private String videoPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";
    private String dramaPath = photoDramaPath + dramaDirPath;
    private String tempAudioPath = photoDramaPath + tempAudioDirPath;

    private FileManager() {
        init();
    }

    public void init() {
        if (hasSDCard() && hasExternalStoragePermission(PhotoDramaApp.getAppContext())) {
            audioDir = createFilePath(audioPath);
            recordDir = createFilePath(recordPath);
            bgmDir = createFilePath(bgmPath);
            effectDir = createFilePath(effectPath);
            videoDir = createFilePath(videoPath);
            if (DEBUG) {
                dramaDir = createFilePath(dramaPath);
            } else {
                dramaDir = createFilePath(
                    PhotoDramaApp.getAppContext().getCacheDir() + dramaDirPath);
            }
            tempAudioDir = createFilePath(tempAudioPath);
        } else {
            audioDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + audioDirPath);
            recordDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + recordDirPath);
            bgmDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + bgmDirPath);
            effectDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + effectDirPath);
            videoDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + videoDirPath);
            dramaDir = createFilePath(PhotoDramaApp.getAppContext().getCacheDir() + dramaDirPath);
            tempAudioDir = createFilePath(
                PhotoDramaApp.getAppContext().getCacheDir() + tempAudioPath);
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
        String zipName = getDramaZipName(theme);
        String[] zipNames = zipName.split("\\.");
        return zipNames[0];
    }

    public String getDramaZipPath(Theme theme) {
        File file = new File(getDramaDir(), getDramaZipName(theme));
        return file.getAbsolutePath();
    }

    public String getDramaZipName(Theme theme) {
        if (theme.zipLink == null) return getDefaultDramaZipName(theme);
        String[] paths = theme.zipLink.split("/");
        return paths[paths.length - 1];
    }

    @NonNull
    private String getDefaultDramaZipName(Theme theme) {
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
        return getVideoDir().getPath() + File.separator + DateUtils.getCurrentTimeVideoString() +
            ".mp4";
    }

    public String getDecodeAudioFilePath(MusicClip musicClip) {
        String name = MD5Util.getMD5Str(musicClip.path + "_" + musicClip.musicStartOffset + "_" +
            musicClip.musicSelectedLength);
        return getTempAudioDir().getPath() + File.separator + name;
    }

    public String getAudioDirPath() {
        return audioPath;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public String getBgmPath() {
        return bgmPath;
    }

    public String getEffectPath() {
        return effectPath;
    }

    public List<Voice> getAudioBgmFiles() {
        return getVoiceFiles(getBgmPath());
    }

    public List<Voice> getAudioEffectFiles() {
        return getVoiceFiles(getEffectPath());
    }

    private List<Voice> getVoiceFiles(String path) {
        File[] files = new File(path).listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return !name.equals(".cache");
            }
        });
        List<Voice> voices = new ArrayList<>();
        for (File file : files) {
            Voice v = Voice.fromFile(file);
            voices.add(v);
        }
        return voices;
    }

    private String getAudioPath(Context context, String dirPath, Voice voice) {
        if (voice == null) return null;
        File[] file = RxDownload.getInstance(context)
            .getRealFiles(voice.getSaveName(), dirPath);
        if (file != null) {
            return file[0].getAbsolutePath();
        }
        return null;
    }

    public String getAudioBgmPath(Context context, Voice voice) {
        return getAudioPath(context,getBgmPath(),voice);
    }

    public String getAudioEffectPath(Context context,Voice voice){
        return getAudioPath(context,getEffectPath(),voice);
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

    public static void scanIntoMediaStore(Context context, String filePath, int duration) {
        if (!checkFile(filePath)) {
            return;
        }

        File file = new File(filePath);
        /*Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);*/

        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Video.Media.TITLE, "Camera");
        values.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DURATION, duration);
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Video.Media.SIZE, file.length());
        Uri uri = context.getContentResolver()
            .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    private static boolean checkFile(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}

