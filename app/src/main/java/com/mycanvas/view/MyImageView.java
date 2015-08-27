package com.mycanvas.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by ljw on 2015/8/27.
 */
public class MyImageView extends ImageView {

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap iBitmap, targetBitmap;

    public MyImageView(Context context) {
        this(context, null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int borderSize = getWidth();

        // 1.得到原图,放入iBitmap中
        iBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas iCanvas = new Canvas(iBitmap);
        super.onDraw(iCanvas);

        targetBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(targetBitmap);

        // 2.绘制形状
        drawShape();

        // 3.绘制图形
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mCanvas.drawBitmap(iBitmap, 0, 0, mPaint);

        canvas.drawBitmap(targetBitmap, 0, 0, null);
    }

    private void drawShape() {
        int a = Math.min(getWidth(), getHeight());
        float size = a / 2.0f;
        mCanvas.drawCircle(size, size, size, mPaint);
    }
}
