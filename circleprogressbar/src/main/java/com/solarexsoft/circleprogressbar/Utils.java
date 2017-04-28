package com.solarexsoft.circleprogressbar;

import android.content.res.Resources;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by houruhou on 23/04/2017.
 */

public class Utils {

    private Utils() {
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().density;
        return sp * scale;
    }

    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }

    public static int measure(int widthMeasureSpec, int defaultSize) {
        int specSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int specMode = View.MeasureSpec.getMode(widthMeasureSpec);

        int result = defaultSize;
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(specSize, defaultSize);
        }
        return result;
    }

    public static float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (fontMetrics.descent - fontMetrics.ascent);
    }
}
