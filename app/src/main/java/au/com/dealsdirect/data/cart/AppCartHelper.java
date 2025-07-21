package au.com.dealsdirect.data.cart;

import javax.inject.Inject;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

public class AppCartHelper implements CartHelper {

    private CartDetailsMapper currentCart = null;
    private PaymentMethod currentPaymentMethod = null;

    @Inject
    public AppCartHelper() {
    }

    @Override
    public CartDetailsMapper getCart() {
        return currentCart;
    }

    @Override
    public void saveCart(CartDetailsMapper cart) {
        currentCart = cart;
    }

    @Override
    public PaymentMethod getSelectedPaymentMethod() {
        return currentPaymentMethod;
    }

    @Override
    public void setSelectedPaymentMethod(PaymentMethod paymentMethod) {
        currentPaymentMethod = paymentMethod;
    }
}
