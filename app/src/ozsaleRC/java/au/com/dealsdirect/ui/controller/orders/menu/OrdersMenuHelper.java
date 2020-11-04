package au.com.dealsdirect.ui.controller.orders.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.CancelInvoiceItemRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.contact.selectsubject.ContactSelectSubjectController;
import au.com.dealsdirect.ui.controller.orders.orders.BottomDialogCancelOrders;
import au.com.dealsdirect.ui.controller.orders.orders.BottomSheetOrderDialog;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnController;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.ActionConstants;

public class OrdersMenuHelper {

    private OrdersMenuHelper() {
    }

    public static void showOrderBottomDialog(Controller controller,
                                             int orderNumber,
                                             String invoiceId,
                                             int invoiceNumber,
                                             GetOrdersResponse.Order.Invoice.Product product,
                                             List<String> actions,
                                             ViewAddressController.OnAddressSelected onAddressSelected,
                                             CancelInvoiceItemAction cancelAction) {
        if (!(controller.getActivity() instanceof FragmentActivity)) {
            return;
        }
        FragmentActivity activity = (FragmentActivity) controller.getActivity();
        boolean isItemCancel = actions.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND);

        String description = "Invoice No: " + invoiceNumber;

        BottomSheetOrderDialog bottomSheetFragment = new BottomSheetOrderDialog(
                new BottomSheetOrderDialog.BottomSheetButtonListener() {
                    @Override
                    public void onContactUsPressed() {
                        showContactUs(controller, orderNumber, invoiceNumber);
                    }

                    @Override
                    public void onOrderPressed() {
                    }

                    @Override
                    public void onChangeAddressPressed() {
                        showChangeAddress(controller, onAddressSelected);
                    }

                    @Override
                    public void onReturnItemPressed() {
                        if (product != null) {
                            showReturnItems(controller, invoiceNumber, product.getOrderItemId());
                        }
                    }

                    @Override
                    public void onCancelOrderPressed() {
                        CancelInvoiceItemRequest request = new CancelInvoiceItemRequest();
                        request.setInvoiceNumber(invoiceNumber);
                        request.setInvoiceId(invoiceId);
                        if (!isItemCancel) {
                            showCancelDialogForPhone(activity, request, cancelAction);
                        } else if (product != null) {
                            request.setOrderItemId(product.getOrderItemId());
                            int quantity = product.getQuantity() - (product.getCancelledQuantity() == null ? 0 : product.getCancelledQuantity());
                            showCancelItemDialogForPhone(
                                    activity,
                                    product.getImageUrl(),
                                    product.getName(),
                                    invoiceNumber,
                                    quantity,
                                    request,
                                    cancelAction);
                        }
                    }

                    @Override
                    public void onViewReturnItemPressed() {
                        if (product != null && product.getReturnId() != null) {
                            showViewReturnDetails(controller, product.getReturnId(), description);
                        }
                    }
                });
        Bundle bundle = new Bundle();

        bundle.putStringArrayList(ActionConstants.ORDER_ARRAYS, new ArrayList<>(actions));

        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(activity.getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }

