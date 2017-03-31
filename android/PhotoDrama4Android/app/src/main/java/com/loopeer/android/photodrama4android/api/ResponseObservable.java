package com.loopeer.android.photodrama4android.api;

import android.content.Context;
import android.util.Log;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.BaseActivity;
import com.loopeer.android.photodrama4android.BuildConfig;
import java.io.IOException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class ResponseObservable {

    private static final String TAG = ResponseObservable.class.getSimpleName();
    private static Context sContext;


    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }


    public static <T> Observable<T> unwrap(Observable<BaseResponse<T>> observable) {
        return observable
                .filter(tResponse -> {
                    if (!tResponse.isSuccessed()) {
                        showToast(tResponse.mMsg);
                    }
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format("code: %s, msg: %s", tResponse.mCode, tResponse.mMsg));
                    }
                    return tResponse.isSuccessed();
                })
                .map(tResponse -> tResponse.mData);
    }

    public static <T> Observable<T> unwrap(BaseActivity activity, Observable<BaseResponse<T>> observable) {
        return observable
                .filter(tResponse -> {
                    if (!tResponse.isSuccessed()) {
                        showToast(tResponse.mMsg);
                    }
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format("code: %s, msg: %s", tResponse.mCode, tResponse.mMsg));
                    }
                    return tResponse.isSuccessed();
                })
                .map(tResponse -> tResponse.mData)
                .doOnSubscribe(() -> activity.showProgressLoading(""))
                .doOnTerminate(activity::dismissProgressLoading);
    }


    public static <T> Observable<T> unwrapWithoutResponseFilter(Observable<BaseResponse<T>> observable) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format("error: %s", throwable.getMessage()));
                    }
                    checkNetError(throwable);
                })
                .onErrorResumeNext(Observable.empty())
                .doOnNext(tResponse -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format("code: %s, msg: %s", tResponse.mCode, tResponse.mMsg));
                    }
                })
                .map(tResponse -> tResponse.mData);
    }


    private static boolean checkNetError(Throwable throwable) {
        if (sContext != null && throwable instanceof IOException) {
            showToast("当前网络不可用，请检查网络设置");
            return true;
        }
        return false;
    }
}
