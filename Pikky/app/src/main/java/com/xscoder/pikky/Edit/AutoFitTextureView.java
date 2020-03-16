package com.xscoder.pikky.Edit;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved


 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.screenWidth;


public class AutoFitTextureView extends TextureView {


    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private boolean mWithMargin = false;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


        public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }



    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int margin = (height - width) / 2;

        if(!mWithMargin) {
            mWithMargin = true;
            ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) getLayoutParams();
            margins.bottomMargin = -margin;
            margins.leftMargin = 0;
            margins.rightMargin = 0;
            setLayoutParams(margins);
        }

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
        Log.i(TAG, "TEXTUREVIEW MEASURE: W: " + width + " H: " + height + " MARGIN: " + margin );

    }


}// ./ end

