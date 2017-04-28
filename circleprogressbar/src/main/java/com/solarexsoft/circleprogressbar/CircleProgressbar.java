package com.solarexsoft.circleprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by houruhou on 28/04/2017.
 */

public class CircleProgressbar extends View {

    private static final String TAG = "CircleProgressbar";

    private Context mContext;

    private int mDefaultSize;
    private boolean antiAlias;

    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    private TextPaint mValuePaint;
    private float mValue;
    private float mMaxValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    private Paint mArcPaint;
    private float mArcWidth;
    private float mStartAngle, mSweepAngle;
    private RectF mRectF;

    private SweepGradient mSweepGradient;
    private int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
    private float mPercent;
    private long mAnimTime;

    private ValueAnimator mValueAnimator;

    private Paint mBgArcPaint;
    private int mBgArcColor;
    private float mBgArcWidth;

    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;


    public CircleProgressbar(Context context) {
        this(context, null);
    }

    public CircleProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = (int) Utils.dp2px(context.getResources(), Constant.DEFAULT_SIZE);
        mValueAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();

        initAttrs(attrs);
        initPaint();
        setValue(mValue);
    }


    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable
                .CircleProgressbar);

        antiAlias = typedArray.getBoolean(R.styleable.CircleProgressbar_antiAlias, Constant
                .ANTI_ALIAS);

        mHint = typedArray.getString(R.styleable.CircleProgressbar_hint);
        mHintColor = typedArray.getColor(R.styleable.CircleProgressbar_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.CircleProgressbar_hintSize, Constant
                .DEFAULT_HINT_SIZE);

        mValue = typedArray.getFloat(R.styleable.CircleProgressbar_value, Constant.DEFAULT_VALUE);
        mMaxValue = typedArray.getFloat(R.styleable.CircleProgressbar_maxValue, Constant
                .DEFAULT_MAX_VALUE);

        mPrecision = typedArray.getInt(R.styleable.CircleProgressbar_precision, 0);
        mPrecisionFormat = Utils.getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.CircleProgressbar_valueColor, Color.BLACK);
        mValueSize = typedArray.getDimension(R.styleable.CircleProgressbar_valueSize, Constant
                .DEFAULT_VALUE_SIZE);

        mUnit = typedArray.getString(R.styleable.CircleProgressbar_unit);
        mUnitColor = typedArray.getColor(R.styleable.CircleProgressbar_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressbar_unitSize, Constant
                .DEFAULT_UNIT_SIZE);

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressbar_arcWidth, Constant
                .DEFAULT_ARC_WIDTH);
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressbar_startAngle, Constant
                .DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressbar_sweepAngle, Constant
                .DEFAULT_SWEEP_ANGLE);

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressbar_bgArcColor, Color.WHITE);
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressbar_bgArcWidth, Constant
                .DEFAULT_ARC_WIDTH);
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable
                .CircleProgressbar_textOffsetPercentInRadius, 0.33f);

        //mPercent = typedArray.getFloat(R.styleable.CircleProgressbar_percent, 0);
        mAnimTime = typedArray.getInt(R.styleable.CircleProgressbar_animTime, Constant
                .DEFAULT_ANIM_TIME);

        int gradientArcColors = typedArray.getResourceId(R.styleable.CircleProgressbar_arcColors,
                0);

        if (gradientArcColors != 0) {
            try {
                int[] gradientColors = getResources().getIntArray(gradientArcColors);
                if (gradientColors.length == 0) {
                    int color = getResources().getColor(gradientArcColors);
                    mGradientColors = new int[2];
                    mGradientColors[0] = color;
                    mGradientColors[1] = color;
                } else if (gradientColors.length == 1) {
                    mGradientColors = new int[2];
                    mGradientColors[0] = gradientColors[0];
                    mGradientColors[1] = gradientColors[1];
                } else {
                    mGradientColors = gradientColors;
                }
            } catch (Resources.NotFoundException e) {
                throw new Resources.NotFoundException("given resources not found");
            }
        }
        typedArray.recycle();
    }

    private void initPaint() {
        mHintPaint = new TextPaint();
        mHintPaint.setAntiAlias(antiAlias);
        mHintPaint.setTextSize(mHintSize);
        mHintPaint.setColor(mHintColor);
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Utils.measure(widthMeasureSpec, mDefaultSize),
                Utils.measure(heightMeasureSpec, mDefaultSize));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onMeasure : width = " + getMeasuredWidth() + ",height = " + getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w = " + w + ",h = " + h);
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) maxArcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) maxArcWidth);
        mRadius = minSize / 2;
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;

        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;

        mValueOffset = mCenterPoint.y + getBaseLineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius +
                getBaseLineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius +
                getBaseLineOffsetFromY(mUnitPaint);
        updateArcPaint();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSizeChanged:w = " + w + ",h = " + h + ",centerPoint = " + mCenterPoint
                    .toString() + ",mRadius = " + mRadius + ",mRectF = " + mRectF.toString());
        }
    }

    private void updateArcPaint() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "updateArcPaint | mGradientColors = " + mGradientColors);
        }
        mSweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, mGradientColors, null);
        mArcPaint.setShader(mSweepGradient);
    }

    private float getBaseLineOffsetFromY(TextPaint valuePaint) {
        return Utils.measureTextHeight(valuePaint) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset,
                mValuePaint);
        if (mHint != null) {
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }
        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        float currentAngle = mSweepAngle * mPercent;
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        canvas.drawArc(mRectF, 0, mSweepAngle, false, mBgArcPaint);
        canvas.drawArc(mRectF, 0, currentAngle, false, mArcPaint);
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision) {
        mPrecision = precision;
    }

    public int[] getGradientColors() {
        return mGradientColors;
    }

    public void setGradientColors(int[] gradientColors) {
        mGradientColors = gradientColors;
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    public void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mValueAnimator = ValueAnimator.ofFloat(start, end);
        mValueAnimator.setDuration(mAnimTime);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent + "," +
                            ",currentAngle = " + (mSweepAngle * mPercent) +
                            ",value = " + mValue);
                }
                invalidate();
            }
        });
        mValueAnimator.start();
    }

}
