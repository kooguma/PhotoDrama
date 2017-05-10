package com.loopeer.android.photodrama4android.media.utils;

import android.content.Context;
import android.util.Log;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.recorder.MediaAudioDecoder;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.utils.FileManager;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.zeroturnaround.zip.ZipUtil;
import zlc.season.rxdownload2.RxDownload;

public class DramaFetchHelper {

    private static final String TAG = "DramaFetchHelper";

    private final static int BUFFER_SIZE = 4096;

    private Disposable mDisposable;

    private Context mContext;

    public DramaFetchHelper(Context context) {
        mContext = context;
    }

    public void getDrama(Theme theme, Consumer<Drama> consumer, Consumer<Throwable> throwableConsumer, Action completeAction) {
        /*Drama cacheDrama = DramaCache.getInstance().getDrama(theme.id);
        if (cacheDrama != null) {
            try {
                preLoadDramaImage(cacheDrama);
                consumer.accept(cacheDrama);
                completeAction.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }*/
        File file = FileManager.getInstance().getDramaPackage(theme);
        unSubscribe();

        if (file == null) {
            String zipPath = FileManager.getInstance().getDramaZipPath(theme);
            String name = FileManager.getInstance().getDramaZipName(theme);
            File zipFilePath = FileManager.getInstance().getDramaDir();
            File zipFile = new File(zipPath);
            if (zipFile.exists()) {
                FileManager.deleteFile(zipFile);
            }
            long startTime = System.currentTimeMillis();
            mDisposable = RxDownload.getInstance(mContext)
                .download(theme.zipLink, name, zipFilePath.getAbsolutePath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                }, throwableConsumer, () -> {
                    Log.e(TAG, "drama download 耗时: " + (System.currentTimeMillis() - startTime));
                    unZipDrama(zipPath, theme, consumer, throwableConsumer, completeAction);
                });

        } else {
            mDisposable = Flowable.fromCallable(() -> {
                long start1 = System.currentTimeMillis();
                Drama drama = ZipUtils.xmlToDrama(file.getAbsolutePath());
                Log.e(TAG, "file exists xmlToDrama 耗时：" + (System.currentTimeMillis() - start1));
                long start2 = System.currentTimeMillis();
                return Flowable.zip(createDramaFlowables(drama), new Function<Object[], Drama>() {
                    @Override public Drama apply(@NonNull Object[] objects) throws Exception {
                        Log.e(TAG, "preLoadDramaImage & decodeMusicClips 耗时：" +
                            (System.currentTimeMillis() - start2));
                        return drama;
                    }
                })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer, throwableConsumer, completeAction);

            })
                .subscribe();
        }
    }

    private void preLoadDramaImage(Drama drama) {
        BitmapFactory.getInstance().clear(drama);
        if (drama.videoGroup.imageClips.size() > 0) {
            BitmapFactory.getInstance()
                .getBitmapFromMemCache(drama.videoGroup.imageClips.get(0).path);
        }
        if (drama.videoGroup.imageClips.size() > 1) {
            BitmapFactory.getInstance()
                .getBitmapFromMemCache(drama.videoGroup.imageClips.get(1).path);
        }
    }

    private void decodeMusicClips(Drama drama) throws IOException {
        if (BuildConfig.DEBUG) {
            if (drama == null) return;
            if (drama.audioGroup == null) return;
            if (drama.audioGroup.musicClips == null || drama.audioGroup.musicClips.size() == 0) {
                return;
            }
            MediaAudioDecoder decoder = null;
            for (MusicClip clip : drama.audioGroup.musicClips) {
                if (decoder == null) {
                    decoder = new MediaAudioDecoder(clip, null);
                } else {
                    decoder.updateDecoder(clip, null);
                }
                long start = System.currentTimeMillis();
                decoder.decode();
                Log.e(TAG, "decode time = " + (System.currentTimeMillis() - start));
            }
        }
    }

    private List<Flowable<Drama>> createDramaFlowables(Drama drama) {
        List<Flowable<Drama>> flows = new ArrayList<>();
        flows.add(createImagePreLoadFlowable(drama));
        flows.addAll(createMusicDecodeFlowables(drama));
        return flows;
    }

    private Flowable<Drama> createImagePreLoadFlowable(Drama drama) {
        return Flowable.fromCallable(new Callable<Drama>() {
            @Override public Drama call() throws Exception {
                preLoadDramaImage(drama);
                return drama;
            }
        })
            .subscribeOn(Schedulers.newThread());
    }

    private List<Flowable<Drama>> createMusicDecodeFlowables(Drama drama) {
        if (drama == null) return null;
        if (drama.audioGroup == null) return null;
        if (drama.audioGroup.musicClips == null) return null;
        if (drama.audioGroup.musicClips.size() == 0) return null;
        final List<Flowable<Drama>> flows = new ArrayList<>();
        for (MusicClip clip : drama.audioGroup.musicClips) {
            Flowable<Drama> flowable = Flowable.fromCallable(new Callable<Drama>() {
                @Override public Drama call() throws Exception {
                    long start = System.currentTimeMillis();
                    MediaAudioDecoder decoder = new MediaAudioDecoder(clip,null);
                    decoder.decode();
                    Log.e(TAG,"key = " + FileManager.getInstance().getDecodeAudioFilePath(clip));
                    Log.e(TAG, "decode time = " + (System.currentTimeMillis() - start));
                    return drama;
                }
            })
                .subscribeOn(Schedulers.newThread());
            flows.add(flowable);
        }
        return flows;
    }

    private void unZipDrama(String zipPath, Theme theme, Consumer<Drama> consumer, Consumer throwableConsumer, Action completeAction) {
        File file = FileManager.getInstance().createDramaPackage(theme);
        mDisposable = Flowable.fromCallable(() -> {
            long start1 = System.currentTimeMillis();
            ZipUtil.unpack(new File(zipPath), file);
            Drama drama = ZipUtils.xmlToDrama(file.getAbsolutePath());
            FileManager.deleteFile(new File(zipPath));
            Log.e(TAG, "unpack & xmlToDrama & delete 耗时：" + (System.currentTimeMillis() - start1));
            long start2 = System.currentTimeMillis();
            preLoadDramaImage(drama);
            return Flowable.zip(createDramaFlowables(drama), new Function<Object[], Drama>() {
                @Override public Drama apply(@NonNull Object[] objects) throws Exception {
                    Log.e(TAG, "preLoadDramaImage & decodeMusicClips 耗时：" +
                        (System.currentTimeMillis() - start2));
                    return drama;
                }
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer,throwableConsumer,completeAction);

        })
            .subscribe();
    }

    public void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public void checkSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
