package au.com.dealsdirect.ui.custom;

import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.PriceUtils;

public class SupplierOriginalPriceInfoHelper {
    private BottomSheetInfoWebViewDialog currentViewDialog = null;

    public static String content1 = "We routinely source end of season or clearance stock from international markets and suppliers for sale on NZSale’s website. \n" +
            "All discounts shown on our website are discounts off either the supplier’s original retail price (ORP) or current recommended retail price (RRP).\n" +
            "Suppliers’ original retail price is the retail price at which a product has previously been sold or offered for sale.  Supplier’s RRP is the supplier’s recommendation of the retail price at which a product be offered for sale by a retailer.  The product may not have been offered or sold at that price at all or recently, in New Zealand or elsewhere.\n" +
            "Where a suppliers original retail price or current RRP is provided by a supplier in NZD, we have used this original price.  Where a supplier has provided an original retail price or RRP in another currency we have converted it to NZD. \n" +
            "<a href=\"http://google.com\">More info</a>";

    public static String content2 = "We routinely source end of season or clearance stock from international markets and suppliers for sale on NZSale’s website. \n" +
            "All discounts shown on our website are discounts off either the supplier’s original retail price (ORP) or current recommended retail price (RRP).\n" +
            "Suppliers’ original retail price is the retail price at which a product has previously been sold or offered for sale.  Supplier’s RRP is the supplier’s recommendation of the retail price at which a product be offered for sale by a retailer.  The product may not have been offered or sold at that price at all or recently, in New Zealand or elsewhere.\n" +
            "Where a suppliers original retail price or current RRP is provided by a supplier in NZD, we have used this original price.  Where a supplier has provided an original retail price or RRP in another currency we have converted it to NZD. \n" +
            "<a href=\"http://google.com\">More info</a>";

    public void showSaleListNotice(FragmentManager fragmentManager, OnDismissSaleNotice onDismissListener) {

        BottomSheetInfoWebViewDialog bottomSheetFragment = new BottomSheetInfoWebViewDialog();

        bottomSheetFragment.setWebViewContent(content1);
        bottomSheetFragment.setCloseButtonVisible(true);
        if (onDismissListener == null) {
            bottomSheetFragment.setOnDismissListener(this::clearCurrentViewDialog);
            bottomSheetFragment.setOnCancelListener(this::clearCurrentViewDialog);
        } else {
            bottomSheetFragment.setOnDismissListener(() -> {
                onDismissListener.onDismiss();
                clearCurrentViewDialog();
            });
            bottomSheetFragment.setOnCancelListener(() -> {
                onDismissListener.onDismiss();
                clearCurrentViewDialog();
            });
        }

        bottomSheetFragment.show(fragmentManager, ActionConstants.ORDER_BOTTOM_WEBVIEW_DIALOG_TAG);

        currentViewDialog = bottomSheetFragment;
    }

    public BottomPopupView createSaleListNoticeBottomPopupView(ViewGroup parent, OnDismissSaleNotice onDismissListener) {
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        final BottomPopupView bottomPopupView = new BottomPopupView(parent, adapter);

        adapter.setWebViewContent(content1);
        adapter.setOnCloseButtonClickListener(() -> bottomPopupView.dismiss(true));
        bottomPopupView.setListener(new BottomPopupView.BottomPopupViewListener() {
            @Override
            public void willShow() {

            }

            @Override
            public void onShow() {

            }

            @Override
            public void willDismiss() {

            }

            @Override
            public void onDismiss() {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        });
        return bottomPopupView;
    }

    public void showSupplierOriginalPriceInfo(FragmentManager fragmentManager, String prepend, SaleItemProduct itemProduct) {
        final Map<String, String> replacements = createStringReplacementsFromSaleItem(itemProduct);
        showSupplierOriginalPriceInfo(
                fragmentManager,
                prepend,
                replacements);
    }

    public void showSupplierOriginalPriceInfo(FragmentManager fragmentManager, String prepend, Map<String, String> replacements) {
        BottomSheetInfoWebViewDialog bottomSheetFragment = new BottomSheetInfoWebViewDialog();

        bottomSheetFragment.setWebViewContent(getOriginalPriceInfoWebViewContent(prepend, replacements));
        bottomSheetFragment.setCloseButtonVisible(true);
        bottomSheetFragment.setOnDismissListener(this::clearCurrentViewDialog);
        bottomSheetFragment.setOnCancelListener(this::clearCurrentViewDialog);

        bottomSheetFragment.show(fragmentManager, ActionConstants.ORDER_BOTTOM_WEBVIEW_DIALOG_TAG);

        currentViewDialog = bottomSheetFragment;
    }

    public static Map<String, String> createStringReplacementsFromSaleItem(SaleItemProduct itemProduct) {
        return createStringReplacementsWithItemValues(
                itemProduct.getTotalPercentOff(),
                itemProduct.getOriginalPrice() != null ? itemProduct.getOriginalPrice().getValue() : null);
    }

    public String getOriginalPriceInfoWebViewContent(String prepend, SaleItemProduct itemProduct) {
        return getOriginalPriceInfoWebViewContent(prepend, createStringReplacementsFromSaleItem(itemProduct));
    }

    public static Map<String, String> createStringReplacementsWithItemValues(Double totalPercentOff, Double originalPrice) {
        final Map<String, String> replacements = new HashMap<>();
        replacements.put("discountPercentOff", totalPercentOff != null ? Integer.toString((int) Math.floor(totalPercentOff)) : "??");
        replacements.put("priceValue", originalPrice != null ? PriceUtils.getPriceStringValue(originalPrice) : "??");
        return replacements;
    }

    public String getOriginalPriceInfoWebViewContent(String prepend, Double totalPercentOff, Double originalPrice) {
        return getOriginalPriceInfoWebViewContent(prepend, createStringReplacementsWithItemValues(totalPercentOff, originalPrice));
    }

    private String getOriginalPriceInfoWebViewContent(String prepend, Map<String, String> replacements) {
        String content = content2;

        for (String key : replacements.keySet()) {
            final String replacement = replacements.get(key);
            if (replacement != null) {
                content = content.replace("[[" + key + "]]", replacement);
            }
        }

        if (prepend != null && !prepend.isEmpty()) {
            content = prepend + "<br /><br />" + content;
        }

        return content;
    }

    public void updateOriginalPriceInfoContent(String prepend, SaleItemProduct itemProduct) {
        updateOriginalPriceInfoContent(prepend, createStringReplacementsFromSaleItem(itemProduct));
    }

    public void updateOriginalPriceInfoContent(String prepend, Map<String, String> replacements) {
        if (currentViewDialog == null) {
            return;
        }

        currentViewDialog.setWebViewContent(getOriginalPriceInfoWebViewContent(prepend, replacements));
    }

    private void clearCurrentViewDialog() {
        currentViewDialog.setOnDismissListener(null);
        currentViewDialog = null;
    }

    public static void setContent1(String text) {
        content1 = text;
    }

    public static void setContent2(String text) {
        content2 = text;
    }

    public BottomSheetInfoWebViewDialog getCurrentViewDialog() {
        return currentViewDialog;
    }

    public static boolean shouldShowSaleNotice(long timestamp) {
        final long currentTimestamp = System.currentTimeMillis();
        final long timeElapsed = currentTimestamp - timestamp;
        return TimeUnit.MILLISECONDS.toDays(timeElapsed) >= 14;
    }

    public interface OnDismissSaleNotice {
        void onDismiss();
    }
}
