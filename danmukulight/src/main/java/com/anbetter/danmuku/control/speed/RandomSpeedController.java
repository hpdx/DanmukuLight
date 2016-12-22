package com.anbetter.danmuku.control.speed;

/**
 * Created by android_ls on 2016/12/7.
 */
public final class RandomSpeedController implements SpeedController {

    private final static int RATE = 1000;

    private static float MAX_SPEED = 3.5f;

    private static float MIN_SPEED = 8.5f;

    private float width;

    @Override
    public void setWidthPixels(int width) {
        this.width = width;
    }

    @Override
    public float getSpeed() {
        return (float)(((Math.random() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED)) / RATE) * width;
    }

    public float getMaxSpeed() {
        return MAX_SPEED;
    }

    public float getMinSpeed() {
        return MIN_SPEED;
    }


}
