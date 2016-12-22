package com.anbetter.danmuku.model.collection;

import android.graphics.Canvas;

import com.anbetter.danmuku.view.IDanMuParent;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuConsumer extends Thread {

    private final static int SLEEP_TIME = 100;

    private boolean forceSleep = false;

    private boolean isStart;

    private volatile WeakReference<IDanMuParent> danMuViewParent;

    private DanMuConsumedPool danMuSharedPool;

    private ReentrantLock lock = new ReentrantLock();

    public DanMuConsumer(DanMuConsumedPool danMuSharedPool, IDanMuParent danMuParent) {
        this.danMuSharedPool = danMuSharedPool;
        this.danMuViewParent = new WeakReference<>(danMuParent);
        isStart = true;
    }

    public void consume(final Canvas canvas) {
        if (danMuSharedPool != null) {
            danMuSharedPool.draw(canvas);
        }
    }

    public void release() {
        isStart = false;
        danMuViewParent.clear();
        interrupt();
        danMuSharedPool = null;
    }

    @Override
    public void run() {
        super.run();
        while (isStart) {
            if (danMuSharedPool.isDrawnQueueEmpty() || forceSleep) {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                lock.lock();
                try {
                    if (danMuViewParent != null && danMuViewParent.get() != null) {
                        danMuViewParent.get().lockDraw();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void forceSleep() {
        forceSleep = true;
    }

    public void releaseForce() {
        forceSleep = false;
    }
}
