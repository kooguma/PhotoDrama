package com.loopeer.android.photodrama4android.media.utils;

import android.content.Context;
import android.util.Log;

import com.laputapp.rx.RxBus;
import com.loopeer.android.photodrama4android.event.MusicDownFailEvent;
import com.loopeer.android.photodrama4android.event.MusicDownLoadSuccessEvent;
import com.loopeer.android.photodrama4android.event.MusicDownProgressEvent;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.utils.FileManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

public class AudioFetchHelper {
    private static RxDownload sRxDownload;
    public static final String DOWNLOAD_ING_NAME_SUFFIX = "_ING";
    public static void init(Context context) {
        sRxDownload = RxDownload.getInstance(context);
    }

    public static void getAudio(MusicClip.MusicType type, Voice voice) {
        final String saveName = voice.name + "_" + voice.id + DOWNLOAD_ING_NAME_SUFFIX;
        final String successSaveName = voice.name + "_" + voice.id;
        final String savePath = type == MusicClip.MusicType.BGM ?
                FileManager.getInstance().getBgmPath() :
                FileManager.getInstance().getEffectPath();

        sRxDownload.download(voice.voiceUrl, saveName, savePath)
                .subscribeOn(Schedulers.io())
                .subscribe(downloadStatus -> RxBus.getDefault().send(new MusicDownProgressEvent(voice
                        , downloadStatus.getPercentNumber()))
                        , throwable -> RxBus.getDefault().send(new MusicDownFailEvent(voice, "下载失败：" + throwable.getMessage()))
                        , () -> {
                            FileManager.renameImageFile(savePath + saveName, savePath + successSaveName);
                            RxBus.getDefault().send(new MusicDownLoadSuccessEvent(voice));
                        });
    }
}
