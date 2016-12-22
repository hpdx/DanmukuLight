package com.anbetter.danmuku.control;

import android.graphics.Canvas;
import android.view.View;

import com.anbetter.danmuku.control.dispatcher.DanMuDispatcher;
import com.anbetter.danmuku.control.speed.RandomSpeedController;
import com.anbetter.danmuku.control.speed.SpeedController;
import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.channel.DanMuPoolManager;
import com.anbetter.danmuku.model.painter.DanMuPainter;
import com.anbetter.danmuku.view.IDanMuParent;

import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public final class DanMuController {

    private DanMuPoolManager danMuPoolManager;
    private DanMuDispatcher danMuRandomDispatcher;
    private SpeedController speedController;
    private boolean channelCreated = false;

    public DanMuController(View view) {
        if (speedController == null) {
            speedController = new RandomSpeedController();
        }
        if (danMuPoolManager == null) {
            danMuPoolManager = new DanMuPoolManager(view.getContext(), (IDanMuParent) view);
        }
        if (danMuRandomDispatcher == null) {
            danMuRandomDispatcher = new DanMuDispatcher(view.getContext());
        }
        danMuPoolManager.setDispatcher(danMuRandomDispatcher);
    }

    public void forceSleep() {
        danMuPoolManager.forceSleep();
    }

    public void forceWake() {
        if (danMuPoolManager != null) {
            danMuPoolManager.releaseForce();
        }
    }

    public void setSpeedController(SpeedController speedController) {
        if (speedController != null) {
            this.speedController = speedController;
        }
    }

    public void prepare() {
        danMuPoolManager.startEngine();
    }

    public void addDanMuView(int index, DanMuModel danMuView) {
        danMuPoolManager.addDanMuView(index, danMuView);
    }

    public void jumpQueue(List<DanMuModel> danMuViews) {
        danMuPoolManager.jumpQueue(danMuViews);
    }

    public void addPainter(DanMuPainter danMuPainter, int key) {
        danMuPoolManager.addPainter(danMuPainter, key);
    }

    public boolean isChannelCreated() {
        return channelCreated;
    }

    public void hide(boolean hide) {
        if (danMuPoolManager != null) {
            danMuPoolManager.hide(hide);
        }
    }

    public void hideAll(boolean hideAll) {
        if (danMuPoolManager != null) {
            danMuPoolManager.hideAll(hideAll);
        }
    }

    public void initChannels(Canvas canvas) {
        if (!channelCreated) {
            speedController.setWidthPixels(canvas.getWidth());
            danMuPoolManager.setSpeedController(speedController);
            danMuPoolManager.divide(canvas.getWidth(), canvas.getHeight());
            channelCreated = true;
        }
    }

    public void draw(Canvas canvas) {
        danMuPoolManager.drawDanMus(canvas);
    }

    public void release() {
        if (danMuPoolManager != null) {
            danMuPoolManager.release();
            danMuPoolManager = null;
        }
        if (danMuRandomDispatcher != null) {
            danMuRandomDispatcher.release();
        }
    }
}
