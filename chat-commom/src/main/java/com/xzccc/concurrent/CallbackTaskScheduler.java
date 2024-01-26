package com.xzccc.concurrent;

import com.google.common.util.concurrent.*;
import com.xzccc.utils.ThreadUtil;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class CallbackTaskScheduler{
    static ListeningExecutorService guavaPool=null;
    static {
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        guavaPool= MoreExecutors.listeningDecorator(jPool);
    }

    public CallbackTaskScheduler(){}

    /**
     * 添加任务
     * @param executeTask
     */
    public static <R> void add(CallbackTask<R> executeTask){
        ListenableFuture<R> future = guavaPool.submit(new Callable<R>() {
            @Override
            public R call() throws Exception {
                R r = executeTask.execute();
                return r;
            }
        });
        Futures.addCallback(future, new FutureCallback<R>() {
            @Override
            public void onSuccess(R result) {
                executeTask.onBack(result);
            }

            @Override
            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        },guavaPool);
    }
}
