package au.com.dealsdirect.ui.controller.saleitems;

/**
 * Created by smartwave on 14/11/2017.
 */

public interface CategoryObserver {

    void onCategoryChangeUpdateUI(String text, int color);

    void showShopCategoryText();
}
