package com.mycanvas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.mycanvas.R;

/**
 * Created by ljw on 2015/8/26.
 * 因为是继承ImageView,所以不用重写onMeasure方法去获取view的宽高
 */
public class MyRoundImageView extends ImageView {

    private float mRound = 0;

    private RectF rectF;

    private Canvas buttomCanvas, showConvas;

    public MyRoundImageView(Context context) {
        super(context);
    }

    public MyRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public MyRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Gson gson = new Gson();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_style_roundImageView);
        Log.e("ljw", "typedArray : " + gson.toJson(typedArray));

        int mRound = typedArray.getDimensionPixelSize(R.styleable.custom_style_roundImageView_custom_round, 0);
        int mBorder = typedArray.getDimensionPixelSize(R.styleable.custom_style_roundImageView_custom_border, 0);
        int mBackground = typedArray.getColor(R.styleable.custom_style_roundImageView_custom_background, 0);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getHeight() <= 0 || getWidth() <= 0) {
            super.onDraw(canvas);
            return;
        }

        mRound = (getHeight() + getWidth()) / 2;

        if (rectF == null)
            rectF = new RectF(0, 0, getWidth(), getHeight());

        Bitmap bottomBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        buttomCanvas.setBitmap(bottomBitmap);

        // ----------------------------------------------

        Bitmap showBitmap = Bitmap.createBitmap(bottomBitmap.getWidth(), bottomBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        showConvas.setBitmap(showBitmap);

        // ----------------------------------------------


    }
}
