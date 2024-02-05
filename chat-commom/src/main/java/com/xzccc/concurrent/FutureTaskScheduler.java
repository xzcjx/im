package com.xzccc.concurrent;

import com.xzccc.utils.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;

public class FutureTaskScheduler {
    static ThreadPoolExecutor mixPool = null;

    static {
        mixPool = ThreadUtil.getMixedTargetThreadPool();
    }

    private static FutureTaskScheduler inst = new FutureTaskScheduler();

    private FutureTaskScheduler() {
    }

    public static void add(ExecuteTask executeTask) {
        mixPool.submit(() -> {
            executeTask.execute();
        });
    }
}
