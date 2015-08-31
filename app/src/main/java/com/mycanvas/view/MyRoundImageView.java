package com.mycanvas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.mycanvas.R;

import java.lang.ref.WeakReference;

/**
 * Created by ljw on 2015/8/31.
 */
public class MyRoundImageView extends ImageView {

    private static final int SHAPE_TYPE_FILLET = 0;     // 圆角
    private static final int SHAPE_TYPE_ROUND = 1;      // 圆形
    private static final int SHAPE_TYPE_ELLIPSE = 2;    // 椭圆

    private Canvas srcCanvas;
    private Bitmap srcBitmap, targetBitmap;
    private Paint mPaint;

    private float mImageViewRadius = 10;
    private int mImageViewType = SHAPE_TYPE_ROUND;

    private WeakReference<Bitmap> mWeakBitmap;

    public MyRoundImageView(Context context) {
        this(context, null);
    }

    public MyRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRoundImageView);
        mImageViewRadius = mTypedArray.getDimensionPixelSize(R.styleable.MyRoundImageView_imageRadius, 10);
        mImageViewType = mTypedArray.getInt(R.styleable.MyRoundImageView_imageType, SHAPE_TYPE_ROUND);
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int mWidth = getWidth();
        int mHeight = getHeight();
        Drawable drawable = getDrawable();
        if (mWidth <= 0 || mHeight <= 0 || drawable == null) {
            super.onDraw(canvas);
            return;
        }

        srcBitmap = (mWeakBitmap == null ? null : mWeakBitmap.get());
        int srcWidth = drawable.getIntrinsicWidth();
        int srcHeight = drawable.getIntrinsicHeight();

        if (srcBitmap == null || srcBitmap.isRecycled()) {
            srcBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            srcCanvas = new Canvas(srcBitmap);

            // 计算压缩比
            float scale = 1.0f;
            if (mImageViewType == SHAPE_TYPE_ROUND) {
                //
                float widthScale = mWidth * 1.0f / srcWidth;
                float heightScale = mHeight * 1.0f / srcHeight;
                scale = Math.max(widthScale, heightScale);
            } else {
                scale = getWidth() * 1.0f / Math.min(srcWidth, srcHeight);
            }

            drawable.setBounds(0, 0, (int) (scale * srcWidth), (int) (scale * srcHeight));
            drawable.draw(srcCanvas);

            if (targetBitmap == null || targetBitmap.isRecycled()) {
                targetBitmap = getBitmap();
            }

            mPaint.reset();
            mPaint.setFilterBitmap(false);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

            srcCanvas.drawBitmap(targetBitmap, 0, 0, mPaint);
            mPaint.setXfermode(null);

            canvas.drawBitmap(srcBitmap, 0, 0, null);

            mWeakBitmap = new WeakReference<Bitmap>(srcBitmap);
        }

        if (srcBitmap != null) {
            mPaint.setXfermode(null);
            canvas.drawBitmap(srcBitmap, 0, 0, mPaint);
        }
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (mImageViewType == SHAPE_TYPE_ROUND) {
            float mSize = getWidth() / 2.0f;
            canvas.drawCircle(mSize, mSize, mSize, paint);
        } else if (mImageViewType == SHAPE_TYPE_FILLET) {
            RectF mRectF = new RectF(0, 0, getWidth(), getHeight());
            canvas.drawRoundRect(mRectF, mImageViewRadius, mImageViewRadius, paint);
        }
        return bitmap;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mImageViewType == SHAPE_TYPE_ROUND) {
            int mSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
            setMeasuredDimension(mSize, mSize);
        }
    }
}
