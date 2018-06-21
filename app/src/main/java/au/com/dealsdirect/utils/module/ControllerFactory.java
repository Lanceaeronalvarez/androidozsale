package au.com.dealsdirect.utils.module;

import android.os.Bundle;

import com.bluelinelabs.conductor.Controller;
import com.mysale.genie.utility.config.model.getappsettings.Checkout;

import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.selectsubject.ContactSelectSubjectController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.login.LoginHostController;
import au.com.dealsdirect.ui.controller.login.PasswordVerificationController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.notification.NotificationController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.register.RegisterController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.controller.tutorial.TutorialController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;

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
            case LOGIN_HOST:
                return LoginHostController.newInstance();
            case CATEGORIES:
                return CategoriesController.newInstance();
            case CHECKOUT:
                return CheckoutController.newInstance();
            case SALEITEMS:
                return SaleItemsController.newInstance();
            case ACCOUNT:
                return AccountController.newInstance();
            case REGISTER:
                return RegisterController.newInstance();
            case FORGOT_PASSWORD:
                return ForgotPasswordController.newInstance();
            case PASSWORD_VERIFICATION:
                return PasswordVerificationController.newInstance();
            case VIEW_VOUCHERS:
                return ViewVouchersController.newInstance();
            case INVITE:
                return InviteSendController.newInstance();
            case ORDERS:
                return OrdersController.newInstance();
            case CURRENT_RETURNS:
                return CurrentReturnsController.newInstance();
            case RETURN_ORDERS:
                return ReturnOrdersController.newInstance();
            case VIEW_ADDRESSES:
                return new ViewAddressController(false, null);
            case ADD_NEW_ADDRESS:
                return new AddNewAddressController(new Bundle());
            case CONTACT_US:
                return ViewContactsController.newInstance();
            case SPLASH:
                return SplashScreenController.newInstance();
            case SALEITEM_DETAILS:
                break;
            case ADD_VOUCHERS:
                break;
            case ADD_CONTACT:
                return AddContactController.newInstance();
            case PAYMENT_SELECT:
                return PaymentSelectController.newInstance();
            case PAYMENT_ADD:
                return AddPaymentController.newInstance();
            case PAYMENT_SUCCESS:
                break;
            case MASTERPASS:
                return MasterpassController.newInstance();
            case ORDER_DETAILS:
                break;
            case SEARCH_FILTER:
                return SearchFilterController.newInstance();
            case LEGALITIES:
                break;
            case LANGUAGE:
                return LanguageController.newInstance();
            case COUNTRY:
                return CountryController.newInstance();
            case NOTIFICATION:
                return NotificationController.newInstance();
            case TUTORIAL:
                return TutorialController.newInstance();
            default:
                return null;
        }
        return null;
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
                return new CheckoutController(bundle);
            case SALEITEMS:
                return new SaleItemsController(bundle);
            case ACCOUNT:
                return new AccountController(bundle);
            case REGISTER:
                return new RegisterController(bundle);
            case FORGOT_PASSWORD:
                return new ForgotPasswordController(bundle);
            case PASSWORD_VERIFICATION:
                return new PasswordVerificationController(bundle);
            case ADD_VOUCHERS:
                return new AddVouchersController(bundle);
            case PAYMENT_ADD:
                return new AddPaymentController(bundle);
            case PAYMENT_SELECT:
                return new PaymentSelectController(bundle);
            case PAYMENT_SUCCESS:
                return new PaymentSuccessController(bundle);
            case MASTERPASS:
                return new MasterpassController(bundle);
            case ORDERS:
                return new OrdersController(bundle);
            case CURRENT_RETURNS:
                return CurrentReturnsController.newInstance();
            case RETURN_ORDERS:
                return ReturnOrdersController.newInstance();
            case VIEW_ADDRESSES:
                return new ViewAddressController(bundle);
            case ADD_NEW_ADDRESS:
                return new AddNewAddressController(bundle);
            case CONTACT_US:
                return ViewContactsController.newInstance();
            case CONTACT_HISTORY:
                return new ViewContactHistoryController(bundle);
            case ADD_CONTACT:
                return new AddContactController(bundle);
            case CONTACT_SELECT_SUBJECT:
                return new ContactSelectSubjectController(bundle);
            case CONTACT_SELECT_ORDER:
                return new ContactSelectOrderController(bundle);
            case SMS_VERIFICATION:
                return new OurpaySMSVerificationController(bundle);
            case LEGALITIES:
                return new LegalitiesController(bundle);
            case SEARCH_FILTER:
                return new SearchFilterController(bundle);
            case LANGUAGE:
                return LanguageController.newInstance();
            case COUNTRY:
                return CountryController.newInstance();
            case NOTIFICATION:
                return NotificationController.newInstance();
            case TUTORIAL:
                return TutorialController.newInstance();
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

        if (controller instanceof MasterpassController) {
            return GateKeeper.Destination.MASTERPASS;
        }

        if (controller instanceof  AddContactController) {
            return GateKeeper.Destination.ADD_CONTACT;
        }

        if (controller instanceof  ViewVouchersController) {
            return GateKeeper.Destination.VIEW_VOUCHERS;
        }

        if (controller instanceof InviteSendController) {
            return GateKeeper.Destination.INVITE;
        }

        if (controller instanceof OurpaySMSVerificationController) {
            return GateKeeper.Destination.SMS_VERIFICATION;
        }

        if (controller instanceof LegalitiesController) {
            return GateKeeper.Destination.LEGALITIES;
	    }

        return GateKeeper.Destination.EMPTY;
    }
}
