package connect.freddybarco.webostv.newwidgetcircle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by Freddy Barco on 7/24/2017.
 */

public class CircularSlider extends View {

    private int mThumbX;
    private int mThumbY;

    private int value;

    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mCircleRadius;

    private Drawable mThumbImage;

    private int mThumbSize;
    private int mThumbColor;
    private int mBorderColor;
    private int[] mBorderGradientColors;
    private int mBorderThickness;
    private double mStartAngle;
    private double mAngle = mStartAngle;
    private boolean mIsThumbSelected = false;

    private Paint mPaint = new Paint();
    private SweepGradient mGradientShader;



    public CircularSlider(Context context) {
        this(context, null);
    }

    public CircularSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularSlider(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    // common initializer method
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0);

        // read all available attributes
        float startAngle = a.getFloat(R.styleable.CircularSlider_start_angle, (float) Math.PI * 2);
        float angle = a.getFloat(R.styleable.CircularSlider_angle, (float) Math.PI * 2);
        int thumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_thumb_size, 30);
        int thumbColor = a.getColor(R.styleable.CircularSlider_thumb_color, Color.BLACK);
        int borderThickness = a.getDimensionPixelSize(R.styleable.CircularSlider_border_thickness, 20);
        int borderColor = a.getColor(R.styleable.CircularSlider_border_color, Color.BLACK);
        String borderGradientColors = a.getString(R.styleable.CircularSlider_border_gradient_colors);
        Drawable thumbImage = a.getDrawable(R.styleable.CircularSlider_thumb_image);


        setmStartAngle(startAngle);
        setmAngle(angle);
        setmBorderThickness(borderThickness);
        setmBorderColor(borderColor);
        if (borderGradientColors != null) {
            setmBorderGradientColors(borderGradientColors.split(";"));
        }
        setmThumbSize(thumbSize);
        setmThumbImage(thumbImage);
        setmThumbColor(thumbColor);

        a.recycle();
    }

    public void setmThumbImage(Drawable ThumbImage) {
        mThumbImage = ThumbImage;
    }


    public void setmThumbSize(int ThumbSize) {
        mThumbSize = ThumbSize;
    }

    public void setmThumbColor(int ThumbColor) {
        mThumbColor = ThumbColor;
    }

    public void setmBorderColor(int BorderColor) {
        mBorderColor = BorderColor;
    }

    public void setmBorderGradientColors(String[] BorderGradientColors) {
        mBorderGradientColors = new int[BorderGradientColors.length];
        for (int i = 0; i < BorderGradientColors.length; i++) {
            mBorderGradientColors[i] = Color.parseColor(BorderGradientColors[i]);
        }
    }

    public void setmBorderThickness(int BorderThickness) {
        mBorderThickness = BorderThickness;
    }

    public void setmStartAngle(double StartAngle) {
        mStartAngle = StartAngle;
    }

    public void setmAngle(double Angle) {
        mAngle = Angle;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // use smaller dimension for calculations (depends on parent size)
        int smallerDim = w > h ? h : w;

        // find circle's rectangle points
        int largestCenteredSquareLeft = (w - smallerDim) / 2;
        int largestCenteredSquareTop = (h - smallerDim) / 2;
        int largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim;
        int largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim;

        // save circle coordinates and radius in fields
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2;
        mCircleRadius = smallerDim / 2 - 40;

        if (mBorderGradientColors != null) {
            mGradientShader = new SweepGradient(mCircleRadius, mCircleRadius, mBorderGradientColors, null);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // outer circle (ring)
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderThickness);
        mPaint.setAntiAlias(true);
        if (mGradientShader != null) {
            mPaint.setShader(mGradientShader);
        }
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius - 20, mPaint);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius + 20, mPaint);

        // find thumb position
        mThumbX = (int) (mCircleCenterX + mCircleRadius * Math.cos(mAngle));
        mThumbY = (int) (mCircleCenterY - mCircleRadius * Math.sin(mAngle));

        if (mThumbImage != null) {
            // draw png
            mThumbImage.setBounds(mThumbX - mThumbSize / 2, mThumbY - mThumbSize / 2, mThumbX + mThumbSize / 2, mThumbY + mThumbSize / 2);
            mThumbImage.draw(canvas);
        } else {
            // draw colored circle
            mPaint.setColor(mThumbColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mThumbX, mThumbY, mThumbSize, mPaint);
        }



        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(50);
        canvas.drawText(Integer.toString(value), mCircleCenterX, mCircleCenterY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // start moving the thumb (this is the first touch)
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x < mThumbX + mThumbSize && x > mThumbX - mThumbSize && y < mThumbY + mThumbSize && y > mThumbY - mThumbSize) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mIsThumbSelected = true;
                    updateSliderState(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // still moving the thumb (this is not the first touch)
                if (mIsThumbSelected) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    updateSliderState(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                // finished moving (this is the last touch)
                getParent().requestDisallowInterceptTouchEvent(false);
                mIsThumbSelected = false;
                break;
            }

        }
        invalidate();
        return true;
    }



    private void updateSliderState(int touchX, int touchY) {
        int distanceX = touchX - mCircleCenterX;
        int distanceY = mCircleCenterY - touchY;
        //noinspection SuspiciousNameCombination
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));


        double brng = Math.atan2(distanceY, distanceX);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise
        mAngle = Math.acos(distanceX / c);
        value = (int)((brng/(18))*5);

        //Log.d(TAG,"Angle : "+ brng);

        if (distanceY < 0) {
            mAngle = -mAngle;
        }

    }


}
