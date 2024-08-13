package au.com.dealsdirect.utils.ScrollingImageHorizontal;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mysale.genie.views.custom.recyclerview.CustomLinearLayoutManager;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import au.com.dealsdirect.R;
import au.com.dealsdirect.listeners.OnHorizontalSwipeTouchListener;
import au.com.dealsdirect.ui.controller.saleitemdetails.HorizontalScrollingItemsAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalPageIndicatorAdapter;
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
public class HorizontalRecyclerItemsViewHolder extends RecyclerView.ViewHolder {

    private int scrollStepSize = 1;
    private int scrollSpeed = 0;
    private boolean isAutoScroll;

    private HorizontalScrollingItemsAdapter adapter;

    private Disposable autoScrollDisposable = null;

    private boolean isSwipeEnabled;

    private View.OnLayoutChangeListener onLayoutChangeListener = null;

    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final int SCROLL_INTERVAL = 3;

    @Nullable
    @BindView(R.id.viewholder_banner_header_text)
    TextView headerTextView;

    @Nullable
    @BindView(R.id.viewholder_banner_header_text_container)
    ViewGroup headerContainer;

    @BindView(R.id.viewholder_horizontal_scrolling_banner_layout)
    ViewGroup layout;

    @BindView(R.id.viewholder_horizontal_scrolling_banner_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.viewholder_horizontal_scrolling_banner_indicator)
    RecyclerView pageIndicatorRecyclerView;

    public HorizontalRecyclerItemsViewHolder(View view,
                                             int height,
                                             HorizontalScrollingItemsAdapter adapter,
                                             boolean isAutoScroll,
                                             boolean isSwipeEnabled,
                                             HorizontalPageIndicatorAdapter.Style pageIndicatorStyle) {
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

        final CustomLinearLayoutManager pageIndicatorLayoutManager = new CustomLinearLayoutManager(
                pageIndicatorRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        pageIndicatorLayoutManager.setScrollEnabled(false);
        pageIndicatorRecyclerView.setLayoutManager(pageIndicatorLayoutManager);

        final HorizontalPageIndicatorAdapter horizontalPageIndicatorAdapter = new HorizontalPageIndicatorAdapter(pageIndicatorStyle);
        pageIndicatorRecyclerView.setAdapter(horizontalPageIndicatorAdapter);

        onLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Handler mainHandler = new Handler(recyclerView.getContext().getMainLooper());
            Runnable myRunnable = () -> {
                adapter.resetReyclerViewPosition();
                horizontalPageIndicatorAdapter.setSelectedPosition(0);

                if (onLayoutChangeListener != null) {
                    pageIndicatorRecyclerView.removeOnLayoutChangeListener(onLayoutChangeListener);
                    onLayoutChangeListener = null;
                }
            };
            mainHandler.post(myRunnable);
        };

        pageIndicatorRecyclerView.addOnLayoutChangeListener(onLayoutChangeListener);
    }

    public HorizontalScrollingItemsAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(HorizontalScrollingItemsAdapter adapter) {
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

    public void setPageIndicatorItemCount(int count) {
        if (getPageIndicatorAdapter() != null) {
            getPageIndicatorAdapter().setItemCount(count);
            if (count > 0) {
                int ratio = adapter.getDataSource().size() / getPageIndicatorAdapter().getItemCount();
                getPageIndicatorAdapter().setSelectedPosition(adapter.getRecyclerViewPosition() / ratio);
            }
        }
    }

    public void setPageIndicatorCountWithPageSize(int pageSize) {
        assert pageSize > 0;
        final float ratio = adapter.getDataSource().size() / (float) pageSize;
        final int indicatorCount = ratio > 0 && ratio < 1 ? 1 : (int) Math.floor(ratio);
        setPageIndicatorItemCount(indicatorCount);
    }

    public HorizontalPageIndicatorAdapter getPageIndicatorAdapter() {
        if (pageIndicatorRecyclerView != null) {
            return (HorizontalPageIndicatorAdapter) pageIndicatorRecyclerView.getAdapter();
        } else {
            return null;
        }
    }

    private final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = () ->
            HorizontalRecyclerItemsViewHolder.this.onScrollChanged(recyclerView.computeHorizontalScrollOffset());


    public void onViewRecycled() {
        clearScrollListener();
        stopAutoScroll();
    }

    public void onViewRemoved() {
        setAdapter(null);
        setPageIndicatorItemCount(0);
        setPageIndicatorVisibility(View.GONE);
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
                private int touchDownX = 0;

                @Override
                public void onFinishDragging() {
                    if (scrollSpeed > scrollStepSize / 4) {
                        scrollToNext(true);
                    } else if (scrollSpeed < scrollStepSize / -4) {
                        scrollToPrevious(true);
                    } else {
                        snapToCenter(true);
                    }
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
                    touchDownX = adapter != null ? adapter.getRecyclerViewPosition() : 0;
                    if (isAutoScroll) {
                        stopAutoScroll();
                    }
                }

                @Override
                public void onTouchUp() {
                    scrollSpeed = (adapter != null ? adapter.getRecyclerViewPosition() : 0) - touchDownX;
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
                displacement = -getStepWidth();
            }
            if (withAnimation) {
                recyclerView.smoothScrollBy(displacement, 0);
            } else {
                recyclerView.scrollBy(displacement, 0);
            }
        };
        mainHandler.post(myRunnable);
    }

    public void snapToCenter(boolean withAnimation) {
        int diff = (getXBeforeNextPosition() - getXAfterPreviousPosition()) % getStepWidth();
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
        return (recyclerView.computeHorizontalScrollOffset() -
                Math.max(0, (int) Math.ceil(recyclerView.getWidth() /
                        (float) getStepWidth()) - 1) * getStepWidth()) % getStepWidth();
    }

    private int getXBeforeNextPosition() {
        if (adapter == null) {
            return 0;
        }
        return getStepWidth() - getXAfterPreviousPosition();
    }

    private int getStepWidth() {
        if (adapter == null) {
            return 0;
        }
        return adapter.getCellWidth() * scrollStepSize;
    }

    private int previousX = 0;

    private void onScrollChanged(int scrollX) {
        int speed = scrollX - previousX;
        previousX = scrollX;
        wrapAround(speed);

        if (getPageIndicatorAdapter() != null && getPageIndicatorAdapter().getItemCount() > 0) {
            int ratio = adapter.getDataSource().size() / getPageIndicatorAdapter().getItemCount();
            getPageIndicatorAdapter().setSelectedPosition(adapter.getRecyclerViewPosition() / ratio);
        }
    }

    private void wrapAround(int speed) {
        if (adapter != null) {
            adapter.wrapScrollPosition(speed);
        }

    }

    public void setHeaderText(String text) {
        if (headerContainer == null) {
            return;
        }
        if (text == null || text.isEmpty()) {
            headerContainer.setVisibility(View.GONE);
        } else {
            headerContainer.setVisibility(View.VISIBLE);
            headerTextView.setText(text);
        }
    }

    public void setPageIndicatorVisibility(int visibility) {
        if (pageIndicatorRecyclerView != null) {
            pageIndicatorRecyclerView.setVisibility(visibility);
        }
    }

    public int getScrollStepSize() {
        return scrollStepSize;
    }

    public void setScrollStepSize(int scrollStepSize) {
        this.scrollStepSize = scrollStepSize;
    }
}