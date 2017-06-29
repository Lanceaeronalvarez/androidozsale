package com.mysale.genie.views.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.mysale.genie.animation.AnimationEngine;
import com.mysale.genie.utility.GenericEvent;
import com.mysale.genie.utility.RxBus;
import com.mysale.genie.views.R;


/**
 * Created by smartwave on 04/11/2016.
 */
public class WishlistLayout extends RelativeLayout{

    static class CustomSimpleStringListener extends SimpleSpringListener{

        private ImageView imageToSpring;
        public CustomSimpleStringListener(ImageView imageView){
            imageToSpring=imageView;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = 1f + (value * 0.4f);
            imageToSpring.setPivotY(200);
            imageToSpring.setScaleX(scale);
            imageToSpring.setScaleY(scale);

        }

    }

    static class InitialSimpleStringListener extends SimpleSpringListener{
        private ImageView imageToSpring;
        public InitialSimpleStringListener(ImageView imageView){
            imageToSpring=imageView;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = value;
            imageToSpring.setPivotY(200);
            imageToSpring.setScaleX(scale);
            imageToSpring.setScaleY(scale);
        }
    }

    public static WishlistLayout inflatedInstance = null;

    private static double BOUNCINESS = 10;
    private static double SPEED = 50;
    private SpringSystem mSpringSystem;
    private Spring mHeartSpring;
    private Spring mFriendSpring;
    private Spring mChristmasSpring;
    private Spring mInitialHeartSpring;
    private Spring mInitialFriendSpring;
    private Spring mInitialChristmasSpring;
    RelativeLayout inflatedLayout;

    public ImageView heart;
    private ImageView friend;
    private ImageView christmas;
    public ImageView selectedWishlist;

    private boolean heartToggled = false;
    private boolean friendToggled = false;
    private boolean christmasToggled = false;
    public static boolean isShown=false;
    public static Rect bounds = null;

    public WishlistLayout(Context context) {
        super(context);
        init(context);
    }

    public WishlistLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WishlistLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void showWishListSpringAnimation(){

        Handler handler = new Handler();
        handler.postDelayed(()->{
            mInitialChristmasSpring.setEndValue(1);
        },50);
        handler.postDelayed(()->{
            mInitialFriendSpring.setEndValue(1);
        },100);
        handler.postDelayed(()->{
            mInitialHeartSpring.setEndValue(1);
        },150);

    }

    public void hideWishListSpringAnimation(){
        Handler handler = new Handler();
        handler.postDelayed(()->{
            mInitialHeartSpring.setEndValue(0);
        },50);
        handler.postDelayed(()->{
            mInitialFriendSpring.setEndValue(0);
        },100);
        handler.postDelayed(()->{
            mInitialChristmasSpring.setEndValue(0);
        },150);
    }

    public void hideWishList(){
        hideWishListSpringAnimation();

        Handler handler = new Handler();
        handler.postDelayed(()->{
            AnimationEngine.Builder.animate(this)
                    .fadeOut()
                    .setDuration(100)
                    .withStartAction(()->{

                    })
                    .withEndAction(()->{
                        setVisibility(View.GONE);
                        selectedWishlist = null;
                        isShown = false;
                        RxBus.instance().post(GenericEvent.Events.WISHLIST_HIDDEN);
                        RxBus.instance().unSubscribe(GenericEvent.Events.WISHLIST_SELECTED);
                    })
                    .build().start();
        },200);
    }

    public void showWishList(){
        AnimationEngine.Builder.animate(this)
                .fadeIn()
                .setDuration(100)
                .withStartAction(() -> {
                    setVisibility(View.VISIBLE);
                    showWishListSpringAnimation();
                })
                .withEndAction(() -> {
                    int[] boundRect = new int[2];
                    this.getLocationOnScreen(boundRect);
                    bounds = new Rect(boundRect[0],boundRect[1]-getHeight(),boundRect[0]+getWidth(),boundRect[1]+getHeight()*2);
                    Log.d("bounds",bounds.toString());
                    inflatedInstance = this;
                    RxBus.instance().post(GenericEvent.Events.WISHLIST_SHOWN);
                    isShown = true;
                })
                .build().start();
    }

    private void initSpring(){

        //scale spring
        mSpringSystem = SpringSystem.create();
        SpringConfig config = SpringConfig.fromBouncinessAndSpeed(BOUNCINESS,SPEED);

        mHeartSpring = mSpringSystem.createSpring();
        mHeartSpring.addListener(new CustomSimpleStringListener(heart));
        mHeartSpring.setSpringConfig(config);

        mFriendSpring = mSpringSystem.createSpring();
        mFriendSpring.addListener(new CustomSimpleStringListener(friend));
        mFriendSpring.setSpringConfig(config);

        mChristmasSpring = mSpringSystem.createSpring();
        mChristmasSpring.addListener(new CustomSimpleStringListener(christmas));
        mChristmasSpring.setSpringConfig(config);

        //initial show spring animation
        SpringConfig initSpringConfig = SpringConfig.fromBouncinessAndSpeed(5,5);

        mInitialHeartSpring = mSpringSystem.createSpring();
        mInitialHeartSpring.addListener(new InitialSimpleStringListener(heart));
        mInitialHeartSpring.setSpringConfig(initSpringConfig);

        mInitialFriendSpring = mSpringSystem.createSpring();
        mInitialFriendSpring.addListener(new InitialSimpleStringListener(friend));
        mInitialFriendSpring.setSpringConfig(initSpringConfig);

        mInitialChristmasSpring = mSpringSystem.createSpring();
        mInitialChristmasSpring.addListener(new InitialSimpleStringListener(christmas));
        mInitialChristmasSpring.setSpringConfig(initSpringConfig);

    }

