package com.loopeer.android.photodrama4android.media.utils;

import android.content.Context;

import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.utils.FileManager;

import org.zeroturnaround.zip.ZipUtil;
import java.io.File;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import zlc.season.rxdownload2.RxDownload;

public class DramaFetchHelper {
    private Disposable mDisposable;

    private Context mContext;
    public DramaFetchHelper(Context context) {
        mContext = context;
    }

    public void getDrama(Theme theme, Consumer<Drama> consumer) {
        File file = FileManager.getInstance().getDramaPackage(theme);
        unSubscribe();
        if (file == null) {
            String zipPath = FileManager.getInstance().getDramaZipPath(theme);
            mDisposable = RxDownload.getInstance(mContext)
                    .download(theme.zipLink, zipPath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(status -> {
                    }, throwable -> {
                    }, () -> unZipDrama(zipPath, theme, consumer));
        } else {
            mDisposable = Flowable.fromCallable(() -> {
                Drama drama = ZipUtils.xmlToDrama(file.getAbsolutePath());
                return drama;})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(drama -> drama != null)
                    .subscribe(consumer);
        }
    }

    private void unZipDrama(String zipPath, Theme theme, Consumer consumer) {
        File file = FileManager.getInstance().getDramaPackage(theme);
        mDisposable = Flowable.fromCallable(() -> {
            ZipUtil.unpack(new File(zipPath), file);
            Drama drama = ZipUtils.xmlToDrama(file.getAbsolutePath());
            return drama;})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(drama -> drama != null)
                .subscribe(consumer);
    }

    public void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
