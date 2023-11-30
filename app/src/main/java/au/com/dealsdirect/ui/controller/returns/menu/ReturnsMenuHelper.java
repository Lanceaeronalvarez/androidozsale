package au.com.dealsdirect.ui.controller.returns.menu;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.BottomSheetReturnsDialog;
import au.com.dealsdirect.utils.ActionConstants;

public class ReturnsMenuHelper {

    private ReturnsMenuHelper() {
    }

    public static void showCurrentReturnsDialog(Controller controller,
                                                SelectOptionListener listener) {
        if (!(controller.getActivity() instanceof FragmentActivity)) {
            return;
        }
        FragmentActivity activity = (FragmentActivity) controller.getActivity();

        BottomSheetReturnsDialog bottomSheetFragment = new BottomSheetReturnsDialog(
                listener::onSelectNeedHelp
        );
        Bundle bundle = new Bundle();

        ArrayList<String> actions = new ArrayList<>();
        actions.add(ActionConstants.ORDER_ACTION_CHECK_STATUS);

        bundle.putStringArrayList(ActionConstants.ORDER_ARRAYS, actions);

        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(activity.getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }

    public static void showPopupMenu(Controller controller,
                                     View anchor,
                                     SelectOptionListener listener) {
        if (controller.getActivity() == null || controller.getView() == null) {
            return;
        }

        PopupMenu popup = new PopupMenu(controller.getActivity(), anchor);
        popup.getMenuInflater().inflate(R.menu.returns_actions_pop_up, popup.getMenu());

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

        MenuItem needHelp = menu.findItem(R.id.need_help);
        needHelp.setIcon(controller.getActivity().getResources().getDrawable(R.drawable.ic_contact_us));
        needHelp.setVisible(true);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.need_help:
                    listener.onSelectNeedHelp();
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    private static void showContactUs(Controller controller, int invoiceNumber) {
        if (!(controller.getActivity() instanceof FragmentActivity)) {
            return;
        }
        final String subject = controller.getActivity().getResources().getString(R.string.returns_enquiry);
        AddContactController newController = (AddContactController) controller.getRouter().getControllerWithTag(AddContactController.TAG);
        if (newController == null) {
            newController = AddContactController.newInstance();
            setupAddContactController(newController, invoiceNumber, subject);
            RouterTransaction routerTransaction = RouterTransaction.with(newController)
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler())
                    .tag(AddContactController.TAG);
            controller.getRouter().pushController(routerTransaction);
        } else {
            setupAddContactController(newController, invoiceNumber, subject);
            controller.getRouter().popCurrentController();
        }
    }

    private static void setupAddContactController(AddContactController controller, int invoiceNumber, String subject) {
        controller.setSubjectId("d4dc457fd28b4284b2084e29c2d540e4");
        controller.setSubject(subject);
        controller.setIsInvoiceRequired(true);
        controller.setInvoiceNumber(invoiceNumber);
        controller.setOrderNumber(0);
        ArrayList<String> actions = new ArrayList<>();
        actions.add(ActionConstants.ORDER_ITEM_RETURN);
        controller.setActions(actions);
    }

    public static interface SelectOptionListener {
        void onSelectNeedHelp();
    }
}
