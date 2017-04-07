/*
 * Copyright (C) 2016 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loopeer.android.photodrama4android.api.calladapter;

import android.text.TextUtils;
import android.util.Log;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.R;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;
import static zlc.season.rxdownload2.function.Constant.TAG;

final class LocalRxJava2CallAdapter<T> implements CallAdapter<T, Object> {
    private final Type responseType;
    private final Scheduler scheduler;
    private final boolean isAsync;
    private final boolean isResult;
    private final boolean isBody;
    private final boolean isFlowable;
    private final boolean isSingle;
    private final boolean isMaybe;
    private final boolean isCompletable;

    LocalRxJava2CallAdapter(Type responseType, Scheduler scheduler, boolean isAsync, boolean isResult,
                            boolean isBody, boolean isFlowable, boolean isSingle, boolean isMaybe,
                            boolean isCompletable) {
        this.responseType = responseType;
        this.scheduler = scheduler;
        this.isAsync = isAsync;
        this.isResult = isResult;
        this.isBody = isBody;
        this.isFlowable = isFlowable;
        this.isSingle = isSingle;
        this.isMaybe = isMaybe;
        this.isCompletable = isCompletable;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call<T> call) {
        Observable<Response<T>> responseObservable = isAsync
                ? new CallEnqueueObservable<>(call)
                : new CallExecuteObservable<>(call);

        Observable<?> observable;
        if (isResult) {
            observable = new ResultObservable<>(responseObservable);
        } else if (isBody) {
            observable = new BodyObservable<>(responseObservable);
        } else {
            observable = responseObservable;
        }

        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }

        if (isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST)
                    .subscribeOn(Schedulers.io())
                    .doOnError(this::checkNetError)
                    .onErrorResumeNext(Flowable.empty())
                    .doOnNext(this::checkTokenValid)
                    .observeOn(AndroidSchedulers.mainThread());
        }
        if (isSingle) {
            return observable.singleOrError();
        }
        if (isMaybe) {
            return observable.singleElement();
        }
        if (isCompletable) {
            return observable.ignoreElements();
        }
        return observable
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNetError)
                .onErrorResumeNext(Observable.empty())
                .doOnNext(this::checkTokenValid)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void checkNetError(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.d("OkHttp", String.format("error: %s", throwable.getMessage()));
            Observable.just(throwable)
                    .filter(throwable1 -> !TextUtils.isEmpty(throwable1.getMessage()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(e -> Log.e(TAG, e.getMessage())
                    );
        }
        Observable.just(throwable)
                .filter(throwable1 -> throwable1 instanceof IOException)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                e -> showToast(R.string.common_net_error)
        );
    }

    private <R> void checkTokenValid(R r) {
        if (r instanceof BaseResponse) {
            BaseResponse baseResponse = (BaseResponse) r;
            if (baseResponse.mCode == 401 || "Not authenticated".equals(baseResponse.mMsg)) {
                Observable
                        .just("base")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
//                            RxBus.getDefault().send(NotAuthEvent.INSTANCE);
                        });
            }
        }
    }
}
