package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartreview;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class CheckoutStepsCartReviewPresenter<V extends CheckoutStepsCartReviewMvpView> extends BasePresenter<V> implements CheckoutStepsCartReviewMvpPresenter<V> {

    @Inject
    public CheckoutStepsCartReviewPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
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
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getDataManager().saveCart(new CartDetailsMapper(responseValue));
                        getMvpView().refreshCart();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                })
        );
    }

    @Override
    public boolean isShippingByPostcodeEnabled() {
        return getDataManager().getShippingByPostcodeEnabled();
    }

    @Override
    public TemplateTextsHelper.TemplateTextsRepository getTemplateTextsRepository() {
        return getDataManager().getTemplateTextsRepository();
    }
}
