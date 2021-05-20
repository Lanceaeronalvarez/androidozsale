package au.com.dealsdirect.utils.ScrollingImageHorizontal;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mysale.genie.views.custom.recyclerview.CustomLinearLayoutManager;

import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.listeners.OnHorizontalSwipeTouchListener;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalCircleIndicatorAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter;
import au.com.dealsdirect.utils.CommonUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by MTC on 2020-06-05.
 */
public class HorizontalRecyclerViewHolder extends RecyclerView.ViewHolder {

    private boolean isAutoScroll;

    private HorizontalScrollingBannerAdapter adapter;

    private Disposable autoScrollDisposable = null;

    private boolean isSwipeEnabled;

    private View.OnLayoutChangeListener onLayoutChangeListener = null;

    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final int SCROLL_INTERVAL = 3;

    @BindView(R.id.viewholder_horizontal_scrolling_banner_layout)
    ViewGroup layout;

    @BindView(R.id.viewholder_horizontal_scrolling_banner_recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.viewholder_horizontal_scrolling_banner_indicator)
    public RecyclerView circleIndicatorRecyclerView;

    public HorizontalRecyclerViewHolder(View view,
                                        int height,
                                        HorizontalScrollingBannerAdapter adapter,
                                        boolean isAutoScroll,
                                        boolean isSwipeEnabled) {
        super(view);
        ButterKnife.bind(this, view);

        if (height > 0) {
            float spacing = view.getContext().getResources().getDimension(R.dimen.horizontal_banner_spacing);

            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = height + (int) (spacing * 2);
            layout.setLayoutParams(params);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        setAdapter(adapter);

        this.isAutoScroll = isAutoScroll;
        this.isSwipeEnabled = isSwipeEnabled;

        CustomLinearLayoutManager circleLayoutManager = new CustomLinearLayoutManager(
                circleIndicatorRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        circleLayoutManager.setScrollEnabled(false);
        circleIndicatorRecyclerView.setLayoutManager(circleLayoutManager);
        HorizontalCircleIndicatorAdapter horizontalCircleIndicatorAdapter = new HorizontalCircleIndicatorAdapter();
        circleIndicatorRecyclerView.setAdapter(horizontalCircleIndicatorAdapter);

        onLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Handler mainHandler = new Handler(recyclerView.getContext().getMainLooper());
            Runnable myRunnable = () -> {
                adapter.resetReyclerViewPosition();
                horizontalCircleIndicatorAdapter.setSelectedPosition(0);

                if (onLayoutChangeListener != null) {
                    circleIndicatorRecyclerView.removeOnLayoutChangeListener(onLayoutChangeListener);
                    onLayoutChangeListener = null;
                }
            };
            mainHandler.post(myRunnable);
        };

        circleIndicatorRecyclerView.addOnLayoutChangeListener(onLayoutChangeListener);
    }

    public HorizontalScrollingBannerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(HorizontalScrollingBannerAdapter adapter) {
        this.adapter = adapter;
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            if (adapter != null) {
                adapter.resetReyclerViewPosition();
            }
        }
    }

    public void onViewBound() {
        previousX = recyclerView.computeHorizontalScrollOffset();
        setupScrollListener();

        if (isAutoScroll) {
            startAutoscroll();
        }
    }

    public void setCircleIndicatorItemCount(int count) {
        if (getCircleIndicatorAdapter() != null) {
            getCircleIndicatorAdapter().setItemCount(count);
            getCircleIndicatorAdapter().setSelectedPosition(adapter.getRecyclerViewPosition());
        }
    }

    public HorizontalCircleIndicatorAdapter getCircleIndicatorAdapter() {
        if (circleIndicatorRecyclerView != null) {
            return (HorizontalCircleIndicatorAdapter) circleIndicatorRecyclerView.getAdapter();
        } else {
            return null;
        }
    }

    private final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = () ->
            HorizontalRecyclerViewHolder.this.onScrollChanged(recyclerView.computeHorizontalScrollOffset());


