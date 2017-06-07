package au.com.dealsdirect.di.module;

import com.bluelinelabs.conductor.Controller;

import au.com.dealsdirect.ui.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.categories.CategoriesMvpView;
import au.com.dealsdirect.ui.categories.CategoriesPresenter;
import au.com.dealsdirect.ui.controller.account.AccountMvpPresenter;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.account.AccountPresenter;
import au.com.dealsdirect.ui.controller.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.CheckoutPresenter;
import au.com.dealsdirect.ui.controller.contact.ContactMvpPresenter;
import au.com.dealsdirect.ui.controller.contact.ContactMvpView;
import au.com.dealsdirect.ui.controller.contact.ContactPresenter;
import au.com.dealsdirect.ui.controller.home.HomeMvpPresenter;
import au.com.dealsdirect.ui.controller.home.HomeMvpView;
import au.com.dealsdirect.ui.controller.home.HomePresenter;
import au.com.dealsdirect.ui.controller.invite.InviteMvpPresenter;
import au.com.dealsdirect.ui.controller.invite.InviteMvpView;
import au.com.dealsdirect.ui.controller.invite.InvitePresenter;
import au.com.dealsdirect.ui.controller.shop.ShopMvpPresenter;
import au.com.dealsdirect.ui.controller.shop.ShopMvpView;
import au.com.dealsdirect.ui.controller.shop.ShopPresenter;
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

    @Provides CategoriesMvpPresenter<CategoriesMvpView> provideCategoriesPresenter
            (CategoriesPresenter<CategoriesMvpView> presenter) {
        return presenter;
    }

    @Provides ShopMvpPresenter<ShopMvpView> provideShopPresenter
            (ShopPresenter<ShopMvpView> presenter) {
        return presenter;
    }

    @Provides HomeMvpPresenter<HomeMvpView> provideHomePresenter
            (HomePresenter<HomeMvpView> presenter) {
        return presenter;
    }

    @Provides AccountMvpPresenter<AccountMvpView> provideAccountPresenter
            (AccountPresenter<AccountMvpView> presenter) {
        return presenter;
    }

    @Provides ContactMvpPresenter<ContactMvpView> provideContactPresenter
            (ContactPresenter<ContactMvpView> presenter) {
        return presenter;
    }

    @Provides InviteMvpPresenter<InviteMvpView> provideInvitePresenter
            (InvitePresenter<InviteMvpView> presenter) {
        return presenter;
    }

    @Provides CheckoutMvpPresenter<CheckoutMvpView> provideCheckoutPresenter
            (CheckoutPresenter<CheckoutMvpView> presenter) {
        return presenter;
    }

}