    private void init(Context context){
        inflatedLayout = (RelativeLayout) inflate(context,R.layout.wishlist_layout,this);
        setClipChildren(false);
        setClipToPadding(false);
        heart = (ImageView) findViewById(R.id.wishlist1);
        friend = (ImageView) findViewById(R.id.wishlist2);
        christmas = (ImageView) findViewById(R.id.wishlist3);

        initSpring();
    }

    public Drawable getSelectedWishlistDrawable(){
        if(selectedWishlist != null) {

            if (selectedWishlist.getId() == R.id.wishlist1) {
                return ContextCompat.getDrawable(getContext(), R.drawable.heart);
            } else if (selectedWishlist.getId() == R.id.wishlist2) {
                return ContextCompat.getDrawable(getContext(), R.drawable.birthday);
            } else if (selectedWishlist.getId() == R.id.wishlist3) {
                return ContextCompat.getDrawable(getContext(), R.drawable.christmas);
            }
        }

        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);

        int[] heartLoc = new int[2];
        heart.getLocationOnScreen(heartLoc);
        int[] friendLoc = new int[2];
        friend.getLocationOnScreen(friendLoc);
        int[] christmasLoc = new int[2];
        christmas.getLocationOnScreen(christmasLoc);

//        Rect heartRect = new Rect(heartLoc[0], heartLoc[1], heartLoc[0] + heart.getWidth(), heartLoc[1] + heart.getHeight());
//        Rect friendRect = new Rect(friendLoc[0], friendLoc[1], friendLoc[0] + friend.getWidth(), friendLoc[1] + friend.getHeight());
//        Rect christmasRect = new Rect(christmasLoc[0], christmasLoc[1], christmasLoc[0] + christmas.getWidth(), christmasLoc[1] + christmas.getHeight());

//        Rect heartRect = new Rect(heartLoc[0], heartLoc[1]-heart.getHeight(), heartLoc[0] + heart.getWidth(), heartLoc[1] + heart.getHeight()*2);
//        Rect friendRect = new Rect(friendLoc[0], friendLoc[1]-friend.getHeight(), friendLoc[0] + friend.getWidth(), friendLoc[1] + friend.getHeight()*2);
//        Rect christmasRect = new Rect(christmasLoc[0], christmasLoc[1]-christmas.getHeight(), christmasLoc[0] + christmas.getWidth(), christmasLoc[1] + christmas.getHeight()*2);

        if(!isShown){
            return false;
        }

//        Log.d("boundsTop",bounds.top+"");
//        Log.d("boundsTop",bounds.bottom+"");

        Rect heartRect = new Rect(heartLoc[0], heartLoc[1]-bounds.top, heartLoc[0] + heart.getWidth(), heartLoc[1] + bounds.bottom);
        Rect friendRect = new Rect(friendLoc[0], friendLoc[1]-bounds.top, friendLoc[0] + friend.getWidth(), friendLoc[1] + bounds.bottom);
        Rect christmasRect = new Rect(christmasLoc[0], christmasLoc[1]-bounds.top, christmasLoc[0] + christmas.getWidth(), christmasLoc[1] + bounds.bottom);

//        Rect heartRect = new Rect(heartLoc[0], heartLoc[1]-heart.getHeight(), heartLoc[0] + heart.getWidth(), heartLoc[1] + heart.getHeight()*2);
//        Rect friendRect = new Rect(friendLoc[0], friendLoc[1]-friend.getHeight(), friendLoc[0] + friend.getWidth(), friendLoc[1] + friend.getHeight()*2);
//        Rect christmasRect = new Rect(christmasLoc[0], christmasLoc[1]-christmas.getHeight(), christmasLoc[0] + christmas.getWidth(), christmasLoc[1] + christmas.getHeight()*2);

        if(action==MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {

            if (heartRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                if (friendToggled) {
                    mFriendSpring.setEndValue(0);
                    friendToggled = false;
                }

                if (christmasToggled) {
                    mChristmasSpring.setEndValue(0);
                    christmasToggled = false;
                }

                mHeartSpring.setEndValue(1);
                heartToggled = true;

                selectedWishlist = heart;

            }else if (friendRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                if (heartToggled) {
                    mHeartSpring.setEndValue(0);
                    heartToggled = false;
                }

                if (christmasToggled) {
                    mChristmasSpring.setEndValue(0);
                    christmasToggled = false;
                }

                mFriendSpring.setEndValue(1);
                friendToggled = true;

                selectedWishlist = friend;
            }else if (christmasRect.contains((int) event.getRawX(), (int) event.getRawY())) {

                if (heartToggled) {
                    mHeartSpring.setEndValue(0);
                    heartToggled = false;
                }

                if (friendToggled) {
                    mFriendSpring.setEndValue(0);
                    friendToggled = false;
                }


                mChristmasSpring.setEndValue(1);
                christmasToggled = true;

                selectedWishlist = christmas;
            }

        }else if(action == MotionEvent.ACTION_UP){
            mHeartSpring.setEndValue(0);
            mFriendSpring.setEndValue(0);
            mChristmasSpring.setEndValue(0);
            RxBus.instance().post(GenericEvent.Events.WISHLIST_SELECTED);
        }
        return true;
    }



}
