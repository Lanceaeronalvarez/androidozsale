package au.com.dealsdirect.di.component;


import au.com.dealsdirect.di.PerController;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.ui.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.contact.ContactController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.productdetails.ProductDetailsController;
import au.com.dealsdirect.ui.controller.salecategories.SaleCategoriesController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.sample.SampleController;
import dagger.Component;

/*
 * Created by Ayi on 05/06/2017.
 */

@PerController
@Component(dependencies = ActivityComponent.class, modules = ControllerModule.class)
public interface ControllerComponent {

    void inject(SampleController controller);

    void inject(CategoriesController categoriesController);

    void inject(ShopsController shopsController);

    void inject(HomeController homeController);

    void inject(AccountController accountController);

    void inject(ContactController contactController);

    void inject(CheckoutController checkoutController);

    void inject(ProductDetailsController productDetailsController);
    
    void inject(SaleCategoriesController saleCategoriesController);
}
