package au.com.dealsdirect.ui.controller.brands.adapter;

import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;

public interface TopBrandsAdapterHelper {
    int getColumnCount();

    void onBrandClick(GetTopBrandsResponse brand);

    void onInfoClick(String title, String description);
}
