package com.anbetter.danmuku.model.channel;

import com.anbetter.danmuku.model.DanMuModel;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuChannel {

    public float speed = 3;
    public int width;
    public int height;
    public int topY;
    public int space = 60;

    public DanMuModel r2lReferenceView;
    public DanMuModel l2rReferenceView;

    public void dispatch(DanMuModel danMuView) {
        if (danMuView.isAttached()) {
            return;
        }

        danMuView.setSpeed(speed);
        if (danMuView.getDisplayType() == DanMuModel.RIGHT_TO_LEFT) {
            int mDeltaX = 0;
            if (r2lReferenceView != null) {
                mDeltaX = (int) (width - r2lReferenceView.getX() - r2lReferenceView.getWidth());
            }
            if (r2lReferenceView == null || !r2lReferenceView.isAlive() || mDeltaX > space) {
                danMuView.setAttached(true);
                r2lReferenceView = danMuView;
            }
        } else if (danMuView.getDisplayType() == DanMuModel.LEFT_TO_RIGHT) {
            int mDeltaX = 0;
            if (l2rReferenceView != null) {
                mDeltaX = (int) l2rReferenceView.getX();
            }
            if (l2rReferenceView == null || !l2rReferenceView.isAlive() || mDeltaX > space) {
                danMuView.setAttached(true);
                l2rReferenceView = danMuView;
            }
        }
    }

}
