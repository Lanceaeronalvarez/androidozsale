package au.com.dealsdirect.di.module;

import com.bluelinelabs.conductor.Controller;

import au.com.dealsdirect.ui.controller.account.AccountMvpPresenter;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.account.AccountPresenter;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressMvpPresenter;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressMvpView;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressPresenter;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressMvpPresenter;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressMvpView;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressPresenter;
import au.com.dealsdirect.ui.controller.cart.CartMvpPresenter;
import au.com.dealsdirect.ui.controller.cart.CartMvpView;
import au.com.dealsdirect.ui.controller.cart.CartPresenter;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
import au.com.dealsdirect.ui.controller.categories.CategoriesPresenter;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentMvpView;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutPresenter;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectMvpView;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectPresenter;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactMvpPresenter;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactMvpView;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactpresenter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpPresenter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsPresenter;
import au.com.dealsdirect.ui.controller.details.DetailsMvpPresenter;
import au.com.dealsdirect.ui.controller.details.DetailsMvpView;
import au.com.dealsdirect.ui.controller.details.DetailsPresenter;
import au.com.dealsdirect.ui.controller.home.HomeMvpPresenter;
import au.com.dealsdirect.ui.controller.home.HomeMvpView;
import au.com.dealsdirect.ui.controller.home.HomePresenter;
import au.com.dealsdirect.ui.controller.invite.InviteMvpPresenter;
import au.com.dealsdirect.ui.controller.invite.InviteMvpView;
import au.com.dealsdirect.ui.controller.invite.InvitePresenter;
import au.com.dealsdirect.ui.controller.language.LanguageMvpPresenter;
import au.com.dealsdirect.ui.controller.language.LanguageMvpView;
import au.com.dealsdirect.ui.controller.language.LanguagePresenter;
import au.com.dealsdirect.ui.controller.login.LoginMvpPresenter;
import au.com.dealsdirect.ui.controller.login.LoginMvpView;
import au.com.dealsdirect.ui.controller.login.LoginPresenter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsMvpView;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsPresenter;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsMvpPresenter;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsMvpView;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsPresenter;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersMvpPresenter;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersMvpView;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersPresenter;
import au.com.dealsdirect.ui.controller.register.RegisterMvpPresenter;
import au.com.dealsdirect.ui.controller.register.RegisterMvpView;
import au.com.dealsdirect.ui.controller.register.RegisterPresenter;
import au.com.dealsdirect.ui.controller.salecategories.SaleCategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.salecategories.SaleCategoriesMvpView;
import au.com.dealsdirect.ui.controller.salecategories.SaleCategoriesPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsPresenter;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpView;
import au.com.dealsdirect.ui.controller.shops.ShopsPresenter;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersMvpPresenter;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersMvpView;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersPresenter;
import au.com.dealsdirect.ui.sample.SampleMvpPresenter;
import au.com.dealsdirect.ui.sample.SampleMvpView;
import au.com.dealsdirect.ui.sample.SamplePresenter;
import dagger.Module;
import dagger.Provides;

/*
 * Created by Ayi on 05/06/2017.
 */

@Module
public class ControllerModule {

    private Controller mController;

    public ControllerModule(Controller controller) {

        this.mController = controller;
    }

    @Provides
    SampleMvpPresenter<SampleMvpView> provideSamplePresenter(SamplePresenter<SampleMvpView> presenter) {
        return presenter;
    }

    @Provides
    CategoriesMvpPresenter<CategoriesMvpView> provideCategoriesPresenter(CategoriesPresenter<CategoriesMvpView> presenter) {
        return presenter;
    }

    @Provides
    ShopsMvpPresenter<ShopsMvpView> provideShopPresenter(ShopsPresenter<ShopsMvpView> presenter) {
        return presenter;
    }

    @Provides
    HomeMvpPresenter<HomeMvpView> provideHomePresenter(HomePresenter<HomeMvpView> presenter) {
        return presenter;
    }

    @Provides
    AccountMvpPresenter<AccountMvpView> provideAccountPresenter(AccountPresenter<AccountMvpView> presenter) {
        return presenter;
    }

    @Provides
    ViewContactsMvpPresenter<ViewContactsMvpView> provideContactPresenter(ViewContactsPresenter<ViewContactsMvpView> presenter) {
        return presenter;
    }

    @Provides
    InviteMvpPresenter<InviteMvpView> provideInvitePresenter(InvitePresenter<InviteMvpView> presenter) {
        return presenter;
    }

    @Provides
    CheckoutMvpPresenter<CheckoutMvpView> provideCheckoutPresenter(CheckoutPresenter<CheckoutMvpView> presenter) {
        return presenter;
    }

    @Provides
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> provideProductDetailsPresenter(SaleItemDetailsPresenter<SaleItemDetailsMvpView> presenter) {
        return presenter;
    }

    @Provides
    SaleCategoriesMvpPresenter<SaleCategoriesMvpView> provideSaleCategoriesPresenter(SaleCategoriesPresenter<SaleCategoriesMvpView> presenter) {
        return presenter;
    }

    @Provides
    SaleItemsMvpPresenter<SaleItemsMvpView> provideSaleItemsPresenter(SaleItemsPresenter<SaleItemsMvpView> presenter) {
        return presenter;
    }

    @Provides
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    DetailsMvpPresenter<DetailsMvpView> provideDetailsPresenter(DetailsPresenter<DetailsMvpView> presenter) {
        return presenter;
    }

    @Provides
    AddContactMvpPresenter<AddContactMvpView> provideAddContactPresenter(AddContactpresenter<AddContactMvpView> presenter){
        return presenter;
    }

    @Provides
    ViewAddressMvpPresenter<ViewAddressMvpView> provideViewAddressPresenter(ViewAddressPresenter<ViewAddressMvpView> presenter){
        return presenter;
    }

    @Provides
    AddNewAddressMvpPresenter<AddNewAddressMvpView> provideAddNewAddressPresenter(AddNewAddressPresenter<AddNewAddressMvpView> presenter){
        return presenter;
    }

    @Provides
    LanguageMvpPresenter<LanguageMvpView> provideLanguagePresenter(LanguagePresenter<LanguageMvpView> presenter) {
        return  presenter;
    }

    @Provides
    OrdersMvpPresenter<OrdersMvpView> provideOrdersPresenter(OrdersPresenter<OrdersMvpView> presenter){
        return presenter;
    }

    @Provides
    OrderDetailsMvpPresenter<OrderDetailsMvpView> provideOrderDetailPresenter(OrderDetailsPresenter<OrderDetailsMvpView> presenter){
        return presenter;
    }

    @Provides
    CartMvpPresenter<CartMvpView> provideCartPresenter(CartPresenter<CartMvpView> presenter) {
        return presenter;
    }

    @Provides
    ViewVouchersMvpPresenter<ViewVouchersMvpView> provideVouchersPresnter(ViewVouchersPresenter<ViewVouchersMvpView> presenter) {
        return presenter;
    }


    @Provides
    RegisterMvpPresenter<RegisterMvpView> provideRegisterPresenter(RegisterPresenter<RegisterMvpView> presenter) {
        return presenter;
    }

    @Provides
    AddPaymentMvpPresenter<AddPaymentMvpView> provideAddPaymentPresenter(AddPaymentPresenter<AddPaymentMvpView> presenter){
        return presenter;
    }

    @Provides
    PaymentSelectMvpPresenter<PaymentSelectMvpView> providePaymentSelectPresenter(PaymentSelectPresenter<PaymentSelectMvpView> presenter){
        return presenter;
    }

}
