package com.fenchtose.progressfabdemo.progressfab;

/**
 * Created by Jay Rambhia on 9/13/16.
 */
public class DimensionUtils {

    public static int dpToPx(int dp, float density) {
        return (int)(density * dp);
    }

    public static float dpToPx(float dp, float density) {
        return (density * dp);
    }
}
