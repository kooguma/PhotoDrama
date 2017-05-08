package com.loopeer.android.photodrama4android.api;


import android.text.TextUtils;
import android.util.Log;

import com.laputapp.api.calladapter.BaseRxJava2CallAdapter;
import com.laputapp.api.calladapter.IApiCache;
import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.R;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Retrofit;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;
import static zlc.season.rxdownload2.function.Constant.TAG;

public class LocalCallAdapter extends BaseRxJava2CallAdapter {

    private static final String TAG = "LocalCallAdapter";

    public LocalCallAdapter(Type responseType, Scheduler scheduler, boolean isAsync, boolean isResult, boolean isBody, boolean isFlowable, boolean isSingle, boolean isMaybe, boolean isCompletable, Annotation[] annotations, Retrofit retrofit, IApiCache iApiCache) {
        super(responseType, scheduler, isAsync, isResult, isBody, isFlowable, isSingle, isMaybe, isCompletable, annotations, retrofit, iApiCache);
    }

    @Override
    protected void doNetError(Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.d("OkHttp", String.format("error: %s", e.getMessage()));
            Observable.just(e)
                    .filter(throwable1 -> !TextUtils.isEmpty(e.getMessage()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(e1 -> Log.e(TAG, e.getMessage())
                    );
        }
        Observable.just(e)
                .filter(throwable1 -> throwable1 instanceof IOException)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                e1 -> showToast(R.string.common_net_error)
        );
    }

    @Override
    protected void doAuthNotValid(BaseResponse baseResponse) {
        if (baseResponse.mCode == 401 || "Not authenticated".equals(baseResponse.mMsg)) {
            Observable
                    .just("base")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
//                            RxBus.getDefault().send(NotAuthEvent.INSTANCE);
                    });
        }
        Log.e(TAG, "doAuthNotValid");
    }
}
