package com.rzq.custom.base.utils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * rxjava 工具
 * Created by Administrator on 2018/11/29.
 */

public abstract class RxMianUtli<T> {
    private final Observable<T> mOb;
    private boolean isAsy = true;

    public boolean isAsy() {
        return isAsy;
    }

    public RxMianUtli<T> setAsy(boolean asy) {
        isAsy = asy;
        return this;
    }

    public RxMianUtli() {
        mOb = new Observable<T>() {
            @Override
            protected void subscribeActual(Observer<? super T> observer) {
                initService(observer);
            }
        };
    }

    protected abstract void initService(Observer<? super T> observer);

    public Observer observer;
    public OnNextCall<T> onNextCall;
    public OnNextCall onStartCall;
    public OnNextCall<Throwable> onErroCall;
    public OnNextCall onCompleteCall;

    public RxMianUtli<T> setOnNextCall(OnNextCall<T> onNextCall) {
        this.onNextCall = onNextCall;
        return this;
    }

    public RxMianUtli<T> setOnStartCall(OnNextCall onStartCall) {
        this.onStartCall = onStartCall;
        return this;
    }

    public RxMianUtli<T> setOnErroCall(OnNextCall<Throwable> onErroCall) {
        this.onErroCall = onErroCall;
        return this;
    }

    public RxMianUtli<T> setOnCompleteCall(OnNextCall onCompleteCall) {
        this.onCompleteCall = onCompleteCall;
        return this;
    }

    public RxMianUtli<T> start() {
        observer = new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (onStartCall != null)
                    onStartCall.onNext("onStartCall");
            }

            @Override
            public void onNext(T t) {
                if (onNextCall != null)
                    onNextCall.onNext(t);
            }

            @Override
            public void onError(Throwable e) {
                if (onErroCall != null)
                    onErroCall.onNext(e);
                onComplete();
            }

            @Override
            public void onComplete() {
                if (onCompleteCall != null)
                    onCompleteCall.onNext("onCompleteCall");
            }
        };
        if (isAsy)
            mOb.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        else
            mOb.subscribeOn(Schedulers.trampoline()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

        return this;
    }
}
