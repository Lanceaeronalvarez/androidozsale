package au.com.dealsdirect.data.wishlist;

import java.util.List;

public interface WishlistHelper {
    void setWishlist(List<WishlistObject> wishlist);

    List<WishlistObject> getWishlist();

    void addToWishlist(WishlistObject object, WishlistChangeDelayedCallback delayedCallback);

    void removeFromWishlist(String productId, WishlistChangeDelayedCallback delayedCallback);

    boolean isProductInWishlist(String productId);

    void setWishlistChangeListener(WishlistChangeListener listener);

    void setCheckoutHasWishlistItem(boolean hasWishlistItem);

    boolean doesCheckoutHaveWishlistItem();

    void updateWishlistCount();

    public interface WishlistChangeDelayedCallback {
        void performDelayedAction();
    }
}