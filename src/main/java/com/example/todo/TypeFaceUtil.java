package com.example.todo;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TypeFaceUtil {

    private static Typeface selectedTypeFace;
    private static float selectedFontSize;
    private static int selectedDefaultColor;

    public  static void setSelectedDefaultColor(final int defaultColor) {
        selectedDefaultColor = defaultColor;
    }

    public static void setSelectedFontSize(final float fontSize) {
        selectedFontSize = fontSize;
    }

    public static void setSelectedTypeFace(final Typeface typeface) {
        selectedTypeFace = typeface;
    }

    public static void applyFontToView(final View view) {
        if (null != selectedTypeFace) {

            if (view instanceof TextView) {
                ((TextView) view).setTypeface(selectedTypeFace);
            } else if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;

                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    final View childView = viewGroup.getChildAt(i);

                    applyFontToView(childView);
                }
            }
        }
    }

    public static void applyTextSizeToView(final View view) {
        if (0 != selectedFontSize) {

            if (view instanceof TextView) {
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedFontSize);
            } else if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;

                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    final View childView = viewGroup.getChildAt(i);

                    applyTextSizeToView(childView);
                }
            }
        }
    }

    public static Typeface getSelectedTypeFace() {
        return selectedTypeFace;
    }

    public static float getSelectedFontSize() {
        return selectedFontSize;
    }

    public static int getSelectedDefaultColor() {
        return selectedDefaultColor;
    }
}
