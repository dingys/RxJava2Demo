package com.example.jni.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import okhttp3.Request.Builder;


public class MainActivity extends AppCompatActivity {
    private TextView mRxOperatorsText;
    private static final String TAG = "!!MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxOperatorsText = (TextView) findViewById(R.id.text);
        findViewById(R.id.start_internet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                test();
                startOkHttp();

            }
        });
//        OperateUtils.debounceOperate(mRxOperatorsText);
//        OperateUtils.intervalOperate(mRxOperatorsText);
//        OperateUtils.flatMap(mRxOperatorsText);
//        OperateUtils.contactOperate(mRxOperatorsText);
//        OperateUtils.zip(mRxOperatorsText);
//        OperateUtils.mapOperate(mRxOperatorsText);
    }

    public void startOkHttp() {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {
                Builder builder = new Builder()
                        .url("http://api.avatardata.cn/MobilePlace/LookUp?key=ec47b85086be4dc8b5d941f5abd37a4e&mobileNumber=13021671512")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response execute = call.execute();
                e.onNext(execute);
            }
        }).map(new Function<Response, MobileAddress>() {
            @Override
            public MobileAddress apply(@NonNull Response response) throws Exception {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        return new Gson().fromJson(body.string(), MobileAddress.class);
                    }
                }
                return new MobileAddress();
            }
        }).observeOn(Schedulers.io()).doOnNext(new Consumer<MobileAddress>() {
            @Override
            public void accept(@NonNull MobileAddress mobileAddress) throws Exception {
                Log.i(TAG, "onAccept" + mobileAddress.toString() + " " + Thread.currentThread().getName());
                mRxOperatorsText.append("doOnNext 线程:" + Thread.currentThread().getName());

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<MobileAddress>() {
            @Override
            public void accept(@NonNull MobileAddress mobileAddress) throws Exception {
                mRxOperatorsText.append("\nsubscribe 线程:" + Thread.currentThread().getName() + "\n");
                mRxOperatorsText.append("成功:" + mobileAddress.toString() + "\n");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                mRxOperatorsText.append("\nsubscribe 线程:" + Thread.currentThread().getName() + "\n");
                mRxOperatorsText.append("失败：" + throwable.getMessage() + "\n");
            }
        });
    }

    public void create() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();

            }
        }).subscribe(new Observer<Integer>() {
            private Disposable disposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mRxOperatorsText.append("onSubscribe : " + d.isDisposed() + "\n");
                this.disposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                mRxOperatorsText.append("onSubscribe : " + integer + "\n");
                if (integer == 2) {
                    disposable.dispose();
                    mRxOperatorsText.append("dispose:" + integer);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                mRxOperatorsText.append("onSubscribe : " + "Complete" + "\n");
            }
        });
    }

}
