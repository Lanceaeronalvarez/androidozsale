package au.com.dealsdirect.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
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
 * <p>
 * Modified Bartosz Lipinski's ViewPropertyObjectAnimator class to become my Builder class in this AnimationEngine
 * <ref>https://github.com/blipinsk/ViewPropertyObjectAnimator</ref>
 * <p>
 * Modifications:
 * <p>
 * added support for backgroundColor and textColor animations
 * <p>
 * modified ArrayMap<Property<View, Float>, PropertyValuesHolder> mPropertyHoldersMap
 * to ArrayMap<Property<View, ?>, PropertyValuesHolder> mPropertyHoldersMap to allow these color animations
 * <p>
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
        private TimeInterpolator mInterpolator;
        private List<Animator.AnimatorListener> mListeners = new ArrayList<>();
        private List<ValueAnimator.AnimatorUpdateListener> mUpdateListeners = new ArrayList<>();
        private List<Animator.AnimatorPauseListener> mPauseListeners = new ArrayList<>();
        private ArrayMap<Property<View, ?>, PropertyValuesHolder> mPropertyHoldersMap = new ArrayMap<>();

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
            animatePropertyBetween(View.ALPHA, 1, 0);
            return this;
        }

        public Builder fadeIn() {
            animatePropertyBetween(View.ALPHA, 0, 1);
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

        public Builder setInterpolator(TimeInterpolator interpolator) {
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

