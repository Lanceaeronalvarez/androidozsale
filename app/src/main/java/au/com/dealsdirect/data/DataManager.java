package au.com.dealsdirect.data;

import au.com.dealsdirect.data.auth.AuthHelper;
import au.com.dealsdirect.data.cachedresponses.CachedResponseHelper;
import au.com.dealsdirect.data.cart.CartHelper;
import au.com.dealsdirect.data.network.ApiHelper;
import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.data.priceinfo.PricingInfoCacheHelper;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.data.wishlist.WishlistHelper;

public interface DataManager extends PreferencesHelper, ApiHelper, AuthHelper, WishlistHelper, CachedResponseHelper, TemplateTextsHelper, PricingInfoCacheHelper, CartHelper {

    boolean isTablet();

}
