package com.solarexsoft.circleprogressbar;

import android.content.res.Resources;

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
}
