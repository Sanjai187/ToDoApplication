package com.example.todo.service.impl;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TypeFaceUtil {

    private static Typeface selectedTypeface;
    private static float selectedTextSize;
    private static int selectedDefaultColor;

    public static void setSelectedDefaultColor(int defaultColor) {
        selectedDefaultColor = defaultColor;
    }

    public static void setSelectedTextSize(float textSize) {
        selectedTextSize = textSize;
    }

    public static void setSelectedTypeface(Typeface typeface) {
        selectedTypeface = typeface;
    }

    public static Typeface getSelectedTypeface() {
        return selectedTypeface;
    }

    public static float getSelectedTextSize() {
        return selectedTextSize;
    }

    public static int getSelectedDefaultColor() {
        return selectedDefaultColor;
    }

    public static void applyTypefaceToView(View view) {
        if (selectedTypeface != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(selectedTypeface);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View childView = viewGroup.getChildAt(i);
                    applyTypefaceToView(childView);
                }
            }
        }
    }

    public static void applyTextSizeToView(View view) {
        if (selectedTextSize != 0) {
            if (view instanceof TextView) {
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTextSize);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View childView = viewGroup.getChildAt(i);
                    applyTextSizeToView(childView);
                }
            }
        }
    }
}
