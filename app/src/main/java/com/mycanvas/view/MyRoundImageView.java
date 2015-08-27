package com.mycanvas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.mycanvas.R;

import java.lang.ref.WeakReference;

/**
 * Created by ljw on 2015/8/26.
 */
public class MyRoundImageView extends ImageView {

    // TYPE_CIRCLE:代表圆角图形;TYPE_ROUND:代表圆形
    private int mType;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;

    private Paint mPaint;
    private Bitmap mMaskBitmap;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private WeakReference<Bitmap> mWeakBitmap;

    private static final int BODER_RADIUS_DEFAULT = 10;
    private int mRound = BODER_RADIUS_DEFAULT;

    public MyRoundImageView(Context context) {
        this(context, null);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);  // 防止边缘出现锯齿
    }

    public MyRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_RoundImageview);

        mType = typedArray.getInt(R.styleable.custom_RoundImageview_mType, TYPE_CIRCLE);

        mRound = typedArray.getDimensionPixelSize(R.styleable.custom_RoundImageview_mRound, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        BODER_RADIUS_DEFAULT, getResources()
                                .getDisplayMetrics()));

        //Log.e("ljw", "mType=" + mType + " , mRound=" + mRound);

        typedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();

        Log.e("ljw", "3333~~~~Width=" + getWidth() + " , Height=" + getHeight());


        if (bitmap == null || bitmap.isRecycled()) {

            Drawable drawable = getDrawable();

            if (drawable != null) {
                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                Canvas drawCanvas = new Canvas(bitmap);

                // 获取图片资源的实际宽度和高度,而getWidth()和getHeight()是获取ImageView组件的宽高
                int dWidth = drawable.getIntrinsicWidth();
                int dHeight = drawable.getIntrinsicHeight();

                // 设置图形的缩放比例
                float scale = 1.0f;
                if (mType == TYPE_ROUND) {
                    scale = Math.max(getWidth() * 1.0f / dWidth, getHeight() * 1.0f / dHeight);
                } else
                    scale = getWidth() * 1.0f / Math.min(dWidth, dHeight);

                drawable.setBounds(0, 0, (int) scale * getWidth(), (int) scale * getHeight());
                drawable.draw(drawCanvas);

                if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                    mMaskBitmap = getBitmap();
                }

                mPaint.reset();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(mXfermode);

                drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);

                mPaint.setXfermode(null);

                canvas.drawBitmap(bitmap, 0, 0, null);

                mWeakBitmap = new WeakReference<>(bitmap);
            }
        }

        if (bitmap != null) {
            mPaint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0, 0, mPaint);
        }
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        if (mType == TYPE_ROUND) {
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), mRound, mRound, paint);
        }

        if (mType == TYPE_CIRCLE) {
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, paint);
        }

        return bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.e("ljw", "1111~~~~Width=" + getWidth() + " , Height=" + getHeight());
        // 当需要绘制的形状是圆形时,将图形转换成正方形
        if (mType == TYPE_ROUND) {
            int width = widthMeasureSpec <= heightMeasureSpec ? widthMeasureSpec : heightMeasureSpec;
            Log.e("ljw", "hahah  w=" + width);
            setMeasuredDimension(width, width);
        }
        Log.e("ljw", "2222~~~~Width=" + getWidth() + " , Height=" + getHeight());
    }

    @Override
    public void invalidate() {

        mWeakBitmap = null;

        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }

        super.invalidate();
    }
}
