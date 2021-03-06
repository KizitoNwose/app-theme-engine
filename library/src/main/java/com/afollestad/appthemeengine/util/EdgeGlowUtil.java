package com.afollestad.appthemeengine.util;

import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;

import com.afollestad.appthemeengine.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

/**
 * https://gist.github.com/PDDStudio/caadc623be2b6d9af114
 */
public class EdgeGlowUtil {

    protected EdgeGlowUtil() {
    }

    private static final Class<ScrollView> CLASS_SCROLL_VIEW = ScrollView.class;
    private static final Field SCROLL_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM;

    private static final Class<NestedScrollView> CLASS_NESTED_SCROLL_VIEW = NestedScrollView.class;
    private static final Field NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM;

    private static final Class<RecyclerView> CLASS_RECYCLER_VIEW = RecyclerView.class;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM;

    private static final Class<AbsListView> CLASS_LIST_VIEW = AbsListView.class;
    private static final Field LIST_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM;

    private static final Field EDGE_GLOW_FIELD_EDGE;
    private static final Field EDGE_GLOW_FIELD_GLOW;

    private static final Field EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT;

    static {
        Field edgeGlowTop = null, edgeGlowBottom = null, edgeGlowLeft = null, edgeGlowRight = null;

        for (Field f : CLASS_RECYCLER_VIEW.getDeclaredFields()) {
            switch (f.getName()) {
                case "mTopGlow":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    break;
                case "mBottomGlow":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    break;
                case "mLeftGlow":
                    f.setAccessible(true);
                    edgeGlowLeft = f;
                    break;
                case "mRightGlow":
                    f.setAccessible(true);
                    edgeGlowRight = f;
                    break;
            }
        }

        RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;
        RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT = edgeGlowLeft;
        RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT = edgeGlowRight;

        for (Field f : CLASS_NESTED_SCROLL_VIEW.getDeclaredFields()) {
            switch (f.getName()) {
                case "mEdgeGlowTop":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    break;
                case "mEdgeGlowBottom":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    break;
            }
        }

        NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

        for (Field f : CLASS_SCROLL_VIEW.getDeclaredFields()) {
            switch (f.getName()) {
                case "mEdgeGlowTop":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    break;
                case "mEdgeGlowBottom":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    break;
            }
        }

        SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

        for (Field f : CLASS_LIST_VIEW.getDeclaredFields()) {
            switch (f.getName()) {
                case "mEdgeGlowTop":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    break;
                case "mEdgeGlowBottom":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    break;
            }
        }

        LIST_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Field edge = null, glow = null;

            Class cls = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                try {
                    cls = Class.forName("android.widget.EdgeGlow");
                } catch (ClassNotFoundException e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                }
            } else {
                cls = EdgeEffect.class;

            }

            if (cls != null) {
                for (Field f : cls.getDeclaredFields()) {
                    switch (f.getName()) {
                        case "mEdge":
                            f.setAccessible(true);
                            edge = f;
                            break;
                        case "mGlow":
                            f.setAccessible(true);
                            glow = f;
                            break;
                    }
                }
            }

