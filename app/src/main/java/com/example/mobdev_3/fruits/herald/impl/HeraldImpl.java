package com.example.mobdev_3.fruits.herald.impl;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;

import com.example.mobdev_3.fruits.R;
import com.example.mobdev_3.fruits.herald.Herald;

/**
 * Created by
 *
 * @author Peter Bukhal petr.bukhal <at> doconcall.ru
 *         on 13.03.2017.
 */
public class HeraldImpl implements Herald {

    private final Context mContext;
    private final ViewGroup mTargetParent;
    private final HeraldLayout mView;
    private final ContentViewCallback mContentViewCallback;
    private final AccessibilityManager mAccessibilityManager;

    protected HeraldImpl(@NonNull ViewGroup parent, @NonNull View content,
                                     @NonNull ContentViewCallback contentViewCallback) {
        if (parent == null) {
            throw new IllegalArgumentException("Herald must have non-null parent");
        }
        if (content == null) {
            throw new IllegalArgumentException("Herald must have non-null content");
        }
        if (contentViewCallback == null) {
            throw new IllegalArgumentException("Herald must have non-null callback");
        }

        mTargetParent = parent;
        mContentViewCallback = contentViewCallback;
        mContext = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        mView = (HeraldLayout) inflater.inflate(R.layout.layout_herald, mTargetParent, false);
        mView.addView(content);

        ViewCompat.setAccessibilityLiveRegion(mView, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
        ViewCompat.setImportantForAccessibility(mView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        // Make sure that we fit system windows and have a listener to apply any insets
        ViewCompat.setFitsSystemWindows(mView, true);
        ViewCompat.setOnApplyWindowInsetsListener(mView,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        // Copy over the bottom inset as padding so that we're displayed
                        // above the navigation bar
                        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(),
                                v.getPaddingRight(), insets.getSystemWindowInsetBottom());
                        return insets;
                    }
                });

        mAccessibilityManager = (AccessibilityManager)
                mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;

        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    interface ContentViewCallback {
        void animateContentIn(int delay, int duration);
        void animateContentOut(int delay, int duration);
    }

    interface OnLayoutChangeListener {
        void onLayoutChange(View view, int left, int top, int right, int bottom);
    }

    interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View v);
        void onViewDetachedFromWindow(View v);
    }

    static class HeraldLayout extends FrameLayout {

        private HeraldImpl.OnLayoutChangeListener mOnLayoutChangeListener;
        private HeraldImpl.OnAttachStateChangeListener mOnAttachStateChangeListener;

        HeraldLayout(Context context) {
            this(context, null);
        }

        HeraldLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeraldLayout);
            if (a.hasValue(R.styleable.HeraldLayout_elevation)) {
                ViewCompat.setElevation(this, a.getDimensionPixelSize(R.styleable.HeraldLayout_elevation, 0));
            }
            a.recycle();

            setClickable(true);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }

            ViewCompat.requestApplyInsets(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(
                HeraldImpl.OnLayoutChangeListener onLayoutChangeListener) {
            mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(
                HeraldImpl.OnAttachStateChangeListener listener) {
            mOnAttachStateChangeListener = listener;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public boolean isShown() {
        return false;
    }

    @Override
    public void hide() {

    }

}
