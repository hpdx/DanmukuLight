package com.anbetter.danmuku;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.anbetter.danmuku.control.DanMuController;
import com.anbetter.danmuku.control.speed.SpeedController;
import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.view.IDanMuParent;
import com.anbetter.danmuku.view.OnDanMuParentViewTouchCallBackListener;
import com.anbetter.danmuku.view.OnDanMuViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuView extends View implements IDanMuParent {

    private DanMuController danMuController;
    private volatile ArrayList<OnDanMuViewTouchListener> onDanMuViewTouchListeners;
    private OnDanMuParentViewTouchCallBackListener onDanMuParentViewTouchCallBackListener;
    private boolean drawFinished = false;

    private Object lock = new Object();

    public DanMuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void jumpQueue(List<DanMuModel> danMuViews) {
        danMuController.jumpQueue(danMuViews);
    }

    @Override
    public void addAllTouchListener(List<DanMuModel> onDanMuTouchCallBackListeners) {
        this.onDanMuViewTouchListeners.addAll(onDanMuTouchCallBackListeners);
    }

    public DanMuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        onDanMuViewTouchListeners = new ArrayList<>();
        if (danMuController == null) {
            danMuController = new DanMuController(this);
        }
    }

    public void prepare() {
        prepare(null);
    }

    public void prepare(SpeedController speedController) {
        if (danMuController != null) {
            danMuController.setSpeedController(speedController);
            danMuController.prepare();
        }
    }

    public void release() {
        onDetectHasCanTouchedDanMusListener = null;
        onDanMuParentViewTouchCallBackListener = null;
        clear();
        if (danMuController != null) {
            danMuController.release();
        }
        danMuController = null;
    }

    private void addDanMuView(final DanMuModel danMuView) {
        if (danMuView == null) {
            return;
        }
        if (danMuController != null) {
            if (danMuView.enableTouch()) {
                onDanMuViewTouchListeners.add(danMuView);
            }
            danMuController.addDanMuView(-1, danMuView);
        }
    }

    public void setOnDanMuParentViewTouchCallBackListener(OnDanMuParentViewTouchCallBackListener onDanMuParentViewTouchCallBackListener) {
        this.onDanMuParentViewTouchCallBackListener = onDanMuParentViewTouchCallBackListener;
    }

    @Override
    public boolean hasCanTouchDanMus() {
        return onDanMuViewTouchListeners.size() > 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasCanTouchDanMus()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int size = onDanMuViewTouchListeners.size();
                for (int i = 0; i < size; i++) {
                    OnDanMuViewTouchListener onDanMuViewTouchListener = onDanMuViewTouchListeners.get(i);
                    boolean onTouched = onDanMuViewTouchListener.onTouch(event.getX(), event.getY());
                    if (((DanMuModel) onDanMuViewTouchListener).getOnTouchCallBackListener() != null && onTouched) {
                        ((DanMuModel) onDanMuViewTouchListener).getOnTouchCallBackListener().callBack((DanMuModel) onDanMuViewTouchListener);
                        return true;
                    }
                }
                if (!hasCanTouchDanMus()) {
                    if (onDanMuParentViewTouchCallBackListener != null) {
                        onDanMuParentViewTouchCallBackListener.callBack();
                    }
                } else {
                    if (onDanMuParentViewTouchCallBackListener != null) {
                        onDanMuParentViewTouchCallBackListener.hideControlPanel();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void add(DanMuModel danMuView) {
        danMuView.enableMoving(true);
        addDanMuView(danMuView);
    }

    public void lockDraw() {
        if (!danMuController.isChannelCreated()) {
            return;
        }
        synchronized (lock) {
            if (Build.VERSION.SDK_INT >= 16) {
                this.postInvalidateOnAnimation();
            } else {
                this.postInvalidate();
            }
            if ((!drawFinished)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            drawFinished = false;
        }
    }

    @Override
    public void forceSleep() {
        danMuController.forceSleep();
    }

    @Override
    public void forceWake() {
        danMuController.forceWake();
    }

    private void unLockDraw() {
        synchronized (lock) {
            drawFinished = true;
            lock.notifyAll();
        }
    }

    @Override
    public void clear() {
        onDanMuViewTouchListeners.clear();
    }

    @Override
    public void remove(DanMuModel danMuView) {
        onDanMuViewTouchListeners.remove(danMuView);
    }

    public interface OnDetectHasCanTouchedDanMusListener {
        void hasNoCanTouchedDanMus(boolean hasDanMus);
    }

    public void detectHasCanTouchedDanMus() {
        for (int i = 0; i < onDanMuViewTouchListeners.size(); i++) {
            if (!((DanMuModel) onDanMuViewTouchListeners.get(i)).isAlive()) {
                onDanMuViewTouchListeners.remove(i);
                i--;
            }
        }
        if (onDanMuViewTouchListeners.size() == 0) {
            if (onDetectHasCanTouchedDanMusListener != null) {
                onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(false);
            }
        } else {
            if (onDetectHasCanTouchedDanMusListener != null) {
                onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(true);
            }
        }
    }

    @Override
    public void hideNormalDanMuView(boolean hide) {
        danMuController.hide(hide);
    }

    @Override
    public void hideAllDanMuView(boolean hideAll) {
        danMuController.hideAll(hideAll);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        detectHasCanTouchedDanMus();
        if (danMuController != null) {
            danMuController.initChannels(canvas);
            danMuController.draw(canvas);
        }
        unLockDraw();
    }

    @Override
    public void add(int index, DanMuModel danMuView) {
        danMuController.addDanMuView(index, danMuView);
    }

    public OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener;

    public void setOnDanMuExistListener(OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener) {
        this.onDetectHasCanTouchedDanMusListener = onDetectHasCanTouchedDanMusListener;
    }
}
