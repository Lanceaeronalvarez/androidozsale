package au.com.dealsdirect.data.wishlist;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppWishlistHelper implements WishlistHelper {
    private WishlistChangeListener wishlistChangeListener;

    private boolean doesCheckoutHaveWishlistItem = false;

    private ArrayList<WishlistObject> wishlist = new ArrayList<>();
    private int oldCount = -1;

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
    public void addToWishlist(WishlistObject object) {
        wishlist.add(object);
        updateWishlistCount();
    }

    @Override
    public void removeFromWishlist(String productId) {
        int i = 0;
        while (i < wishlist.size()) {
            if (wishlist.get(i).getProductId().equals(productId)) {
                wishlist.remove(i);
            } else {
                i++;
            }
        }
        updateWishlistCount();
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
