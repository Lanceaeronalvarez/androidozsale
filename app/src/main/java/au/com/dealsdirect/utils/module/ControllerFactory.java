package au.com.dealsdirect.utils.module;

import android.os.Bundle;

import com.bluelinelabs.conductor.Controller;

import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.register.RegisterController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;

/**
 * Created by smartwave on 16/10/2017.
 */

//register here controllers and how to create instances of them
public class ControllerFactory {
    public static Controller getInstance(GateKeeper.Destination destination) {
        return getInstance(destination, new Bundle());
    }

    public static Controller getInstance(GateKeeper.Destination destination, Bundle bundle) {
        return determineConstructionType(destination, bundle);
    }

    private static Controller determineConstructionType(GateKeeper.Destination destination, Bundle bundle) {
        return bundle == null || bundle.isEmpty() ? simpleConstruction(destination) : buildWithParameters(destination, bundle);
    }

    private static Controller simpleConstruction(GateKeeper.Destination destination) {
        switch (destination) {
            case MAIN:
                return MainController.newInstance();
            case DETAILS:
                return DetailsController.newInstance();
            case LOGIN:
                return LoginController.newInstance();
            case CATEGORIES:
                return CategoriesController.newInstance();
            case CHECKOUT:
                return new CheckoutController();
            case SALEITEMS:
                return SaleItemsController.newInstance();
            case ACCOUNT:
                return AccountController.newInstance();
            case REGISTER:
                return RegisterController.newInstance();
            case FORGOT_PASSWORD:
                return ForgotPasswordController.newInstance();
            case VIEW_VOUCHERS:
                return ViewVouchersController.newInstance();
            case INVITE:
                return InviteSendController.newInstance();
            case ORDERS:
                return new OrdersController();
            case CURRENT_RETURNS:
                return CurrentReturnsController.newInstance();
            case VIEW_ADDRESSES:
                return new ViewAddressController(false, null);
            case CONTACT_US:
                return ViewContactsController.newInstance();
            case FACET_FILTER:
                return FacetFilterController.newInstance();
            default:
                return null;
        }
    }

    private static Controller buildWithParameters(GateKeeper.Destination destination, Bundle bundle) {
        switch (destination) {
            case MAIN:
                return new MainController(bundle);
            case DETAILS:
                return new DetailsController(bundle);
            case LOGIN:
                return new LoginController(bundle);
            case CATEGORIES:
                return new CategoriesController(bundle);
            case CHECKOUT:
                return new CheckoutController();
            case SALEITEMS:
                return new SaleItemsController(bundle);
            case ACCOUNT:
                return new AccountController(bundle);
            case REGISTER:
                return new RegisterController(bundle);
            case FORGOT_PASSWORD:
                return new ForgotPasswordController(bundle);
            case ADD_VOUCHERS:
                return new AddVouchersController(bundle);
            case PAYMENT_ADD:
                return new AddPaymentController(bundle);
            case PAYMENT_SELECT:
                return new PaymentSelectController(bundle);
            case ORDERS:
                return new OrdersController();
            case CURRENT_RETURNS:
                return CurrentReturnsController.newInstance();
            case VIEW_ADDRESSES:
                return new ViewAddressController(false, null);
            case CONTACT_US:
                return ViewContactsController.newInstance();
            case LEGALITIES:
                return new LegalitiesController(bundle);
            case FACET_FILTER:
                return new FacetFilterController(bundle);
            default:
                return null;
        }
    }

    public static GateKeeper.Destination mapControllerToDestination(Controller controller) {
        if (controller instanceof MainController) {
            return GateKeeper.Destination.MAIN;
        }

        if (controller instanceof DetailsController) {
            return GateKeeper.Destination.DETAILS;
        }

        if (controller instanceof LoginController) {
            return GateKeeper.Destination.LOGIN;
        }

        if (controller instanceof CategoriesController) {
            return GateKeeper.Destination.CATEGORIES;
        }

        if (controller instanceof CheckoutController) {
            return GateKeeper.Destination.CHECKOUT;
        }

        if (controller instanceof SaleItemsController) {
            return GateKeeper.Destination.SALEITEMS;
        }

        if (controller instanceof AccountController) {
            return GateKeeper.Destination.ACCOUNT;
        }

        if (controller instanceof RegisterController) {
            return GateKeeper.Destination.REGISTER;
        }

        if (controller instanceof ForgotPasswordController) {
            return GateKeeper.Destination.FORGOT_PASSWORD;
        }

        if (controller instanceof  ViewVouchersController) {
            return GateKeeper.Destination.VIEW_VOUCHERS;
        }

        if (controller instanceof InviteSendController) {
            return GateKeeper.Destination.INVITE;
        }

        if (controller instanceof LegalitiesController) {
            return GateKeeper.Destination.LEGALITIES;
	}

        if (controller instanceof FacetFilterController) {
            return GateKeeper.Destination.FACET_FILTER;
        }

        return GateKeeper.Destination.EMPTY;
    }
}