            EDGE_GLOW_FIELD_EDGE = edge;
            EDGE_GLOW_FIELD_GLOW = glow;
        } else {
            EDGE_GLOW_FIELD_EDGE = null;
            EDGE_GLOW_FIELD_GLOW = null;
        }

        Field efc = null;
        try {
            efc = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
        } catch (NoSuchFieldException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
        EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT = efc;
    }

    @IntDef({ALWAYS, PRE_HONEYCOMB, PRE_KITKAT, PRE_LOLLIPOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeGlowColorApi {
    }

    public static final int ALWAYS = 0;
    /**
     * Replace yellow glow in vanilla, blue glow on Samsung.
     */
    public static final int PRE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
    /**
     * Replace Holo blue glow.
     */
    public static final int PRE_KITKAT = Build.VERSION_CODES.KITKAT;
    /**
     * Replace Holo grey glow.
     */
    public static final int PRE_LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;

    public static void setEdgeGlowColor(AbsListView listView, @ColorInt int color, @EdgeGlowColorApi int when) {
        if (Build.VERSION.SDK_INT < when || when == ALWAYS) {
            setEdgeGlowColor(listView, color);
        }
    }

    public static void setEdgeGlowColor(AbsListView listView, @ColorInt int color) {
        try {
            Object ee;
            ee = LIST_VIEW_FIELD_EDGE_GLOW_TOP.get(listView);
            setEdgeGlowColor(ee, color);
            ee = LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(listView);
            setEdgeGlowColor(ee, color);
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) ex.printStackTrace();
        }
    }

    public static void setEdgeGlowColor(ScrollView scrollView, @ColorInt int color, @EdgeGlowColorApi int when) {
        if (Build.VERSION.SDK_INT < when || when == ALWAYS) {
            setEdgeGlowColor(scrollView, color);
        }
    }

    public static void setEdgeGlowColor(ScrollView scrollView, @ColorInt int color) {
        try {
            Object ee;
            ee = SCROLL_VIEW_FIELD_EDGE_GLOW_TOP.get(scrollView);
            setEdgeGlowColor(ee, color);
            ee = SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(scrollView);
            setEdgeGlowColor(ee, color);
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) ex.printStackTrace();
        }
    }

    public static void setEdgeGlowColor(NestedScrollView scrollView, @ColorInt int color, @EdgeGlowColorApi int when) {
        if (Build.VERSION.SDK_INT < when || when == ALWAYS) {
            setEdgeGlowColor(scrollView, color);
        }
    }

    public static void setEdgeGlowColor(NestedScrollView scrollView, @ColorInt int color) {
        try {
            Object ee;
            ee = NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP.get(scrollView);
            setEdgeGlowColor(ee, color);
            ee = NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(scrollView);
            setEdgeGlowColor(ee, color);
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) ex.printStackTrace();
        }
    }

    public static void setEdgeGlowColor(RecyclerView scrollView, @ColorInt int color, @Nullable RecyclerView.OnScrollListener scrollListener, @EdgeGlowColorApi int when) {
        if (Build.VERSION.SDK_INT < when || when == ALWAYS) {
            setEdgeGlowColor(scrollView, color);
        }
    }

    public static void setEdgeGlowColor(RecyclerView scrollView, final @ColorInt int color, @Nullable RecyclerView.OnScrollListener scrollListener) {
        if (scrollListener == null) {
            scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    EdgeGlowUtil.setEdgeGlowColor(recyclerView, color, this);
                }
            };
            scrollView.addOnScrollListener(scrollListener);
        }
        try {
            Object ee;
            ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP.get(scrollView);
            setEdgeGlowColor(ee, color);
            ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(scrollView);
            setEdgeGlowColor(ee, color);
            ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT.get(scrollView);
            setEdgeGlowColor(ee, color);
            ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT.get(scrollView);
            setEdgeGlowColor(ee, color);
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) ex.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setEdgeGlowColor(Object edgeEffect, @ColorInt int color) {
        if (edgeEffect instanceof EdgeEffectCompat) {
            // EdgeEffectCompat
            try {
                EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT.setAccessible(true);
                edgeEffect = EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT.get(edgeEffect);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }

        if (edgeEffect == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // EdgeGlow
            try {
                EDGE_GLOW_FIELD_EDGE.setAccessible(true);
                final Drawable mEdge = (Drawable) EDGE_GLOW_FIELD_EDGE.get(edgeEffect);
                EDGE_GLOW_FIELD_GLOW.setAccessible(true);
                final Drawable mGlow = (Drawable) EDGE_GLOW_FIELD_GLOW.get(edgeEffect);
                mEdge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mEdge.setCallback(null); // free up any references
                mGlow.setCallback(null); // free up any references
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            // EdgeEffect
            ((EdgeEffect) edgeEffect).setColor(color);
        }
    }
}
