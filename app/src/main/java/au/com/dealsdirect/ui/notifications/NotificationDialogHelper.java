package au.com.dealsdirect.ui.notifications;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mysale.genie.views.custom.recyclerview.CustomLinearLayoutManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsResponse;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsType;
import au.com.dealsdirect.listeners.OnHorizontalSwipeTouchListener;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalPageIndicatorAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationDialogHelper {

    private Dialog dialog = null;

    private TextView header = null;
    private RecyclerView recyclerView = null;
    private RecyclerView pageIndicatorRecyclerView = null;
    private Button buttonSelect = null;
    private ImageButton buttonLeft = null;
    private ImageButton buttonRight = null;

    private boolean isAutoScroll = false;
    private boolean isSwipeEnabled = false;

    private int scrollSpeed = 0;
    private int scrollStepSize = 1;

    Disposable autoScrollDisposable = null;

    private View.OnLayoutChangeListener onLayoutChangeListener = null;

    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final int SCROLL_INTERVAL = 3;

    private HorizontalScrollingNotificationAdapter adapter = null;

    private Map<GetNotificationsType, ButtonSelector> buttonSelectors = null;

    public NotificationDialogHelper(Context context, boolean isAutoScroll, boolean isSwipeEnabled) {
        dialog = createDialog(context);
        setup();

        this.isAutoScroll = isAutoScroll;
        this.isSwipeEnabled = isSwipeEnabled;
    }

    private Dialog createDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog);

        return dialog;
    }

    private void setup() {
        header = dialog.findViewById(R.id.notification_dialog_header);
        recyclerView = dialog.findViewById(R.id.notification_dialog_recycler_view);
        pageIndicatorRecyclerView = dialog.findViewById(R.id.notification_dialog_page_indicator);
        buttonSelect = dialog.findViewById(R.id.notification_dialog_button);
        buttonLeft = dialog.findViewById(R.id.notification_left_button);
        buttonRight = dialog.findViewById(R.id.notification_right_button);

        buttonLeft.setOnClickListener(v -> scrollToPrevious(true));
        buttonRight.setOnClickListener(v -> scrollToNext(true));

        dialog.setOnShowListener(dialog12 -> onViewBound());
        dialog.setOnDismissListener(dialog1 -> onViewRemoved());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        final CustomLinearLayoutManager pageIndicatorLayoutManager = new CustomLinearLayoutManager(
                pageIndicatorRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        pageIndicatorLayoutManager.setScrollEnabled(false);
        pageIndicatorRecyclerView.setLayoutManager(pageIndicatorLayoutManager);

        final HorizontalPageIndicatorAdapter horizontalPageIndicatorAdapter = new HorizontalPageIndicatorAdapter(HorizontalPageIndicatorAdapter.Style.CIRCLE);
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

    private void setupHeader(GetNotificationsResponse notification) {
        if (notification == null) {
            return;
        }
        final String notificationType = notification.getNotificationType();
        if (notificationType.equalsIgnoreCase("YouHaveUnExpiredVoucher") ||
                notificationType.equalsIgnoreCase("YouHavePromoCode")) {
            String text;
            if (adapter.getDataSource().size() > 1) {
                text = header.getContext().getResources().getString(R.string.notifications_use_these_codes);
            } else {
                text = header.getContext().getResources().getString(R.string.notifications_use_this_code);
            }
            header.setText(text);
        }
    }

    private void setupButton(GetNotificationsResponse notification) {
        if (buttonSelectors == null || notification == null) {
            buttonSelect.setOnClickListener(null);
            return;
        }
        try {
            GetNotificationsType notificationType = GetNotificationsType.valueOf(notification.getNotificationType());
            final ButtonSelector selector = buttonSelectors.get(notificationType);
            buttonSelect.setOnClickListener(v -> {
                if (selector != null) {
                    selector.onClick(NotificationDialogHelper.this, notification);
                }
            });
            buttonSelect.setText(selector.getButtonText());
        } catch (IllegalArgumentException e) {
            buttonSelect.setOnClickListener(null);
        }
    }

    public void show() {
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public HorizontalScrollingNotificationAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(HorizontalScrollingNotificationAdapter adapter) {
        this.adapter = adapter;
        if (recyclerView != null) {
            if (adapter != null) {
                adapter.setWillScrollWrapAround(adapter.getDataSource().size() > 1);
            }
            recyclerView.setAdapter(adapter);
            if (adapter != null) {
                adapter.resetReyclerViewPosition();
                setupHeader(adapter.getSelectedNotification());
                setupButton(adapter.getSelectedNotification());
                if (adapter.getDataSource().size() > 1) {
                    buttonLeft.setVisibility(View.VISIBLE);
                    buttonRight.setVisibility(View.VISIBLE);
                    setPageIndicatorItemCount(adapter.getDataSource().size());
                } else {
                    buttonLeft.setVisibility(View.GONE);
                    buttonRight.setVisibility(View.GONE);
                    setPageIndicatorItemCount(0);
                }
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
        pageIndicatorRecyclerView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
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
            NotificationDialogHelper.this.onScrollChanged(recyclerView.computeHorizontalScrollOffset());


    public void onViewRemoved() {
        setAdapter(null);
        setPageIndicatorItemCount(0);
        setPageIndicatorVisibility(View.GONE);
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
                    if (!dialog.isShowing() ||
                            dialog.getOwnerActivity() == null ||
                            dialog.getOwnerActivity().isDestroyed()) {
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
        if (getStepWidth() == 0) {
            return;
        }
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
        return adapter.getCellWidth(dialog.getContext()) * scrollStepSize;
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

        setupHeader(adapter.getSelectedNotification());
        setupButton(adapter.getSelectedNotification());
    }

    private void wrapAround(int speed) {
        if (adapter != null) {
            adapter.wrapScrollPosition(speed);
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

    public void setButtonSelectors(Map<GetNotificationsType, ButtonSelector> buttonSelectors) {
        this.buttonSelectors = buttonSelectors;
        if (adapter != null) {
            setupButton(adapter.getSelectedNotification());
        }
    }

    public interface ButtonSelector {
        void onClick(NotificationDialogHelper dialogHelper, GetNotificationsResponse notification);
        @NonNull
        String getButtonText();
    }
}
