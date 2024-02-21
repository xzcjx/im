package com.xzccc.concurrent;

import com.google.common.util.concurrent.*;
import com.xzccc.utils.ThreadUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Service
public class CallbackTaskScheduler{
    @Autowired
    private MeterRegistry meterRegistry;
    private ListeningExecutorService guavaPool=null;

    public CallbackTaskScheduler(){}

    public void run(){
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        ExecutorService monitor = ExecutorServiceMetrics.monitor(
                meterRegistry,
                jPool,
                "netty.thread.callback.mix"
        );
        guavaPool= MoreExecutors.listeningDecorator(monitor);
    }
    /**
     * 添加任务
     * @param executeTask
     */
    public <R> void add(CallbackTask<R> executeTask){
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
