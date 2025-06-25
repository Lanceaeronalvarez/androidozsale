package au.com.dealsdirect.di.component;


import au.com.dealsdirect.di.PerController;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.afterpay.AfterpayViewController;
import au.com.dealsdirect.ui.controller.brands.TopBrandsController;
import au.com.dealsdirect.ui.controller.categories.NewCategoriesController;
import au.com.dealsdirect.ui.controller.categories.NewSaleCategoriesController;
import au.com.dealsdirect.ui.controller.categories.OldCategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.checkout.deliveryoptions.DeliveryOptionsController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.selectsubject.ContactSelectSubjectController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.floatingimageviewer.FloatingImageViewerController;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordController;
import au.com.dealsdirect.ui.controller.information.InformationMenuController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.klarna.KlarnaViewController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.login.PasswordVerificationController;
import au.com.dealsdirect.ui.controller.lpay.LPayViewController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.notification.NotificationController;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.register.RegisterController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnController;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsController;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersController;
import au.com.dealsdirect.ui.controller.returns.returnspolicy.ReturnsPolicyViewController;
import au.com.dealsdirect.ui.controller.salecategories.SaleCategoriesController;
import au.com.dealsdirect.ui.controller.salefilter.SaleFilterController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;
import au.com.dealsdirect.ui.controller.webviewcontroller.WebViewController;
import au.com.dealsdirect.ui.controller.zippay.ZipPayViewController;
import au.com.dealsdirect.ui.sample.SampleController;
import dagger.Component;

@PerController
@Component(dependencies = ActivityComponent.class, modules = ControllerModule.class)
public interface ControllerComponent {

    void inject(SampleController controller);

    void inject(OldCategoriesController controller);

    void inject(NewCategoriesController controller);

    void inject(ShopsController controller);

    void inject(TopBrandsController controller);

    void inject(MainController controller);

    void inject(AccountController controller);

    void inject(ViewContactsController controller);

    void inject(CheckoutController controller);

    void inject(SaleItemDetailsController controller);

    void inject(SaleCategoriesController controller);

    void inject(SaleItemsController controller);

    void inject(LoginController controller);

    void inject(ViewAddressController controller);

    void inject(AddNewAddressController controller);

    void inject(AddContactController controller);

    void inject(DetailsController controller);

    void inject(LanguageController controller);

    void inject(CountryController controller);

    void inject(OrdersController controller);

    void inject(OrderDetailsController controller);

    void inject(ViewContactHistoryController controller);

    void inject(ViewVouchersController controller);

    void inject(AddVouchersController controller);

    void inject(RegisterController controller);

    void inject(CurrentReturnsController controller);

    void inject(ReturnDetailsController controller);

    void inject(ReturnOrdersController controller);

    void inject(AddPaymentController controller);

    void inject(PaymentSelectController controller);

    void inject(PaymentSuccessController controller);

    void inject(InviteSendController controller);

    void inject(ContactSelectOrderController controller);

    void inject(ContactSelectSubjectController controller);

    void inject(ForgotPasswordController controller);

    void inject(LegalitiesController controller);

    void inject(NewReturnController controller);

    void inject(SearchFilterController controller);

    void inject(MasterpassController controller);

    void inject(BaseController controller);

    void inject(SplashScreenController controller);

    void inject(DeliveryOptionsController controller);

    void inject(PasswordVerificationController controller);

    void inject(CheckoutHostController controller);

    void inject(NotificationController controller);

    void inject(InformationMenuController controller);

    void inject(WebViewController controller);

    void inject(AfterpayViewController controller);

    void inject(LPayViewController controller);

    void inject(FloatingImageViewerController controller);

    void inject(ReturnsPolicyViewController controller);

    void inject(KlarnaViewController klarnaViewController);

    void inject(ZipPayViewController zipPayViewController);

    void inject(NewSaleCategoriesController controller);

    void inject(SaleFilterController controller);

}
