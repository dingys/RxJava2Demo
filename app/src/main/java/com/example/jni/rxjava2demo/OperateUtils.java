package com.example.jni.rxjava2demo;

import android.widget.TextView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by dingyasong on 2017/7/27.
 */

public class OperateUtils {

    //接受的数量按照最少得那个来计算
    public static void zip(final TextView textView) {
        Observable.zip(getIntegerObservable(textView), getStringObservable(textView), new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                textView.append("zip : accept : " + s + "\n");
            }
        });
    }

    private static Observable getStringObservable(final TextView textView) {
        Observable<String> stringObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    textView.append("String emit : A \n");
                    e.onNext("A");
                    textView.append("String emit : B \n");
                    e.onNext("B");
                    textView.append("String emit : C \n");
                    e.onNext("C");
                    textView.append("String emit : D \n");
                    e.onNext("D");
                    textView.append("String emit : E \n");
                    e.onNext("E");
                    textView.append("String onComplete\n");
                    e.onComplete();
                }
            }
        });
        return stringObservable;
    }

    private static Observable getIntegerObservable(final TextView textView) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext(1);
                    textView.append("Integer emit : 1 \n");
                    e.onNext(2);
                    textView.append("Integer emit : 2 \n");
                    e.onNext(3);
                    textView.append("Integer emit : 3 \n");
                    e.onNext(4);
                    textView.append("Integer emit : 4 \n");
//                    e.onNext(5);
//                    textView.append("Integer emit : 5 \n");
                    e.onComplete();
                    textView.append("Integer onComplete\n");
                }
            }
        });
    }

    /**
     * map的操作是将一个Observable通过某种函数关系转换成另外一个Observable
     *
     * @param textView
     */
    public static void mapOperate(final TextView textView) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "This  is return result:" + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                textView.append(s + "\n");
            }
        });
    }

    //将两个Observable合并成一个
    public static void contactOperate(final TextView textView) {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                textView.append(integer + "\n");
            }
        });
    }

    public static void flatmapOperate(final TextView textView) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                textView.append("flatMap : accept : " + s + "\n");
            }
        });
    }

    //contactMap 可以保证顺序
    public static void flatMap(final TextView textView) {
        Observable.just(1, 2, 3).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                return getStringObservable(integer);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                textView.append(s + " " + Thread.currentThread().getName() + "\n");
            }
        });
    }

    public static Observable<String> getStringObservable(final Integer integer) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("getString-->" + integer + " threadName " + Thread.currentThread().getName());
            }
        });
    }

    public static void distinctOperate(final TextView textView) {
        Observable.just(1, 1, 2, 1, 2, 3, 4).distinct().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                textView.append(integer + "\n");
            }
        });
    }


    public static void filterOperate(final TextView textView) {
        Observable.just(1, 1, 2, 1, 2, 3, 4).filter(new Predicate<Integer>() {
            @Override
            public boolean test(@NonNull Integer integer) throws Exception {
                if (integer > 1) {
                    return true;
                }
                return false;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                textView.append(integer + "\n");
            }
        });
    }

    public static void bufferOperate(final TextView textView) {
        Observable.just(1, 2, 3, 4, 5).buffer(3, 2).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(@NonNull List<Integer> integers) throws Exception {
                textView.append("buffer size : " + integers.size() + "\n");
                textView.append("buffer value : ");
                for (Integer i : integers) {
                    textView.append(i + "");
                }
                textView.append("\n");
            }
        });
    }


    public static void timerOperate(final TextView textView) {
        textView.append("timer start : " + TimeUtil.getNowStrTime() + "\n");
        Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                textView.append("timer :" + aLong + " at " + TimeUtil.getNowStrTime() + "\n");
            }
        });

    }

    public static void intervalOperate(final TextView textView) {
        textView.append("timer start : " + TimeUtil.getNowStrTime() + "\n");
        Disposable mDispode = Observable.interval(2, 3, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        textView.append("timer :" + aLong + " at " + TimeUtil.getNowStrTime() + "\n");
                    }
                });

    }


    public static void doOnNextOperate(final TextView textView) {
        Observable.just(1, 2, 2, 3).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                textView.append("doOnNext 保存 " + integer + "成功" + "\n");
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                textView.append("doOnNext :" + integer + "\n");
            }
        });
    }

    public static void debounceOperate(final TextView textView) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                Thread.sleep(500);
                e.onNext(2);
                Thread.sleep(300);
                e.onNext(3);
                Thread.sleep(100);
                e.onNext(4);
                Thread.sleep(605);
                e.onNext(5);
                Thread.sleep(510);
                e.onComplete();
            }
        }).debounce(500,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                textView.append(integer+"\n");
            }
        });
    }


}
