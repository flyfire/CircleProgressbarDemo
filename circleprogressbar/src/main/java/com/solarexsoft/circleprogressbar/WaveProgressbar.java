package com.solarexsoft.circleprogressbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by houruhou on 29/04/2017.
 */

public class WaveProgressbar extends View {

    private static final String TAG = "WaveProgressbar";

    private static final int L2R = 0;
    private static final int R2L = 1;

    private int mDefaultSize;
    private Point mCenterPoint;
    private float mRadius;
    private RectF mRectF;
    private float mDarkWaveOffset;
    private float mLightWaveOffset;
    private boolean isR2L;
    private boolean lockWave;

    private boolean antiAlias;
    private float mMaxValue;
    private float mValue;
    private float mPercent;

    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;

    private Paint mPercentPaint;
    private float mValueSize;
    private int mValueColor;

    private float mCircleWidth;
    private Paint mCirclePaint;
    private int mCircleColor;
    private int mBgCircleColor;

    private Path mWaveLimitPath;
    private Path mWavePath;
    private float mWaveHeight;
    private int mWaveNum;
    private Paint mWavePaint;
    private int mDarkWaveColor;
    private int mLightWaveColor;

    private Point[] mDarkPoints;
    private Point[] mLightPoints;

    private int mAllPointCount;
    private int mHalfPointCount;

    private ValueAnimator mProgressAnimator;
    private long mDarkWaveAnimTime;
    private ValueAnimator mDarkWaveAnimator;
    private long mLightWaveAnimTime;
    private ValueAnimator mLightWaveAnimator;

    public WaveProgressbar(Context context) {
        this(context, null);
    }

    public WaveProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDefaultSize = (int) Utils.dp2px(context.getResources(), Constant.DEFAULT_SIZE);
        mRectF = new RectF();
        mCenterPoint = new Point();

