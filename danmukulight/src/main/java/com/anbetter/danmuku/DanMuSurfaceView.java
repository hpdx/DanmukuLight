package com.anbetter.danmuku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.anbetter.danmuku.control.DanMuController;
import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.painter.DanMuPainter;
import com.anbetter.danmuku.view.IDanMuParent;
import com.anbetter.danmuku.view.OnDanMuParentViewTouchCallBackListener;
import com.anbetter.danmuku.view.OnDanMuViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuSurfaceView extends SurfaceView implements IDanMuParent, SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private DanMuController danMuController;
    private ArrayList<OnDanMuViewTouchListener> onDanMuViewTouchListeners = new ArrayList<>();
    private OnDanMuParentViewTouchCallBackListener onDanMuParentViewTouchCallBackListener;

    private boolean isSurfaceCreated = false;

    public DanMuSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        danMuController = new DanMuController(this);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void prepare(Canvas canvas) {
        danMuController.prepare();
        onDanMuViewTouchListeners = new ArrayList<>();
        danMuController.initChannels(canvas);
    }

    private void addDanMuView(int index, final DanMuModel danMuView) {
        if (danMuView == null) {
            return;
        }
        if (danMuController != null) {
            if (danMuView.enableTouch()) {
                onDanMuViewTouchListeners.add(danMuView);
            }
            danMuController.addDanMuView(index, danMuView);
        }
    }

    public void addPainter(DanMuPainter danMuPainter, int key) {
        if (danMuController != null) {
            danMuController.addPainter(danMuPainter, key);
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
                handler.removeMessages(1);
                handler.sendEmptyMessage(1);
                int size = onDanMuViewTouchListeners.size();
                for (int i = 0; i < size; i++) {
                    OnDanMuViewTouchListener onDanMuViewTouchListener = onDanMuViewTouchListeners.get(i);
                    boolean onTouched = onDanMuViewTouchListener.onTouch(event.getX(), event.getY());
                    if (((DanMuModel) onDanMuViewTouchListener).getOnTouchCallBackListener() != null && onTouched) {
                        ((DanMuModel) onDanMuViewTouchListener).getOnTouchCallBackListener().callBack((DanMuModel) onDanMuViewTouchListener);
                        return true;
                    }
                }
                if (hasCanTouchDanMus()) {
                    if (onDanMuParentViewTouchCallBackListener != null) {
                        onDanMuParentViewTouchCallBackListener.hideControlPanel();
                    }
                } else {
                    if (onDanMuParentViewTouchCallBackListener != null) {
                        onDanMuParentViewTouchCallBackListener.callBack();
                    }
                }
                break;
        }
        return true;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (onDanMuViewTouchListeners.size() > 0) {
                        detectHasCanTouchedDanMus();
                        handler.sendEmptyMessageDelayed(1, 100);
                    } else {
                        if (onDetectHasCanTouchedDanMusListener != null) {
                            onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(false);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    public void release() {
        onDetectHasCanTouchedDanMusListener = null;
        onDanMuParentViewTouchCallBackListener = null;
        clear();
        danMuController.release();
        danMuController = null;
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
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

    public interface OnDetectHasCanTouchedDanMusListener {
        void hasNoCanTouchedDanMus(boolean hasDanMus);
    }

    public OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener;

    public void setOnDanMuExistListener(OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener) {
        this.onDetectHasCanTouchedDanMusListener = onDetectHasCanTouchedDanMusListener;
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
    public void add(DanMuModel danMuView) {
        addDanMuView(-1, danMuView);
    }

    @Override
    public void add(int index, DanMuModel danMuView) {
        addDanMuView(index, danMuView);
    }

    @Override
    public void jumpQueue(List<DanMuModel> danMuViews) {
        danMuController.jumpQueue(danMuViews);
    }

    @Override
    public void addAllTouchListener(List<DanMuModel> danMuViews) {
       this.onDanMuViewTouchListeners.addAll(danMuViews);
    }

    public void lockDraw() {
        if (!isSurfaceCreated) {
            return;
        }
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (danMuController != null) {
            danMuController.draw(canvas);
        }

        if (isSurfaceCreated) {
            mSurfaceHolder.unlockCanvasAndPost(canvas);
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

    @Override
    public void clear() {
        onDanMuViewTouchListeners.clear();
    }

    @Override
    public void remove(DanMuModel danMuView) {
        onDanMuViewTouchListeners.remove(danMuView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        Canvas canvas = mSurfaceHolder.lockCanvas();
        prepare(canvas);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreated = false;
    }

}
