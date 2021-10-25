package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import java.util.HashSet;
import java.util.Set;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.utils.BundleKeys;

/**
 * Created by smartwave on 24/07/2017.
 */

public class SearchChipModel {

    private String mFilterType;
    private String mChipTitle;
    private String mKey;
    private int minValue;
    private int maxValue;

    public static Set<SearchChipModel> chipListFromStoreId(String storeId) {
        if (storeId == null || storeId.isEmpty()) {
            return null;
        }

        return new HashSet<SearchChipModel>() {
            {
                add(new SearchChipModel(BundleKeys.STORE_ID_FACETFILTER_NAME, storeId));
            }
        };
    }

    public static Set<SearchChipModel> chipListFromLinkOptions(GetBannerResponse.LinkOptions linkOptions) {
        final GetBannerResponse.LinkOptions.Facets facets = linkOptions.getFacets();
        final Set<SearchChipModel> chipModels = new HashSet<>();
        if (linkOptions.getSorting() != null && !linkOptions.getSorting().isEmpty()) {
            chipModels.add(new SearchChipModel(BundleKeys.SORT_FACETFILTER_NAME, linkOptions.getSorting(), linkOptions.getSorting()));
        }

        if (facets.getBrandNames() != null) {
            for (String brandName : facets.getBrandNames()) {
                chipModels.add(new SearchChipModel(BundleKeys.BRANDS_FACETFILTER_NAME, brandName));
            }
        }
        if (facets.getColors() != null) {
            for (String color : facets.getColors()) {
                chipModels.add(new SearchChipModel(BundleKeys.COLORS_FACETFILTER_NAME, color));
            }
        }
        if (facets.getDelivery() != null) {
            for (String delivery : facets.getDelivery()) {
                chipModels.add(new SearchChipModel(BundleKeys.DELIVERY_FACETFILTER_NAME, delivery));
            }
        }
        if (facets.getNewArrivals() != null) {
            for (String newArrival : facets.getNewArrivals()) {
                chipModels.add(new SearchChipModel(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME, newArrival));
            }
        }
        if (facets.getPriceLimit() != null) {
            final GetBannerResponse.LinkOptions.Facets.PriceLimit priceLimit = facets.getPriceLimit();
            final SearchChipModel priceChip = new SearchChipModel(
                    BundleKeys.PRICE_FACETFILTER_NAME,
                    priceLimit.getFrom() + " to " + priceLimit.getMax());
            priceChip.setMaxValue(priceLimit.getMax());
            priceChip.setMinValue(priceLimit.getFrom());
            chipModels.add(priceChip);
        }
        if (facets.getSizes() != null) {
            for (String size : facets.getSizes()) {
                chipModels.add(new SearchChipModel(BundleKeys.SIZES_FACETFILTER_NAME, size));
            }
        }
        return chipModels;
    }

    public SearchChipModel(String mFilterType, String mChipTitle) {
        this.mFilterType = mFilterType;
        this.mChipTitle = mChipTitle;
        this.mKey = null;
    }

    public SearchChipModel(String mFilterType, String mChipTitle, String key) {
        this.mFilterType = mFilterType;
        this.mChipTitle = mChipTitle;
        this.mKey = key;
    }

    public String getFilterType() {
        return mFilterType;
    }

    public void setFilterType(String mFilterType) {
        this.mFilterType = mFilterType;
    }

    public String getChipTitle() {
        return mChipTitle;
    }

    public void setChipTitle(String mChipTitle) {
        this.mChipTitle = mChipTitle;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }
}
