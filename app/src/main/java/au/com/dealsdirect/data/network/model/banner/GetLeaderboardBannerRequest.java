package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.cachedresponses.CachableRequest;
public class GetLeaderboardBannerRequest implements CachableRequest {

    public static String SHOP_PAGE = "ShopPage";
    public static String PRODUCT_LIST = "ProductList";
    public static String PRODUCT_DETAILS = "ProductDetails";

    public static String MOBILE_BROWSER = "MobileBrowser";

    public static String DESKTOP_BROWSER = "DesktopBrowser";

    @SerializedName("pageName")
    @Expose
    private String pageName;

    @Expose
    @SerializedName("limit")
    private int limit;

    @Expose
    @SerializedName("bannergroups")
    private int bannergroups;

    @Expose
    @SerializedName("frontEnd")
    private String frontEnd;

    @Expose
    @SerializedName("category")
    private String categoryId = null;

    public GetLeaderboardBannerRequest(String pageName, String frontEnd) {
        this.pageName = pageName;
        this.frontEnd = frontEnd;
        bannergroups = 12;
        limit = 50;
    }

    public static GetLeaderboardBannerRequest newInstanceForShopPage(String frontend, String categoryId) {
        GetLeaderboardBannerRequest request = new GetLeaderboardBannerRequest(SHOP_PAGE, frontend);
        request.categoryId = categoryId;
        return request;
    }

    public static GetLeaderboardBannerRequest newInstanceForProductList(String frontEnd) {
        return new GetLeaderboardBannerRequest(PRODUCT_LIST, frontEnd);
    }

    public static GetLeaderboardBannerRequest newInstanceForProductDetails(String frontEnd) {
        return new GetLeaderboardBannerRequest(PRODUCT_DETAILS, frontEnd);
    }

    @Override
    public String getCacheKey() {
        return getClass().getSimpleName() + "," + pageName;
    }
}
