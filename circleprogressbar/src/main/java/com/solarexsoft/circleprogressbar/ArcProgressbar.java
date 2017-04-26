package com.solarexsoft.circleprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by houruhou on 23/04/2017.
 */

public class ArcProgressbar extends View {
    private Paint mPaint;
    private Paint mTextPaint;
    private RectF mRectF = new RectF();

    private float mStrokeWidth;
    private float mSuffixTextSize;
    private float mBottomTextSize;
    private String mBottomText;
    private float mTextSize;
    private int mTextColor;
    private int mProgress;
    private int mMax;
    private int mFinishedStrokeColor;
    private int mUnfinishedStrokeColor;
    private float mArcAngle;
    private String mSuffixText = "%";
    private float mSuffixTextPadding;

    private float mArcBottomHeight;

    private float default_stroke_width;
    private float default_suffix_text_size;
    private float default_bottom_text_size;
    private float default_text_size;
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_max = 100;
    private final int default_finished_color = Color.WHITE;
    private final int default_unfinished_color = Color.rgb(72, 106, 176);
    private final float default_arc_angle = 360 * 0.8f;
    private float default_suffix_padding;

    private int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
    private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
    private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_ARC_ANGLE = "arc_angle";
    private static final String INSTANCE_SUFFIX = "suffix";


    public ArcProgressbar(Context context) {
        this(context, null);
    }

    public ArcProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefault();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressbar);
        initAttributes(attributes);
        attributes.recycle();
        initPaint();
    }


    private void initDefault() {
        default_stroke_width = Utils.dp2px(getResources(), 4);
        default_suffix_text_size = Utils.sp2px(getResources(), 15);
        default_bottom_text_size = Utils.sp2px(getResources(), 10);
        default_text_size = Utils.sp2px(getResources(), 40);
        default_suffix_padding = Utils.dp2px(getResources(), 4);
        min_size = (int) Utils.dp2px(getResources(), 100);

    }


    private void initAttributes(TypedArray attributes) {
        mFinishedStrokeColor = attributes.getColor(R.styleable.ArcProgressbar_arc_finished_color,
                default_finished_color);
        mUnfinishedStrokeColor = attributes.getColor(R.styleable
                .ArcProgressbar_arc_unfinished_color, default_unfinished_color);
        mTextColor = attributes.getColor(R.styleable.ArcProgressbar_arc_text_color,
                default_text_color);
        mTextSize = attributes.getDimension(R.styleable.ArcProgressbar_arc_text_size,
                default_text_size);
        mArcAngle = attributes.getFloat(R.styleable.ArcProgressbar_arc_angle, default_arc_angle);
        mMax = attributes.getInt(R.styleable.ArcProgressbar_arc_max, default_max);
        mProgress = attributes.getInt(R.styleable.ArcProgressbar_arc_progress, 0);
        mStrokeWidth = attributes.getDimension(R.styleable.ArcProgressbar_arc_stroke_width,
                default_stroke_width);
        mSuffixTextSize = attributes.getDimension(R.styleable
                .ArcProgressbar_arc_suffix_text_size, default_suffix_text_size);
        mSuffixText = TextUtils.isEmpty(attributes.getString(R.styleable
                .ArcProgressbar_arc_suffix_text)) ? mSuffixText : attributes.getString(R
                .styleable.ArcProgressbar_arc_suffix_text);
        mSuffixTextPadding = attributes.getDimension(R.styleable
                .ArcProgressbar_arc_suffix_text_padding, default_suffix_padding);
        mBottomTextSize = attributes.getDimension(R.styleable
                .ArcProgressbar_arc_bottom_text_size, default_bottom_text_size);
        mBottomText = attributes.getString(R.styleable.ArcProgressbar_arc_bottom_text);

    }


    private void initPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setColor(mUnfinishedStrokeColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    public float getSuffixTextSize() {
        return mSuffixTextSize;
    }

    public void setSuffixTextSize(float suffixTextSize) {
        mSuffixTextSize = suffixTextSize;
    }

    public float getBottomTextSize() {
        return mBottomTextSize;
    }

    public void setBottomTextSize(float bottomTextSize) {
        mBottomTextSize = bottomTextSize;
    }

    public String getBottomText() {
        return mBottomText;
    }

    public void setBottomText(String bottomText) {
        mBottomText = bottomText;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getFinishedStrokeColor() {
        return mFinishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        mFinishedStrokeColor = finishedStrokeColor;
    }

    public int getUnfinishedStrokeColor() {
        return mUnfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        mUnfinishedStrokeColor = unfinishedStrokeColor;
    }

    public float getArcAngle() {
        return mArcAngle;
    }

    public void setArcAngle(float arcAngle) {
        mArcAngle = arcAngle;
    }

    public String getSuffixText() {
        return mSuffixText;
    }

    public void setSuffixText(String suffixText) {
        mSuffixText = suffixText;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mStrokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            mSuffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            mSuffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
            mBottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            mBottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mMax = bundle.getInt(INSTANCE_MAX);
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            mFinishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            mUnfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            mSuffixText = bundle.getString(INSTANCE_SUFFIX);
            initPaint();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
