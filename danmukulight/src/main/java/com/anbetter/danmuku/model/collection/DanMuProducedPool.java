package com.anbetter.danmuku.model.collection;

import android.content.Context;

import com.anbetter.danmuku.control.dispatcher.IDanMuDispatcher;
import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.channel.DanMuChannel;
import com.anbetter.danmuku.model.utils.DimensionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuProducedPool {

    private final static int MAX_COUNT_IN_SCREEN = 30;

    private final static int DEFAULT_SINGLE_CHANNEL_HEIGHT = 40;

    private IDanMuDispatcher iDanMuDispatcher;

    private volatile ArrayList<DanMuModel> mixedDanMuViewPendingQueue = new ArrayList<>();

    private volatile ArrayList<DanMuModel> fastDanMuViewPendingQueue = new ArrayList<>();

    private ReentrantLock reentrantLock = new ReentrantLock();

    private DanMuChannel[] danMuChannels;

    private Context context;

    public DanMuProducedPool(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setDanMuDispatcher(IDanMuDispatcher iDanMuDispatcher) {
        this.iDanMuDispatcher = iDanMuDispatcher;
    }

    public void addDanMuView(int index, DanMuModel danMuView) {
        reentrantLock.lock();
        try {
            if (index > -1) {
                mixedDanMuViewPendingQueue.add(index, danMuView);
            } else {
                mixedDanMuViewPendingQueue.add(danMuView);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public void jumpQueue(List<DanMuModel> danMuViews) {
        reentrantLock.lock();
        try {
            fastDanMuViewPendingQueue.addAll(danMuViews);
        } finally {
            reentrantLock.unlock();
        }
    }

    public synchronized ArrayList<DanMuModel> dispatch() {
        if (isEmpty()) {
            return null;
        }
        ArrayList<DanMuModel> danMuViews = fastDanMuViewPendingQueue.size() > 0 ? fastDanMuViewPendingQueue : mixedDanMuViewPendingQueue;
        ArrayList<DanMuModel> validateDanMuViews = new ArrayList<>();
        for (int i = 0; i < (danMuViews.size() > MAX_COUNT_IN_SCREEN ? MAX_COUNT_IN_SCREEN : danMuViews.size()); i++) {
            DanMuModel danMuView = danMuViews.get(i);
            iDanMuDispatcher.dispatch(danMuView, danMuChannels);
            validateDanMuViews.add(danMuView);
            danMuViews.remove(i);
            i--;
        }

        if (validateDanMuViews.size() > 0) {
            return validateDanMuViews;
        }
        return null;
    }

    public boolean isEmpty() {
        return fastDanMuViewPendingQueue.size() == 0 && mixedDanMuViewPendingQueue.size() == 0;
    }

    public void divide(int width, int height) {
        int singleHeight = DimensionUtil.dpToPx(context, DEFAULT_SINGLE_CHANNEL_HEIGHT);
        int count = height / singleHeight;

        danMuChannels = new DanMuChannel[count];
        for (int i = 0; i < count; i++) {
            DanMuChannel danMuChannel = new DanMuChannel();
            danMuChannel.width = width;
            danMuChannel.height = singleHeight;
            danMuChannel.topY = i * singleHeight;
            danMuChannels[i] = danMuChannel;
        }
    }

    public void clear() {
        fastDanMuViewPendingQueue.clear();
        mixedDanMuViewPendingQueue.clear();
        context = null;
    }
}