    public void onViewRecycled() {
        clearScrollListener();
        stopAutoScroll();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupScrollListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> onScrollChanged(recyclerView.computeHorizontalScrollOffset()));
        } else {
            recyclerView.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
        }

        if (isSwipeEnabled) {
            recyclerView.setOnTouchListener(new OnHorizontalSwipeTouchListener() {
                @Override
                public void onFinishDragging() {
                    snapToCenter(true);
                }

                @Override
                public void onSwipeLeft() {
                    scrollToNext(true);
                }

                @Override
                public void onSwipeRight() {
                    scrollToPrevious(true);
                }

                @Override
                public void onTouchDown() {
                    if (isAutoScroll) {
                        stopAutoScroll();
                    }
                }

                @Override
                public void onTouchUp() {
                    if (isAutoScroll) {
                        startAutoscroll();
                    }
                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clearScrollListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(null);
        } else {
            recyclerView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
        }

        recyclerView.setOnTouchListener(null);
    }

    private void startAutoscroll() {

        stopAutoScroll();
        autoScrollDisposable = Observable.interval(SCROLL_INTERVAL, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .takeWhile(o -> compositeDisposable.size() != 0 && !compositeDisposable.isDisposed())
                .doOnNext(o -> {
                    scrollToNext(true);
                    if (CommonUtils.isActivityOfViewDestroyed(itemView)) {
                        stopAutoScroll();
                    }
                })
                .subscribe();

        compositeDisposable.add(autoScrollDisposable);
    }

    private void stopAutoScroll() {
        if (autoScrollDisposable != null) {
            autoScrollDisposable.dispose();
            autoScrollDisposable = null;
        }
    }

    private void scrollToNext(boolean withAnimation) {
        if (adapter == null) {
            return;
        }

        Handler mainHandler = new Handler(recyclerView.getContext().getMainLooper());

        Runnable myRunnable = () -> {
            recyclerView.stopScroll();
            wrapAround(1);
            int displacement = getXBeforeNextPosition();
            if (withAnimation) {
                recyclerView.smoothScrollBy(displacement, 0);
            } else {
                recyclerView.scrollBy(displacement, 0);
            }

            if (getCircleIndicatorAdapter() != null) {
                getCircleIndicatorAdapter().setSelectedPosition(adapter.getRecyclerViewPosition(displacement));
            }
        };
        mainHandler.post(myRunnable);
    }

    private void scrollToPrevious(boolean withAnimation) {
        if (adapter == null) {
            return;
        }

        Handler mainHandler = new Handler(recyclerView.getContext().getMainLooper());

        Runnable myRunnable = () -> {
            recyclerView.stopScroll();
            wrapAround(-1);
            int displacement = -getXAfterPreviousPosition();
            if (displacement >= 0) {
                displacement = -adapter.getCellWidth();
            }
            if (withAnimation) {
                recyclerView.smoothScrollBy(displacement, 0);
            } else {
                recyclerView.scrollBy(displacement, 0);
            }

            if (getCircleIndicatorAdapter() != null) {
                getCircleIndicatorAdapter().setSelectedPosition(adapter.getRecyclerViewPosition(displacement));
            }
        };
        mainHandler.post(myRunnable);
    }

    public void snapToCenter(boolean withAnimation) {
        int diff = (getXBeforeNextPosition() - getXAfterPreviousPosition()) % adapter.getCellWidth();
        if (diff < 0) {
            scrollToNext(withAnimation);
        } else if (diff > 0) {
            scrollToPrevious(withAnimation);
        }
    }

    private int getXAfterPreviousPosition() {
        if (adapter == null) {
            return 0;
        }
        return (recyclerView.computeHorizontalScrollOffset() - (recyclerView.getWidth() - adapter.getCellWidth())) % adapter.getCellWidth();
    }

    private int getXBeforeNextPosition() {
        if (adapter == null) {
            return 0;
        }
        return adapter.getCellWidth() - getXAfterPreviousPosition();
    }

    private int previousX = 0;

    private void onScrollChanged(int scrollX) {
        int speed = scrollX - previousX;
        previousX = scrollX;
        wrapAround(speed);
    }

    private void wrapAround(int speed) {
        if (adapter != null) {
            adapter.wrapScrollPosition(speed);
        }

    }
}