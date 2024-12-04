package au.com.dealsdirect.ui.controller.saleitems;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public class SaleItemFooterPlaceholder extends SaleItemProduct {
    public static final int VIEW_TYPE_UNDEFINED = 0;
    public static final int VIEW_TYPE_ADMOB = 1;
    private transient int viewType = 0;

    public SaleItemFooterPlaceholder(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
