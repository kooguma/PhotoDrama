package com.loopeer.android.photodrama4android.media.utils;

import android.content.Context;
import android.util.Log;
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

    private RxDownload mRxDownload;
    private Disposable mDisposable;

    public AudioFetchHelper(Context context) {
        mRxDownload = RxDownload.getInstance(context);
    }

    public void getAudio(Voice voice,
                         Consumer<DownloadStatus> consumer,
                         Consumer<Throwable> throwable, Action action) {
        final String saveName = voice.name + "_" + voice.id;
        final String savePath = FileManager.getInstance().getAudioDirPath();
        Log.e("TAG", "saveName = " + saveName + " savePath = " + savePath);
        mDisposable = mRxDownload.download(voice.voiceUrl, saveName, savePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer, throwable, action);
    }

    public void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