        initAttrs(context, attrs);
        initPaint();
        initPath();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressbar);

        antiAlias = typedArray.getBoolean(R.styleable.WaveProgressbar_antiAlias, true);
        mDarkWaveAnimTime = typedArray.getInt(R.styleable.WaveProgressbar_darkWaveAnimTime,
                Constant.DEFAULT_ANIM_TIME);
        mLightWaveAnimTime = typedArray.getInt(R.styleable.WaveProgressbar_lightWaveAnimTime,
                Constant.DEFAULT_ANIM_TIME);
        mMaxValue = typedArray.getFloat(R.styleable.WaveProgressbar_maxValue, Constant
                .DEFAULT_MAX_VALUE);
        mValue = typedArray.getFloat(R.styleable.WaveProgressbar_value, Constant.DEFAULT_VALUE);
        mValueSize = typedArray.getDimension(R.styleable.WaveProgressbar_valueSize, Constant
                .DEFAULT_VALUE_SIZE);
        mValueColor = typedArray.getColor(R.styleable.WaveProgressbar_valueColor, Color.BLACK);

        mHint = typedArray.getString(R.styleable.WaveProgressbar_hint);
        mHintColor = typedArray.getColor(R.styleable.WaveProgressbar_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.WaveProgressbar_hintSize, Constant
                .DEFAULT_HINT_SIZE);

        mCircleWidth = typedArray.getDimension(R.styleable.WaveProgressbar_circleWidth, Constant
                .DEFAULT_ARC_WIDTH);
        mCircleColor = typedArray.getColor(R.styleable.WaveProgressbar_circleColor, Color.GREEN);
        mBgCircleColor = typedArray.getColor(R.styleable.WaveProgressbar_bgCircleColor, Color
                .WHITE);

        mWaveHeight = typedArray.getDimension(R.styleable.WaveProgressbar_waveHeight, Constant
                .DEFAULT_WAVE_HEIGHT);
        mWaveNum = typedArray.getInt(R.styleable.WaveProgressbar_waveNum, 1);
        mDarkWaveColor = typedArray.getColor(R.styleable.WaveProgressbar_darkWaveColor,
                getResources().getColor(android.R.color.holo_blue_dark));
        mLightWaveColor = typedArray.getColor(R.styleable.WaveProgressbar_lightWaveColor,
                getResources().getColor(android.R.color.holo_green_light));

        isR2L = typedArray.getInt(R.styleable.WaveProgressbar_lightWaveDirect, R2L) == R2L;
        lockWave = typedArray.getBoolean(R.styleable.WaveProgressbar_lockWave, false);

        typedArray.recycle();

    }

    private void initPaint() {
        mHintPaint = new TextPaint();
        mHintPaint.setAntiAlias(antiAlias);
        mHintPaint.setTextSize(mHintSize);
        mHintPaint.setColor(mHintColor);
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(antiAlias);
        mCirclePaint.setStrokeWidth(mCircleWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(antiAlias);
        mWavePaint.setStyle(Paint.Style.FILL);

        mPercentPaint = new Paint();
        mPercentPaint.setTextAlign(Paint.Align.CENTER);
        mPercentPaint.setAntiAlias(antiAlias);
        mPercentPaint.setColor(mValueColor);
        mPercentPaint.setTextSize(mValueSize);
    }

    private void initPath() {
        mWaveLimitPath = new Path();
        mWavePath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Utils.measure(widthMeasureSpec, mDefaultSize), Utils.measure
                (heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSizeChanged: w = " + w + ",h = " + h);
        }
        int minSize = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - 2 *
                (int) mCircleWidth, getMeasuredHeight() - getPaddingTop() - getPaddingBottom() -
                2 * (int) mCircleWidth);
        mRadius = minSize / 2;
        mCenterPoint.x = getMeasuredWidth() / 2;
        mCenterPoint.y = getMeasuredHeight() / 2;
        mRectF.left = mCenterPoint.x - mRadius - mCircleWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + mCircleWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - mCircleWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + mCircleWidth / 2;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSizeChanged:width = " + getMeasuredWidth()
                    + ",height = " + getMeasuredHeight()
                    + ",centerPoint = " + mCenterPoint.toString()
                    + ",radius = " + mRadius
                    + ",rectF = " + mRectF.toString());
        }

        initWavePoints();
        setValue(mValue);
        startWaveAnimator();
    }

    private void initWavePoints() {
        float waveWidth = (mRadius * 2) / mWaveNum;
        mAllPointCount = 8 * mWaveNum + 1;
        mHalfPointCount = mAllPointCount / 2;
        mDarkPoints = getPoint(false, waveWidth);
        mLightPoints = getPoint(isR2L, waveWidth);
    }

    private Point[] getPoint(boolean isR2L, float waveWidth) {
        Point[] points = new Point[mAllPointCount];
        //处理第一个点，即数组中点
        points[mHalfPointCount] = new Point((int) (mCenterPoint.x + (isR2L ? mRadius : -mRadius))
                , mCenterPoint.y);
        //屏幕内的贝塞尔曲线点
        for (int i = mHalfPointCount + 1; i < mAllPointCount; i += 4) {
            float width = points[mHalfPointCount].x + waveWidth * (i / 4 - mWaveNum);
            points[i] = new Point((int) (waveWidth / 4 + width), (int) (mCenterPoint.y -
                    mWaveHeight));
            points[i + 1] = new Point((int) (waveWidth / 2 + width), mCenterPoint.y);
            points[i + 2] = new Point((int) (waveWidth * 3 / 4 + width), (int) (mCenterPoint.y +
                    mWaveHeight));
            points[i + 3] = new Point((int) (waveWidth + width), mCenterPoint.y);
        }

        for (int i = 0; i < mHalfPointCount; i++) {
            int reverse = mAllPointCount - i - 1;
            points[i] = new Point((isR2L ? 2 : 1) * points[mHalfPointCount].x - points[reverse].x,
                    points[mHalfPointCount].y * 2 - points[reverse].y);
        }
//        points[0] = new Point((int) (-waveWidth+mDarkWaveOffset),(int)(mCenterPoint.y+mRadius-2*mPercent*mRadius));
//        points[1] = new Point((int)waveWidth/8, (int)(-mWaveHeight));
//        points[2] = new Point((int)waveWidth/4, 0);
//        points[3] = new Point((int)waveWidth/8, (int)mWaveHeight);
//        points[4] = new Point((int)waveWidth/4,0);
//        points[5] = new Point((int)waveWidth/8, (int)(-mWaveHeight));
//        points[6] = new Point((int)waveWidth/4, 0);
//        points[7] = new Point((int)waveWidth/8, (int)mWaveHeight);
//        points[8] = new Point((int)waveWidth/4, 0);
        return isR2L ? Utils.reverse(points) : points;
    }

    private void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mDarkWaveAnimTime);
    }

    private void startAnimator(float start, float end, long darkWaveAnimTime) {
        mProgressAnimator = ValueAnimator.ofFloat(start, end);
        mProgressAnimator.setDuration(darkWaveAnimTime);
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                if (mPercent == 0.0f || mPercent == 1.0f) {
                    stopWaveAnimator();
                } else {
                    startWaveAnimator();
                }
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate:percent = " + mPercent + ",value = " + mValue);
                }
                invalidate();
            }
        });
        mProgressAnimator.start();
    }

    private void stopWaveAnimator() {
        if (mDarkWaveAnimator != null && mDarkWaveAnimator.isRunning()) {
            mDarkWaveAnimator.cancel();
            mDarkWaveAnimator = null;
        }
        if (mLightWaveAnimator != null && mLightWaveAnimator.isRunning()) {
            mLightWaveAnimator.cancel();
            mLightWaveAnimator = null;
        }
    }

    private void startWaveAnimator() {
        startLightWaveAnimator();
        startDarkWaveAnimator();
    }

    private void startDarkWaveAnimator() {
        if (mDarkWaveAnimator != null && mDarkWaveAnimator.isRunning()) {
            return;
        }
        mDarkWaveAnimator = ValueAnimator.ofFloat(0, 2 * mRadius);
        mDarkWaveAnimator.setDuration(mDarkWaveAnimTime);
        mDarkWaveAnimator.setInterpolator(new LinearInterpolator());
        mDarkWaveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mDarkWaveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDarkWaveOffset = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mDarkWaveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDarkWaveOffset = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mDarkWaveAnimator.start();
    }

    private void startLightWaveAnimator() {
        if (mLightWaveAnimator != null && mLightWaveAnimator.isRunning()) {
            return;
        }
        mLightWaveAnimator = ValueAnimator.ofFloat(0, 2 * mRadius);
        mLightWaveAnimator.setDuration(mLightWaveAnimTime);
        mLightWaveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mLightWaveAnimator.setInterpolator(new LinearInterpolator());
        mLightWaveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLightWaveOffset = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mLightWaveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLightWaveOffset = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mLightWaveAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawLightWave(canvas);
        drawDarkWave(canvas);
        drawProgress(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.save();
        canvas.rotate(270, mCenterPoint.x, mCenterPoint.y);
        int currentAngle = (int) (360 * mPercent);
        mCirclePaint.setColor(mBgCircleColor);
        canvas.drawArc(mRectF, currentAngle, 360 - currentAngle, false, mCirclePaint);
        mCirclePaint.setColor(mCircleColor);
        canvas.drawArc(mRectF, 0, currentAngle, false, mCirclePaint);
        canvas.restore();
    }

    private void drawLightWave(Canvas canvas) {
        mWavePaint.setColor(mLightWaveColor);
        drawWave(canvas, mWavePaint, mLightPoints, isR2L ? -mLightWaveOffset : mLightWaveOffset);
    }

    private void drawDarkWave(Canvas canvas) {
        mWavePaint.setColor(mDarkWaveColor);
        drawWave(canvas, mWavePaint, mDarkPoints, mDarkWaveOffset);
    }

    private void drawWave(Canvas canvas, Paint wavePaint, Point[] points, float waveOffset) {
        mWaveLimitPath.reset();
        mWavePath.reset();
        float height = lockWave ? 0 : mRadius - 2 * mRadius * mPercent;
        mWavePath.moveTo(points[0].x+waveOffset, points[0].y+height);
        for (int i = 1; i < mAllPointCount; i += 2) {
            mWavePath.quadTo(points[i].x + waveOffset, points[i].y + height,
                    points[i + 1].x + waveOffset, points[i + 1].y + height);
        }
        mWavePath.lineTo(points[mAllPointCount - 1].x, points[mAllPointCount - 1].y + height);
        mWavePath.lineTo(points[mAllPointCount - 1].x, mCenterPoint.y + mRadius);
        mWavePath.lineTo(points[0].x, mCenterPoint.y + mRadius);
//        mWavePath.moveTo(points[0].x, points[0].y);
//        for (int i = 1; i <= 8; i+=2) {
//            mWavePath.rQuadTo(points[i].x, points[i].y, points[i+1].x, points[i+1].y);
//        }
        mWavePath.lineTo(getMeasuredWidth(), getMeasuredHeight());
        mWavePath.lineTo(0, getMeasuredHeight());
        mWavePath.close();
        mWavePath.close();
        mWaveLimitPath.addCircle(mCenterPoint.x, mCenterPoint.y, mRadius, Path.Direction.CW);
        mWaveLimitPath.op(mWavePath, Path.Op.INTERSECT);
        canvas.drawPath(mWaveLimitPath, wavePaint);
    }

    private void drawProgress(Canvas canvas) {
        float y = mCenterPoint.y - (mPercentPaint.descent() + mPercentPaint.ascent()) / 2;
        canvas.drawText(String.format("%.0f%%", mPercent * 100), mCenterPoint.x, y, mPercentPaint);
        if (mHint != null) {
            float hintY = mCenterPoint.y * 2 / 3 - (mHintPaint.descent() + mHintPaint.ascent()) / 2;
            canvas.drawText(mHint.toString(), mCenterPoint.x, hintY, mHintPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopWaveAnimator();
        if (mProgressAnimator != null && mProgressAnimator.isRunning()) {
            mProgressAnimator.cancel();
            mProgressAnimator = null;
        }
    }
}

















































