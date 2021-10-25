package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.listeners.OnHorizontalSwipeTouchListener;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductPriceBlockHelper;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SaleItemDetailsHorizontalScrollingItemsHelper {

    private static final int SCROLL_INTERVAL = 3;

    private final HorizontalScrollingItemsAdapter adapter;
    private final RecyclerView recyclerView;
    private final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final ImageUtils.Grid grid;
    private final boolean isSwipeEnabled;
    private final boolean isAutoScroll;
    private final boolean isSupplierOriginalPriceInfoEnabled;

    private Disposable autoScrollDisposable = null;
    private int previousX;


    public SaleItemDetailsHorizontalScrollingItemsHelper(RecyclerView recyclerView,
                                                         boolean isTablet,
                                                         boolean isAutoScroll,
                                                         boolean isSwipeEnabled,
                                                         boolean isSupplierOriginalPriceInfoEnabled) {
        this.recyclerView = recyclerView;
        this.isAutoScroll = isAutoScroll;
        this.isSwipeEnabled = isSwipeEnabled;
        this.isSupplierOriginalPriceInfoEnabled = isSupplierOriginalPriceInfoEnabled;

        onScrollChangedListener = () ->
                this.onScrollChanged(recyclerView.computeHorizontalScrollOffset());

        assert recyclerView.getAdapter() instanceof HorizontalScrollingItemsAdapter;
        adapter = (HorizontalScrollingItemsAdapter) recyclerView.getAdapter();

        grid = getGrid(recyclerView.getContext(), isTablet);
        setupDimensions();

        adapter.resetReyclerViewPosition();
        snapToCenter(false);

        previousX = recyclerView.computeHorizontalScrollOffset();
        setupScrollListener();

        if (isAutoScroll) {
            startAutoscroll();
        }
    }

    private ImageUtils.Grid getGrid(Context context, boolean isTablet) {
        final float numberOfColumns = isTablet ? 3f : 2.1f;
        final float screenDensity = ScreenUtils.getScreenDensity(context);
        final int proposedWidth = (int) (context.getResources().getInteger(R.integer.item_image_width) * screenDensity);
        final int proposedHeight = (int) ((context.getResources().getInteger(R.integer.item_image_height) * screenDensity) +
                context.getResources().getDimension(R.dimen.price_block_top_text_height) +
                context.getResources().getDimension(R.dimen.price_block_height) +
                SaleItemProductPriceBlockHelper.getBottomTextViewHeight(context, true, isSupplierOriginalPriceInfoEnabled) +
                context.getResources().getDimension(R.dimen.price_block_free_shipping_text_height));
        final float ratio = (float) proposedHeight / Math.max(1, proposedWidth);
        return ImageUtils.getExactGridDefinition(
                numberOfColumns,
                ratio,
                ScreenUtils.getScreenWidth(context));
    }

    private void setupDimensions() {
        if (adapter == null) {
            return;
        }
        adapter.setupDimensions(
                (int) grid.getItemWidth(),
                (int) grid.getItemHeight());
    }

    public void onRecyclerViewDetach() {
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
                    if (CommonUtils.isActivityOfViewDestroyed(recyclerView)) {
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
                displacement = -Math.round(grid.getItemWidth());
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
        if (adapter == null) {
            return;
        }

        float diff = (getXBeforeNextPosition() - getXAfterPreviousPosition()) % grid.getItemWidth();
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
        return recyclerView.computeHorizontalScrollOffset() % Math.round(grid.getItemWidth());
    }

    private int getXBeforeNextPosition() {
        if (adapter == null) {
            return 0;
        }
        return Math.round(grid.getItemWidth() - getXAfterPreviousPosition());
    }

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
