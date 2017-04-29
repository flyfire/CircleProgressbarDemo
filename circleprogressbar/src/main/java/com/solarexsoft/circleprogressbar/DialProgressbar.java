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

public class DialProgressbar extends View {

    private static final String TAG = "DialProgressbar";

    private Context mContext;

    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;

    private boolean antiAlias;
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    private Paint mValuePaint;
    private int mValueColor;
    private float mMaxValue;
    private float mValue;
    private float mValueSize;
    private float mValueOffset;
    private String mPrecisionFormat;

    private Paint mUnitPaint;
    private float mUnitSize;
    private int mUnitColor;
    private float mUnitOffset;
    private CharSequence mUnit;
    private Paint mArcPaint;
    private float mArcWidth;
    private int mDialIntervalDegree;
    private float mStartAngle, mSweepAngle;
    private RectF mRectF;
    private int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
    private float mPercent = 0f;
    private long mAnimTime;
    private ValueAnimator mAnimator;

    private Paint mBgArcPaint;
    private int mBgArcColor;

    private Paint mDialPaint;
    private float mDialWidth;
    private int mDialColor;

    private int mDefaultSize;

    public DialProgressbar(Context context) {
        this(context, null);
    }

    public DialProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = (int) Utils.dp2px(context.getResources(), Constant.DEFAULT_SIZE);
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(context, attrs);
        initPaint();
        setValue(mValue);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialProgressbar);

        antiAlias = typedArray.getBoolean(R.styleable.DialProgressbar_antiAlias, true);
        mMaxValue = typedArray.getFloat(R.styleable.DialProgressbar_maxValue, Constant
                .DEFAULT_MAX_VALUE);
        mValue = typedArray.getFloat(R.styleable.DialProgressbar_value, Constant.DEFAULT_VALUE);
        mValueSize = typedArray.getDimension(R.styleable.DialProgressbar_valueSize, Constant
                .DEFAULT_VALUE_SIZE);
        mValueColor = typedArray.getColor(R.styleable.DialProgressbar_valueColor, Color.BLACK);
        mDialIntervalDegree = typedArray.getInt(R.styleable.DialProgressbar_dialIntervalDegree, 10);
        int precision = typedArray.getInt(R.styleable.DialProgressbar_precision, 0);
        mPrecisionFormat = Utils.getPrecisionFormat(precision);

        mUnit = typedArray.getString(R.styleable.DialProgressbar_unit);
        mUnitColor = typedArray.getColor(R.styleable.DialProgressbar_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.DialProgressbar_unitSize, Constant
                .DEFAULT_UNIT_SIZE);

        mHint = typedArray.getString(R.styleable.DialProgressbar_hint);
        mHintColor = typedArray.getColor(R.styleable.DialProgressbar_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.DialProgressbar_hintSize, Constant
                .DEFAULT_HINT_SIZE);

        mArcWidth = typedArray.getDimension(R.styleable.DialProgressbar_arcWidth, Constant
                .DEFAULT_ARC_WIDTH);

        mStartAngle = typedArray.getFloat(R.styleable.DialProgressbar_startAngle, Constant
                .DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.DialProgressbar_sweepAngle, Constant
                .DEFAULT_SWEEP_ANGLE);

        mAnimTime = typedArray.getInt(R.styleable.DialProgressbar_animTime, Constant
                .DEFAULT_ANIM_TIME);

        mBgArcColor = typedArray.getColor(R.styleable.DialProgressbar_bgArcColor, Color.GRAY);
        mDialWidth = typedArray.getDimension(R.styleable.DialProgressbar_dialWidth, 2);
        mDialColor = typedArray.getColor(R.styleable.DialProgressbar_dialColor, Color.WHITE);

        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable
                .DialProgressbar_textOffsetPercentInRadius, 0.33f);

        int gradientArcColors = typedArray.getResourceId(R.styleable.DialProgressbar_arcColors, 0);
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
                    mGradientColors[1] = gradientColors[0];
                } else {
                    mGradientColors = gradientColors;
                }
            } catch (Resources.NotFoundException e) {
                throw new Resources.NotFoundException("the give resource not found.");
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

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new Paint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.BUTT);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.BUTT);
        mBgArcPaint.setColor(mBgArcColor);

        mDialPaint = new Paint();
        mDialPaint.setAntiAlias(antiAlias);
        mDialPaint.setColor(mDialColor);
        mDialPaint.setStrokeWidth(mDialWidth);
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
            Log.d(TAG, "onSizeChanged | w = " + w + ",h = " + h);
        }
        int minSize = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - 2 *
                (int) mArcWidth, getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - 2 *
                (int) mArcWidth);
        mRadius = minSize / 2;
        mCenterPoint.x = getMeasuredWidth() / 2;
        mCenterPoint.y = getMeasuredHeight() / 2;
        mRectF.left = mCenterPoint.x - mRadius - mArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + mArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - mArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + mArcWidth / 2;

        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius +
                getBaselineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius +
                getBaselineOffsetFromY(mUnitPaint);
        updateArcPaint();
    }

    private void updateArcPaint() {
        SweepGradient sweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y,
                mGradientColors, null);
        mArcPaint.setShader(sweepGradient);
    }

    private float getBaselineOffsetFromY(Paint valuePaint) {
        return Utils.measureTextHeight(valuePaint) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawDial(canvas);
        drawText(canvas);
    }

    private void drawArc(Canvas canvas) {
        float currentAngle = mSweepAngle * mPercent;
        canvas.save();
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        canvas.drawArc(mRectF, currentAngle, mSweepAngle - currentAngle, false, mBgArcPaint);
        canvas.drawArc(mRectF, 0, currentAngle, false, mArcPaint);
        canvas.restore();
    }

    private void drawDial(Canvas canvas) {
        int total = (int) (mSweepAngle / mDialIntervalDegree);
        canvas.save();
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        for (int i = 0; i < total; i++) {
            canvas.drawLine(mCenterPoint.x + mRadius, mCenterPoint.y, mCenterPoint.x + mRadius +
                    mArcWidth, mCenterPoint.y, mDialPaint);
            canvas.rotate(mDialIntervalDegree, mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset,
                mValuePaint);
        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
        if (mHint != null) {
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }
    }

    private void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
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

    public float getValue() {
        return mValue;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public int getDialIntervalDegree() {
        return mDialIntervalDegree;
    }

    public void setDialIntervalDegree(int dialIntervalDegree) {
        mDialIntervalDegree = dialIntervalDegree;
    }

    public int[] getGradientColors() {
        return mGradientColors;
    }

    public void setGradientColors(int[] gradientColors) {
        mGradientColors = gradientColors;
        updateArcPaint();
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                            + ",currentAngle = " + (mSweepAngle * mPercent)
                            + ",value = " + mValue);
                }
                invalidate();
            }
        });
        mAnimator.start();
    }
}
