package com.anbetter.danmuku.model.painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.channel.DanMuChannel;
import com.anbetter.danmuku.model.utils.PaintUtils;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuPainter extends IDanMuPainter {

    protected static TextPaint paint;
    protected static RectF rectF;

    private boolean hide;

    private boolean hideAll;

    static {
        paint = PaintUtils.getPaint();
        rectF = new RectF();
    }

    public DanMuPainter() {
    }

    protected void layout(DanMuModel danMuView, DanMuChannel danMuChannel) {
    }

    private void onLayout(DanMuModel danMuView, DanMuChannel danMuChannel) {
        if (danMuView.isMoving()) {
            layout(danMuView, danMuChannel);
        }
    }

    protected void draw(Canvas canvas, DanMuModel danMuView, DanMuChannel danMuChannel) {
        if (danMuView.textBackground != null) {
            drawTextBackground(danMuView, canvas, danMuChannel);
        }

        if (danMuView.avatar != null) {
            drawAvatar(danMuView, canvas, danMuChannel);
        }

        if(danMuView.avatarStrokes) {
            drawAvatarStrokes(danMuView, canvas, danMuChannel);
        }

        if (danMuView.levelBitmap != null) {
            drawLevel(danMuView, canvas, danMuChannel);
        }

        if (!TextUtils.isEmpty(danMuView.levelText)) {
            drawLevelText(danMuView, canvas, danMuChannel);
        }

        if (!TextUtils.isEmpty(danMuView.text)) {
            drawText(danMuView, canvas, danMuChannel);
        }
    }

    protected void drawAvatar(DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        float top = (int) (danMuView.getY()) + danMuChannel.height / 2 - danMuView.avatarHeight / 2;
        float x = danMuView.getX() + danMuView.marginLeft;

        rectF.set((int) x, top,
                (int) (x + danMuView.avatarWidth),
                top + danMuView.avatarHeight);
        canvas.drawBitmap(danMuView.avatar, null, rectF, paint);
    }

    protected void drawAvatarStrokes(DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        float x = danMuView.getX() + danMuView.marginLeft + danMuView.avatarWidth/2;
        float top = danMuView.getY() + danMuChannel.height / 2;

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((int)x , (int)top, danMuView.avatarHeight/2, paint);
    }

    protected void drawLevel(DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        float top = (int) (danMuView.getY()) + danMuChannel.height / 2 - danMuView.levelBitmapHeight / 2;

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                + danMuView.levelMarginLeft;

        rectF.set((int) x, top,
                (int) (x + danMuView.levelBitmapWidth),
                top + danMuView.levelBitmapHeight);
        canvas.drawBitmap(danMuView.levelBitmap, null, rectF, paint);
    }

    protected void drawLevelText(DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        if (TextUtils.isEmpty(danMuView.levelText)) {
            return;
        }

        paint.setTextSize(danMuView.levelTextSize);
        paint.setColor(danMuView.levelTextColor);
        paint.setStyle(Paint.Style.FILL);

        float top = (int) danMuView.getY()
                + danMuChannel.height / 2
                - paint.ascent() / 2
                - paint.descent() / 2;

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                + danMuView.levelMarginLeft
                + danMuView.levelBitmapWidth/2;

        canvas.drawText(danMuView.levelText.toString(), (int) x, top, paint);
    }

    protected void drawText(DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        if (TextUtils.isEmpty(danMuView.text)) {
            return;
        }

        paint.setTextSize(danMuView.textSize);
        paint.setColor(danMuView.textColor);
        paint.setStyle(Paint.Style.FILL);

        CharSequence text = danMuView.text;
        StaticLayout staticLayout = new StaticLayout(text,
                paint,
                (int) Math.ceil(StaticLayout.getDesiredWidth(text, paint)),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                + danMuView.levelMarginLeft
                + danMuView.levelBitmapWidth
                + danMuView.textMarginLeft;

        float top = (int) (danMuView.getY())
                + danMuChannel.height / 2
                - staticLayout.getHeight()/2;

        canvas.save();
        canvas.translate((int) x, top);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    protected void drawTextBackground(DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        CharSequence text = danMuView.text;
        StaticLayout staticLayout = new StaticLayout(text,
                paint,
                (int) Math.ceil(StaticLayout.getDesiredWidth(text, paint)),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        int textBackgroundHeight = staticLayout.getHeight()
                + danMuView.textBackgroundPaddingTop
                + danMuView.textBackgroundPaddingBottom;

        float top = danMuView.getY()
                + (danMuChannel.height - textBackgroundHeight) / 2;

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                - danMuView.textBackgroundMarginLeft;

        Rect rectF = new Rect((int)x,
                (int)top,
                (int)(x + danMuView.levelMarginLeft
                        + danMuView.levelBitmapWidth
                        + danMuView.textMarginLeft
                        + danMuView.textBackgroundMarginLeft
                        + staticLayout.getWidth()
                        + danMuView.textBackgroundPaddingRight),
                (int)(top + textBackgroundHeight));

        danMuView.textBackground.setBounds(rectF);
        danMuView.textBackground.draw(canvas);
    }

    @Override
    public void requestLayout() {
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void hideNormal(boolean hide) {
        this.hide = hide;
    }

    @Override
    public void hideAll(boolean hideAll) {
        this.hideAll = hideAll;
    }

    @Override
    public void execute(Canvas canvas, DanMuModel danMuView, DanMuChannel danMuChannel) {
        if ((int) danMuView.getSpeed() == 0) {
            danMuView.setAlive(false);
        }

        onLayout(danMuView, danMuChannel);

        if (hideAll) {
            return;
        }

        if (danMuView.getPriority() == DanMuModel.NORMAL && hide) {
            return;
        }

        draw(canvas, danMuView, danMuChannel);
    }

}
