package au.com.dealsdirect.data.wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@Singleton
public class AppWishlistHelper implements WishlistHelper {

    private static final int DELAYED_CALLS_DELAY = 500;
    private static final TimeUnit DELAYED_CALLS_DELAY_TIME_UNIT = TimeUnit.MILLISECONDS;

    private WishlistChangeListener wishlistChangeListener;

    private boolean doesCheckoutHaveWishlistItem = false;

    private ArrayList<WishlistObject> wishlist = new ArrayList<>();
    private int oldCount = -1;

    private HashMap<String, WishlistChangeDelayedCallback> delayedCallbacks = new HashMap<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public AppWishlistHelper() {

    }

    @Override
    public void setWishlist(List<WishlistObject> wishlist) {
        if (wishlist == null) {
            this.wishlist.clear();
        } else {
            this.wishlist = new ArrayList<>(wishlist);
        }
        updateWishlistCount();
    }

    @Override
    public List<WishlistObject> getWishlist() {
        return new ArrayList<>(wishlist);
    }

    @Override
    public void addToWishlist(WishlistObject object, WishlistChangeDelayedCallback delayedCallback) {
        wishlist.add(object);
        updateWishlistCount();
        addOrCancelDelayedCallback(object.getProductId(), delayedCallback);
        startDelayedCallback();
    }

    @Override
    public void removeFromWishlist(String productId, WishlistChangeDelayedCallback delayedCallback) {
        int i = 0;
        while (i < wishlist.size()) {
            if (wishlist.get(i).getProductId().equals(productId)) {
                wishlist.remove(i);
            } else {
                i++;
            }
        }
        updateWishlistCount();
        addOrCancelDelayedCallback(productId, delayedCallback);
        startDelayedCallback();
    }

    private void addOrCancelDelayedCallback(String key, WishlistChangeDelayedCallback callback) {
        // this makes it so an add request will cancel out an existing delete request and
        // a delete request will cancel out an add request
        if (delayedCallbacks.containsKey(key)) {
            delayedCallbacks.remove(key);
        } else {
            delayedCallbacks.put(key, callback);
        }
    }

    private void startDelayedCallback() {
        compositeDisposable.clear();
        Disposable disposable = Completable
                .timer(
                        DELAYED_CALLS_DELAY,
                        DELAYED_CALLS_DELAY_TIME_UNIT,
                        AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    for (Map.Entry<String, WishlistChangeDelayedCallback> entry : delayedCallbacks.entrySet()) {
                        entry.getValue().performDelayedAction();
                    }
                    delayedCallbacks.clear();
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public boolean isProductInWishlist(String productId) {
        for (int i = 0; i < wishlist.size(); i++) {
            if (wishlist.get(i).getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setWishlistChangeListener(WishlistChangeListener listener) {
        wishlistChangeListener = listener;
    }

    @Override
    public void setCheckoutHasWishlistItem(boolean hasWishlistItem) {
        doesCheckoutHaveWishlistItem = hasWishlistItem;
    }

    @Override
    public boolean doesCheckoutHaveWishlistItem() {
        return doesCheckoutHaveWishlistItem;
    }

    @Override
    public void updateWishlistCount() {
        if (wishlistChangeListener != null) {
            oldCount = wishlist.size();
            wishlistChangeListener.wishlistCountChanged(wishlist.size());
        }
    }
}
