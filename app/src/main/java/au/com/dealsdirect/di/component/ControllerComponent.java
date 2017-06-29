package au.com.dealsdirect.di.component;


import au.com.dealsdirect.di.PerController;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.cart.CartController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
//import au.com.dealsdirect.ui.controller.contact.ContactController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.register.RegisterController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.salecategories.SaleCategoriesController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;
import au.com.dealsdirect.ui.sample.SampleController;
import dagger.Component;

/*
 * Created by Ayi on 05/06/2017.
 */

@PerController
@Component(dependencies = ActivityComponent.class, modules = ControllerModule.class)
public interface ControllerComponent {

    void inject(SampleController controller);

    void inject(CategoriesController controller);

    void inject(ShopsController controller);

    void inject(HomeController controller);

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

    void inject(OrdersController controller);
    
    void inject(OrderDetailsController controller);

    void inject(ViewContactHistoryController controller);

    void inject(CartController controller);

    void inject(ViewVouchersController controller);

    void inject(RegisterController controller);

    void inject(AddPaymentController controller);

}
