package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartitems;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CheckoutStepsCartItemsPresenter<V extends CheckoutStepsCartItemsMvpView> extends BasePresenter<V> implements CheckoutStepsCartItemsMvpPresenter<V> {

    @Inject
    public CheckoutStepsCartItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public CartDetailsMapper getCart() {
        return getDataManager().getCart();
    }

    @Override
    public void fetchAdjustItemQuantity(String url, String itemID, String postcode) {
        getCompositeDisposable().add(getDataManager()
                .callAdjustQuantityOrderItem(url, new AdjustOrderItem.RequestValue(itemID, postcode, null, getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getDataManager().saveCart(new CartDetailsMapper(responseValue));
                    getMvpView().refreshCart();
                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                })
        );
    }

    @Override
    public boolean isShippingByPostcodeEnabled() {
        return getDataManager().getShippingByPostcodeEnabled();
    }

    @Override
    public String getImpossibleToDeliverAtLocationText() {
        if (getDataManager().getTemplateTextsRepository() == null) {
            return null;
        }
        return getDataManager().getTemplateTextsRepository().getImpossibleToDeliverAtLocation();
    }

    @Override
    public String getUnavailableText() {
        if (getDataManager().getTemplateTextsRepository() == null) {
            return null;
        }
        return getDataManager().getTemplateTextsRepository().getUnavailable();
    }
}
