package com.anbetter.danmuku.model.utils;

import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Created by android_ls on 2016/12/12.
 */

public class PaintUtils {

    private static TextPaint paint;

    public static TextPaint getPaint() {
        if(paint == null) {
            paint = new TextPaint();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setStrokeWidth(3.5f);
        }
        return paint;
    }

}
