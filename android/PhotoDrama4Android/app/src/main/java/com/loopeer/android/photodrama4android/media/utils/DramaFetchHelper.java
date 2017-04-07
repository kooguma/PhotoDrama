package com.loopeer.android.photodrama4android.media.utils;

import android.content.Context;

import com.loopeer.android.photodrama4android.api.service.SeriesService;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.model.Series;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.utils.FileManager;

import io.reactivex.Observable;
import java.util.List;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import zlc.season.rxdownload2.RxDownload;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaFetchHelper {
    private Disposable mDisposable;

    private Context mContext;

    public DramaFetchHelper(Context context) {
        mContext = context;
    }

    public void getDrama(Theme theme, Consumer<Drama> consumer, Consumer<Throwable> throwableConsumer, Action completeAction) {
        File file = FileManager.getInstance().getDramaPackage(theme);
        unSubscribe();

        if (file == null) {
            String zipPath = FileManager.getInstance().getDramaZipPath(theme);
            String name = FileManager.getInstance().getDramaZipName(theme);
            File zipFilePath = FileManager.getInstance().getDramaDir();

            mDisposable = RxDownload.getInstance(mContext)
                .download(theme.zipLink, name, zipFilePath.getAbsolutePath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {}, throwableConsumer, () -> unZipDrama(zipPath, theme, consumer, throwableConsumer, completeAction));
        } else {
                mDisposable = Flowable.fromCallable(() -> {
                Drama drama = ZipUtils.xmlToDrama(file.getAbsolutePath());
                return drama;
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(drama -> drama != null)
                .subscribe(consumer, throwableConsumer, completeAction);
        }
    }

    private void unZipDrama(String zipPath, Theme theme, Consumer<Drama> consumer, Consumer throwableConsumer, Action completeAction) {
        File file = FileManager.getInstance().createDramaPackage(theme);
        mDisposable = Flowable.fromCallable(() -> {
            ZipUtil.unpack(new File(zipPath), file);
            Drama drama = ZipUtils.xmlToDrama(file.getAbsolutePath());
            FileManager.deleteFile(new File(zipPath));
            return drama;
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(drama -> drama != null)
            .subscribe(consumer, throwableConsumer, completeAction);
    }

    public void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