    public static void showPopupMenu(Controller controller,
                                     int orderNumber,
                                     String invoiceId, int invoiceNumber,
                                     GetOrdersResponse.Order.Invoice.Product product,
                                     List<String> actions,
                                     ViewAddressController.OnAddressSelected onAddressSelected,
                                     CancelInvoiceItemAction cancelAction) {
        if (controller.getActivity() == null || controller.getView() == null) {
            return;
        }

        boolean showChangeAddress = actions.contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS);
        boolean showRequestReturn = actions.contains(ActionConstants.ORDER_ITEM_RETURN);
        boolean showContactUs = actions.contains(ActionConstants.ORDER_ACTION_CHECK_STATUS);
        boolean showViewReturns = actions.contains(ActionConstants.ORDER_ITEM_VIEW_RETURN);
        boolean showCancel = (actions.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND) ||
                actions.contains(ActionConstants.ORDER_ACTION_REFUND));
        boolean isItemCancel = actions.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND);

        PopupMenu popup = new PopupMenu(controller.getActivity(), controller.getView());
        popup.getMenuInflater().inflate(R.menu.order_actions_pop_up, popup.getMenu());

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Menu menu = popup.getMenu();
        MenuItem changeAddress = menu.findItem(R.id.change_address);
        changeAddress.setIcon(controller.getActivity().getResources().getDrawable(R.drawable.ic_change_address));
        changeAddress.setVisible(showChangeAddress);

        MenuItem requestReturn = menu.findItem(R.id.return_item);
        requestReturn.setIcon(controller.getActivity().getResources().getDrawable(R.drawable.ic_return_item));
        requestReturn.setVisible(showRequestReturn);

        MenuItem contactUs = menu.findItem(R.id.contact_us);
        contactUs.setIcon(controller.getActivity().getResources().getDrawable(R.drawable.ic_contact_us));
        contactUs.setVisible(showContactUs);

        MenuItem viewReturns = menu.findItem(R.id.view_return_details);
        viewReturns.setIcon(controller.getActivity().getResources().getDrawable(R.drawable.ic_return_item));
        viewReturns.setVisible(showViewReturns);

        MenuItem cancel = menu.findItem(R.id.cancel_order);
        cancel.setIcon(controller.getActivity().getResources().getDrawable(R.drawable.ic_cancel_order));
        cancel.setVisible(showCancel);

        String description = "Invoice No: " + invoiceNumber;

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.contact_us:
                    showContactUs(controller, orderNumber, invoiceNumber);
                    return true;
                case R.id.change_address:
                    showChangeAddress(controller, onAddressSelected);
                    return true;
                case R.id.return_item:
                    if (product != null) {
                        showReturnItems(controller, invoiceNumber, product.getOrderItemId());
                    }
                    return true;
                case R.id.view_return_details:
                    if (product != null && product.getReturnId() != null) {
                        showViewReturnDetails(controller, product.getReturnId(), description);
                    }
                    return true;
                case R.id.cancel_order:
                    CancelInvoiceItemRequest request = new CancelInvoiceItemRequest();
                    request.setInvoiceNumber(invoiceNumber);
                    request.setInvoiceId(invoiceId);

                    if (!isItemCancel) {
                        showCancelDialogForTablet(controller.getActivity(), request, cancelAction);
                    } else if (product != null) {
                        request.setOrderItemId(product.getOrderItemId());
                        int quantity = product.getQuantity() - (product.getCancelledQuantity() == null ? 0 : product.getCancelledQuantity());
                        showCancelItemDialogForTablet(
                                controller.getActivity(),
                                product.getImageUrl(),
                                product.getName(),
                                quantity,
                                request,
                                cancelAction);
                    }
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    private static void showContactUs(Controller controller, int orderNumber, int invoiceNumber) {
        RouterTransaction routerTransaction = RouterTransaction.with(ContactSelectSubjectController.newInstance(orderNumber, invoiceNumber))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());
        controller.getRouter().pushController(routerTransaction);
    }

    private static void showChangeAddress(Controller controller, ViewAddressController.OnAddressSelected onAddressSelected) {
        ViewAddressController viewAddressController = ViewAddressController.newInstance();
        viewAddressController.setOnAddressSelected(onAddressSelected);
        controller.getRouter().pushController(RouterTransaction.with(viewAddressController)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private static void showReturnItems(Controller controller, int invoiceNumber, String productId) {
        RouterTransaction routerTransaction = RouterTransaction.with(NewReturnController.newInstance(invoiceNumber, true, productId))
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler());

        controller.getRouter().pushController(routerTransaction);
    }

    private static void showViewReturnDetails(Controller controller, String returnID, String productName) {
        RouterTransaction routerTransaction = RouterTransaction.with(ReturnDetailsController.newInstance(returnID, productName, true))
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler());

        controller.getRouter().pushController(routerTransaction);

    }

    private static void showCancelDialogForPhone(FragmentActivity activity,
                                                 CancelInvoiceItemRequest request,
                                                 CancelInvoiceItemAction action) {
        BottomDialogCancelOrders bottomSheetFragment = new BottomDialogCancelOrders(
                new BottomDialogCancelOrders.BottomDialogButtonListener() {
                    @Override
                    public void onYes(Object object) {
                        action.requestCancelInvoiceItem(request);
                    }

                    @Override
                    public void onNo(Object object) {

                    }

                    @Override
                    public void onClose(Object object) {

                    }
                });
        Bundle bundle = new Bundle();

        bundle.putInt(ActionConstants.ORDER_INVOICE_NUMBER, request.getInvoiceNumber());
        bundle.putBoolean(ActionConstants.ORDER_SHOULD_SHOW_CANCEL_ORDER, true);

        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(activity.getSupportFragmentManager(), "DialogBottomCancelOrders");
    }

    private static void showCancelDialogForTablet(Activity activity,
                                                  CancelInvoiceItemRequest request,
                                                  CancelInvoiceItemAction action) {
        CustomAlertDialog.showCustomCancelOrderDialog(
                activity,
                request.getInvoiceNumber(),
                new CustomAlertDialog.CustomDialogButtonListener() {
                    @Override
                    public void onYes(Object object) {
                        action.requestCancelInvoiceItem(request);
                    }

                    @Override
                    public void onNo(Object object) {

                    }

                    @Override
                    public void onClose(Object object) {

                    }
                });
    }

    public interface CancelInvoiceItemAction {
        void requestCancelInvoiceItem(CancelInvoiceItemRequest request);
    }

    private static void showCancelItemDialogForPhone(FragmentActivity activity, String imageUrl,
                                                     String itemName, int invoiceNumber,
                                                     int quantity, CancelInvoiceItemRequest request,
                                                     CancelInvoiceItemAction action) {
        BottomDialogCancelOrders bottomSheetFragment = new BottomDialogCancelOrders(
                new BottomDialogCancelOrders.BottomDialogButtonListener() {
                    @Override
                    public void onYes(Object object) {
                        if (object instanceof String) {
                            request.setQuantity(Integer.parseInt((String) object));
                        }
                        action.requestCancelInvoiceItem(request);
                    }

                    @Override
                    public void onNo(Object object) {

                    }

                    @Override
                    public void onClose(Object object) {

                    }
                });
        Bundle bundle = new Bundle();

        bundle.putString(ActionConstants.ORDER_ITEM_IMAGE_URL, imageUrl);
        bundle.putString(ActionConstants.ORDER_ITEM_DESCRIPTION, itemName);
        bundle.putInt(ActionConstants.ORDER_INVOICE_NUMBER, invoiceNumber);
        bundle.putInt(ActionConstants.ORDER_QUANTITY, quantity);
        bundle.putBoolean(ActionConstants.ORDER_SHOULD_SHOW_CANCEL_ORDER, false);

        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(activity.getSupportFragmentManager(), "DialogBottomCancelItemOrders");
    }

    private static void showCancelItemDialogForTablet(Activity activity, String imageUrl,
                                                      String itemName, int quantity,
                                                      CancelInvoiceItemRequest request,
                                                      CancelInvoiceItemAction action) {
        CustomAlertDialog.showCancelItemDialog(
                activity,
                imageUrl,
                itemName,
                quantity,
                new CustomAlertDialog.CustomDialogButtonListener() {
                    @Override
                    public void onYes(Object object) {
                        if (object instanceof String) {
                            request.setQuantity(Integer.parseInt((String) object));
                        }
                        action.requestCancelInvoiceItem(request);
                    }

                    @Override
                    public void onNo(Object object) {

                    }

                    @Override
                    public void onClose(Object object) {

                    }
                });
    }
}
