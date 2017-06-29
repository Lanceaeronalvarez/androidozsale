package com.mysale.genie.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by smartwave on 26/09/16.
 *
 * Modified Bartosz Lipinski's ViewPropertyObjectAnimator class to become my Builder class in this AnimationEngine
 * <ref>https://github.com/blipinsk/ViewPropertyObjectAnimator</ref>
 *
 * Modifications:
 *
 * added support for backgroundColor and textColor animations
 *
 * modified ArrayMap<Property<View, Float>, PropertyValuesHolder> mPropertyHoldersMap
 *  to ArrayMap<Property<View, ?>, PropertyValuesHolder> mPropertyHoldersMap to allow these color animations
 *
 * wrapped alpha() to become fadeIn() and fadeOut()
 */


public class AnimationEngine {
    private ObjectAnimator anim;

    public AnimationEngine(ObjectAnimator animator) {
        anim = animator;
    }

    public void start() {
        anim.start();
    }

    public ObjectAnimator getAnimation() {
        return anim;
    }

    public static void playAnimationSequence(ValueAnimator... animatorList) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (animatorList.length < 2) return;

        for (int i = 1; i < animatorList.length; i++) {
            animatorSet.play(animatorList[i]).after(animatorList[i - 1]);
        }

        animatorSet.start();
    }

    public static class Builder {

        private WeakReference<View> mView;
        private long mDuration = -1;
        private long mStartDelay = -1;
        private boolean mWithLayer = false;
        private Interpolator mInterpolator;
        private List<Animator.AnimatorListener> mListeners = new ArrayList<>();
        private List<ValueAnimator.AnimatorUpdateListener> mUpdateListeners = new ArrayList<>();
        private List<Animator.AnimatorPauseListener> mPauseListeners = new ArrayList<>();
        private ArrayMap<Property<View, ?>, PropertyValuesHolder> mPropertyHoldersMap = new ArrayMap<>();
        private MarginChangeListener mMarginListener;
        private DimensionChangeListener mDimensionListener;
        private PaddingChangeListener mPaddingListener;
        private ScrollChangeListener mScrollListener;
        private PercentChangeListener mPercentListener;

        private Property<View, Drawable> dummyTextColorProp = new Property<View, Drawable>(Drawable.class, "dummy") {
            @Override
            public Drawable get(View object) {
                return null;
            }
        };
        private Property<View, Drawable> dummyBackgroundColorProp = new Property<View, Drawable>(Drawable.class, "dummy") {
            @Override
            public Drawable get(View object) {
                return null;
            }
        };

        private Builder(View view) {
            mView = new WeakReference<View>(view);
        }

        public static Builder animate(View view) {
            return new Builder(view);
        }

        private void animateProperty(Property<View, Float> property, float toValue) {
            if (hasView()) {
                float fromValue = property.get(mView.get());
                animatePropertyBetween(property, fromValue, toValue);
            }
        }

        private void animatePropertyBy(Property<View, Float> property, float byValue) {
            if (hasView()) {
                float fromValue = property.get(mView.get());
                float toValue = fromValue + byValue;
                animatePropertyBetween(property, fromValue, toValue);
            }
        }

        private void animatePropertyBetween(Property<View, Float> property, float fromValue, float toValue) {
            mPropertyHoldersMap.remove(property); //if the same property is assigned again, we want to override it
            mPropertyHoldersMap.put(property, PropertyValuesHolder.ofFloat(property, fromValue, toValue));
        }

        //textColor and backgroundColor doesn't use animateProperty() call which is only for float values
        //since these two accept integer values of colors to be animated.

        private Builder textColor(Integer... colorValues) {
            mPropertyHoldersMap.remove(dummyTextColorProp); //if the same property is assigned again, we want to override it
            mPropertyHoldersMap.put(dummyTextColorProp, PropertyValuesHolder.ofObject("textColor", new ArgbEvaluator(), (Object[]) colorValues));
            return this;
        }

        private Builder backgroundColor(Integer... colorValues) {
            mPropertyHoldersMap.remove(dummyBackgroundColorProp); //if the same property is assigned again, we want to override it
            mPropertyHoldersMap.put(dummyBackgroundColorProp, PropertyValuesHolder.ofObject("backgroundColor", new ArgbEvaluator(), (Object[]) colorValues));
            return this;
        }

        public Builder alpha(float alpha) {
            animateProperty(View.ALPHA, alpha);
            return this;
        }

        public Builder animateBackgroundColor(Integer... colorValues) {
            if (colorValues.length == 0) {
                return null;
            }

            return backgroundColor(colorValues);
        }

        public Builder animateTextColor(Integer... colorValues) {
            if (colorValues.length == 0) {
                return null;
            }

            return textColor(colorValues);

        }

        public Builder fadeOut() {
            animatePropertyBetween(View.ALPHA,1,0);
            return this;
        }

        public Builder fadeIn() {
            animatePropertyBetween(View.ALPHA,0,1);
            return this;

        }

        public Builder alphaBy(float alphaBy) {
            animatePropertyBy(View.ALPHA, alphaBy);
            return this;
        }

        public Builder scaleX(float scaleX) {
            animateProperty(View.SCALE_X, scaleX);
            return this;
        }

        public Builder scaleXBy(float scaleXBy) {
            animatePropertyBy(View.SCALE_X, scaleXBy);
            return this;
        }

        public Builder scaleY(float scaleY) {
            animateProperty(View.SCALE_Y, scaleY);
            return this;
        }

        public Builder scaleYBy(float scaleYBy) {
            animatePropertyBy(View.SCALE_Y, scaleYBy);
            return this;
        }

        public Builder scales(float scales) {
            scaleY(scales);
            scaleX(scales);
            return this;
        }

        public Builder scalesBy(float scalesBy) {
            scaleYBy(scalesBy);
            scaleXBy(scalesBy);
            return this;
        }

        public Builder translate(float x, float y) {
            return translateX(x).translateY(y);
        }

        public Builder translateX(float translationX) {
            animateProperty(View.TRANSLATION_X, translationX);
            return this;
        }

        public Builder translateXBy(float translationXBy) {
            animatePropertyBy(View.TRANSLATION_X, translationXBy);
            return this;
        }

        public Builder translateY(float translationY) {
            animateProperty(View.TRANSLATION_Y, translationY);
            return this;
        }

        public Builder translateYBy(float translationYBy) {
            animatePropertyBy(View.TRANSLATION_Y, translationYBy);
            return this;
        }

        @SuppressLint("NewApi")
        public Builder translateZ(float translationZ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animateProperty(View.TRANSLATION_Z, translationZ);
            }
            return this;
        }

        @SuppressLint("NewApi")
        public Builder translateZBy(float translationZBy) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animatePropertyBy(View.TRANSLATION_Z, translationZBy);
            }
            return this;
        }

        public Builder rotate(float rotation) {
            animatePropertyBetween(View.ROTATION, 0, rotation);
            return this;
        }

        public Builder rotateBy(float rotationBy) {
            animatePropertyBy(View.ROTATION, rotationBy);
            return this;
        }

        public Builder rotateX(float rotationX) {
            animateProperty(View.ROTATION_X, rotationX);
            return this;
        }

        public Builder rotateXBy(float rotationXBy) {
            animatePropertyBy(View.ROTATION_X, rotationXBy);
            return this;
        }

        public Builder rotateY(float rotationY) {
            animateProperty(View.ROTATION_Y, rotationY);
            return this;
        }

        public Builder rotateYBy(float rotationYBy) {
            animatePropertyBy(View.ROTATION_Y, rotationYBy);
            return this;
        }

        public Builder x(float x) {
            animateProperty(View.X, x);
            return this;
        }

        public Builder xBy(float xBy) {
            animatePropertyBy(View.X, xBy);
            return this;
        }

        public Builder y(float y) {
            animateProperty(View.Y, y);
            return this;
        }

        public Builder yBy(float yBy) {
            animatePropertyBy(View.Y, yBy);
            return this;
        }

        @SuppressLint("NewApi")
        public Builder z(float z) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animateProperty(View.Z, z);
            }
            return this;
        }

        @SuppressLint("NewApi")
        public Builder zBy(float zBy) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animatePropertyBy(View.Z, zBy);
            }
            return this;
        }

        public Builder leftMargin(int leftMargin) {
            if (initMarginListener()) {
                mMarginListener.leftMargin(leftMargin);
            }
            return this;
        }

        public Builder leftMarginBy(int leftMarginBy) {
            if (initMarginListener()) {
                mMarginListener.leftMarginBy(leftMarginBy);
            }
            return this;
        }

        public Builder topMargin(int topMargin) {
            if (initMarginListener()) {
                mMarginListener.topMargin(topMargin);
            }
            return this;
        }

        public Builder topMarginBy(int topMarginBy) {
            if (initMarginListener()) {
                mMarginListener.topMarginBy(topMarginBy);
            }
            return this;
        }

        public Builder rightMargin(int rightMargin) {
            if (initMarginListener()) {
                mMarginListener.rightMargin(rightMargin);
            }
            return this;
        }

        public Builder rightMarginBy(int rightMarginBy) {
            if (initMarginListener()) {
                mMarginListener.rightMarginBy(rightMarginBy);
            }
            return this;
        }

        public Builder bottomMargin(int bottomMargin) {
            if (initMarginListener()) {
                mMarginListener.bottomMargin(bottomMargin);
            }
            return this;
        }

        public Builder bottomMarginBy(int bottomMarginBy) {
            if (initMarginListener()) {
                mMarginListener.bottomMarginBy(bottomMarginBy);
            }
            return this;
        }

        public Builder horizontalMargin(int horizontalMargin) {
            if (initMarginListener()) {
                mMarginListener.horizontalMargin(horizontalMargin);
            }
            return this;
        }

        public Builder horizontalMarginBy(int horizontalMarginBy) {
            if (initMarginListener()) {
                mMarginListener.horizontalMarginBy(horizontalMarginBy);
            }
            return this;
        }

        public Builder verticalMargin(int verticalMargin) {
            if (initMarginListener()) {
                mMarginListener.verticalMargin(verticalMargin);
            }
            return this;
        }

        public Builder verticalMarginBy(int verticalMarginBy) {
            if (initMarginListener()) {
                mMarginListener.verticalMarginBy(verticalMarginBy);
            }
            return this;
        }

        public Builder margin(int margin) {
            if (initMarginListener()) {
                mMarginListener.margin(margin);
            }
            return this;
        }

        public Builder marginBy(int marginBy) {
            if (initMarginListener()) {
                mMarginListener.marginBy(marginBy);
            }
            return this;
        }

        public Builder width(int width) {
            if (initDimensionListener()) {
                mDimensionListener.width(width);
            }
            return this;
        }

        public Builder widthBy(int widthBy) {
            if (initDimensionListener()) {
                mDimensionListener.widthBy(widthBy);
            }
            return this;
        }

        public Builder height(int height) {
            if (initDimensionListener()) {
                mDimensionListener.height(height);
            }
            return this;
        }

        public Builder heightBy(int heightBy) {
            if (initDimensionListener()) {
                mDimensionListener.heightBy(heightBy);
            }
            return this;
        }

        public Builder size(int size) {
            if (initDimensionListener()) {
                mDimensionListener.size(size);
            }
            return this;
        }

        public Builder sizeBy(int sizeBy) {
            if (initDimensionListener()) {
                mDimensionListener.sizeBy(sizeBy);
            }
            return this;
        }

        public Builder leftPadding(int leftPadding) {
            if (initPaddingListener()) {
                mPaddingListener.leftPadding(leftPadding);
            }
            return this;
        }

        public Builder leftPaddingBy(int leftPaddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.leftPaddingBy(leftPaddingBy);
            }
            return this;
        }

        public Builder topPadding(int topPadding) {
            if (initPaddingListener()) {
                mPaddingListener.topPadding(topPadding);
            }
            return this;
        }

        public Builder topPaddingBy(int topPaddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.topPaddingBy(topPaddingBy);
            }
            return this;
        }

        public Builder rightPadding(int rightPadding) {
            if (initPaddingListener()) {
                mPaddingListener.rightPadding(rightPadding);
            }
            return this;
        }

        public Builder rightPaddingBy(int rightPaddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.rightPaddingBy(rightPaddingBy);
            }
            return this;
        }

        public Builder bottomPadding(int bottomPadding) {
            if (initPaddingListener()) {
                mPaddingListener.bottomPadding(bottomPadding);
            }
            return this;
        }

        public Builder bottomPaddingBy(int bottomPaddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.bottomPaddingBy(bottomPaddingBy);
            }
            return this;
        }

        public Builder horizontalPadding(int horizontalPadding) {
            if (initPaddingListener()) {
                mPaddingListener.horizontalPadding(horizontalPadding);
            }
            return this;
        }

        public Builder horizontalPaddingBy(int horizontalPaddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.horizontalPaddingBy(horizontalPaddingBy);
            }
            return this;
        }

        public Builder verticalPadding(int verticalPadding) {
            if (initPaddingListener()) {
                mPaddingListener.verticalPadding(verticalPadding);
            }
            return this;
        }

        public Builder verticalPaddingBy(int verticalPaddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.verticalPaddingBy(verticalPaddingBy);
            }
            return this;
        }

        public Builder padding(int padding) {
            if (initPaddingListener()) {
                mPaddingListener.padding(padding);
            }
            return this;
        }

        public Builder paddingBy(int paddingBy) {
            if (initPaddingListener()) {
                mPaddingListener.paddingBy(paddingBy);
            }
            return this;
        }

        public Builder scrollX(int scrollX) {
            if (initScrollListener()) {
                mScrollListener.scrollX(scrollX);
            }
            return this;
        }

        public Builder scrollXBy(int scrollXBy) {
            if (initScrollListener()) {
                mScrollListener.scrollXBy(scrollXBy);
            }
            return this;
        }

        public Builder scrollY(int scrollY) {
            if (initScrollListener()) {
                mScrollListener.scrollY(scrollY);
            }
            return this;
        }

        public Builder scrollYBy(int scrollYBy) {
            if (initScrollListener()) {
                mScrollListener.scrollYBy(scrollYBy);
            }
            return this;
        }

        public Builder widthPercent(float widthPercent) {
            if (initPercentListener()) {
                mPercentListener.widthPercent(widthPercent);
            }
            return this;
        }

        public Builder widthPercentBy(float widthPercentBy) {
            if (initPercentListener()) {
                mPercentListener.widthPercentBy(widthPercentBy);
            }
            return this;
        }

        public Builder heightPercent(float heightPercent) {
            if (initPercentListener()) {
                mPercentListener.heightPercent(heightPercent);
            }
            return this;
        }

        public Builder heightPercentBy(float heightPercentBy) {
            if (initPercentListener()) {
                mPercentListener.heightPercentBy(heightPercentBy);
            }
            return this;
        }

        public Builder sizePercent(float sizePercent) {
            if (initPercentListener()) {
                mPercentListener.sizePercent(sizePercent);
            }
            return this;
        }

        public Builder sizePercentBy(float sizePercentBy) {
            if (initPercentListener()) {
                mPercentListener.sizePercentBy(sizePercentBy);
            }
            return this;
        }

        public Builder leftMarginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.leftMarginPercent(marginPercent);
            }
            return this;
        }

        public Builder leftMarginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.leftMarginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder topMarginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.topMarginPercent(marginPercent);
            }
            return this;
        }

        public Builder topMarginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.topMarginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder bottomMarginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.bottomMarginPercent(marginPercent);
            }
            return this;
        }

        public Builder bottomMarginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.bottomMarginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder rightMarginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.rightMarginPercent(marginPercent);
            }
            return this;
        }

        public Builder rightMarginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.rightMarginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder horizontalMarginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.horizontalMarginPercent(marginPercent);
            }
            return this;
        }

        public Builder horizontalMarginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.horizontalMarginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder verticalMarginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.verticalMarginPercent(marginPercent);
            }
            return this;
        }

        public Builder verticalMarginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.verticalMarginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder marginPercent(float marginPercent) {
            if (initPercentListener()) {
                mPercentListener.marginPercent(marginPercent);
            }
            return this;
        }

        public Builder marginPercentBy(float marginPercentBy) {
            if (initPercentListener()) {
                mPercentListener.marginPercentBy(marginPercentBy);
            }
            return this;
        }

        public Builder aspectRatio(float aspectRatio) {
            if (initPercentListener()) {
                mPercentListener.aspectRatio(aspectRatio);
            }
            return this;
        }

        public Builder aspectRatioBy(float aspectRatioBy) {
            if (initPercentListener()) {
                mPercentListener.aspectRatioBy(aspectRatioBy);
            }
            return this;
        }

        private boolean initMarginListener() {
            //we're initializing margin listener only when needed (it can cause an exception when there are no params)
            if (mMarginListener == null) {
                if (!hasView()) {
                    return false;
                }
                mMarginListener = new MarginChangeListener(mView.get());
            }
            return true;
        }

        private boolean initDimensionListener() {
            //we're initializing dimension listener only when needed (it can cause an exception when there are no params)
            if (mDimensionListener == null) {
                if (!hasView()) {
                    return false;
                }
                mDimensionListener = new DimensionChangeListener(mView.get());
            }
            return true;
        }

        private boolean initPaddingListener() {
            if (mPaddingListener == null) {
                if (!hasView()) {
                    return false;
                }
                mPaddingListener = new PaddingChangeListener(mView.get());
            }
            return true;
        }

        private boolean initScrollListener() {
            if (mScrollListener == null) {
                if (!hasView()) {
                    return false;
                }
                mScrollListener = new ScrollChangeListener(mView.get());
            }
            return true;
        }

        private boolean initPercentListener() {
            if (mPercentListener == null) {
                if (!hasView()) {
                    return false;
                }
                mPercentListener = new PercentChangeListener(mView.get());
            }
            return true;
        }

        public Builder withLayer() {
            mWithLayer = true;
            return this;
        }

        public Builder setStartDelay(long startDelay) {
            if (startDelay < 0) {
                throw new IllegalArgumentException("startDelay cannot be < 0");
            }
            mStartDelay = startDelay;
            return this;
        }

        public Builder setDuration(long duration) {
            if (duration < 0) {
                throw new IllegalArgumentException("duration cannot be < 0");
            }
            mDuration = duration;
            return this;
        }

        public Builder setInterpolator(Interpolator interpolator) {
            mInterpolator = interpolator;
            return this;
        }

        public Builder addListener(Animator.AnimatorListener listener) {
            mListeners.add(listener);
            return this;
        }

        public Builder removeListener(Animator.AnimatorListener listener) {
            mListeners.remove(listener);
            return this;
        }

        public Builder removeAllListeners() {
            mListeners.clear();
            return this;
        }

        public Builder addUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
            mUpdateListeners.add(listener);
            return this;
        }

        public Builder removeUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
            mUpdateListeners.remove(listener);
            return this;
        }

        public Builder removeAllUpdateListeners() {
            mUpdateListeners.clear();
            return this;
        }

        public Builder addPauseListener(ValueAnimator.AnimatorPauseListener listener) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mPauseListeners.add(listener);
            }
            return this;
        }

        public Builder removePauseListener(ValueAnimator.AnimatorPauseListener listener) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mPauseListeners.remove(listener);
            }
            return this;
        }

        public Builder removeAllPauseListeners() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mPauseListeners.clear();
            }
            return this;
        }

        public Builder withStartAction(final Runnable runnable) {
            return addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    runnable.run();
                    removeListener(this);
                }
            });
        }

        public Builder withEndAction(final Runnable runnable) {
            return addListener(new AnimatorListenerAdapter() {
                private boolean mIsCanceled;

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsCanceled = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mIsCanceled) {
                        runnable.run();
                    }
                    removeListener(this);
                }
            });
        }

        private boolean hasView() {
            return mView.get() != null;
        }

        @SuppressLint("NewApi")
        public AnimationEngine build() {
            if (hasView()) {
                Collection<PropertyValuesHolder> holders = mPropertyHoldersMap.values();
                ObjectAnimator animator =
                        ObjectAnimator.ofPropertyValuesHolder(mView.get(),
                                holders.toArray(new PropertyValuesHolder[holders.size()]));
                if (mWithLayer) {
                    animator.addListener(new AnimatorListenerAdapter() {
                        int mCurrentLayerType = View.LAYER_TYPE_NONE;

                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (hasView()) {
                                View view = mView.get();
                                mCurrentLayerType = view.getLayerType();
                                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                                if (ViewCompat.isAttachedToWindow(view)) {
                                    view.buildLayer();
                                }
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (hasView()) {
                                mView.get().setLayerType(mCurrentLayerType, null);
                            }
                        }
                    });
                }
                if (mStartDelay != -1) {
                    animator.setStartDelay(mStartDelay);
                }
                if (mDuration != -1) {
                    animator.setDuration(mDuration);
                }
                if (mInterpolator != null) {
                    animator.setInterpolator(mInterpolator);
                }
                for (Animator.AnimatorListener listener : mListeners) {
                    animator.addListener(listener);
                }
                if (mMarginListener != null) {
                    animator.addUpdateListener(mMarginListener);
                }
                if (mDimensionListener != null) {
                    animator.addUpdateListener(mDimensionListener);
                }
                if (mPaddingListener != null) {
                    animator.addUpdateListener(mPaddingListener);
                }
                if (mScrollListener != null) {
                    animator.addUpdateListener(mScrollListener);
                }
                if (mPercentListener != null) {
                    animator.addUpdateListener(mPercentListener);
                }
                for (ValueAnimator.AnimatorUpdateListener listener : mUpdateListeners) {
                    animator.addUpdateListener(listener);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (Animator.AnimatorPauseListener listener : mPauseListeners) {
                        animator.addPauseListener(listener);
                    }
                }
                return new AnimationEngine(animator);
            }
            return new AnimationEngine(ObjectAnimator.ofFloat(null, View.ALPHA, 1, 1));
        }
    }
}
