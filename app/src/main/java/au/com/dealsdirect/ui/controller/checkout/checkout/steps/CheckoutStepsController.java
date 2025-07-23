package au.com.dealsdirect.ui.controller.checkout.checkout.steps;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartreview.CheckoutStepsCartReviewController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.contact.CheckoutStepsContactController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.payment.CheckoutStepsPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.shipping.CheckoutStepsShippingController;
import au.com.dealsdirect.ui.controller.main.MainCustomViewPager;
import au.com.dealsdirect.ui.custom.transitions.ReverseVerticalChangeHandler;
import butterknife.BindView;
import butterknife.OnClick;

public class CheckoutStepsController extends BaseController implements CheckoutStepsMvpView {

    public final static String TAG = "CheckoutStepsController";

    public enum Step {
        CART(false, true),
        CONTACT(true, true),
        SHIPPING(true, true),
        PAYMENT(true, true);

        private static final int CART_INDEX = 0;
        private static final int CONTACT_INDEX = 1;
        private static final int SHIPPING_INDEX = 2;
        private static final int PAYMENT_INDEX = 3;
        private final boolean willReviewShowItems;
        private final boolean willReviewShowSummary;
        private static final Step[] ORDERED_LIST = {Step.CART, Step.CONTACT, Step.SHIPPING, Step.PAYMENT};

        Step(boolean willReviewShowItems, boolean willReviewShowSummary) {
            this.willReviewShowItems = willReviewShowItems;
            this.willReviewShowSummary = willReviewShowSummary;
        }

        public boolean willReviewShowItems() {
            return willReviewShowItems;
        }

        public boolean willReviewShowSummary() {
            return willReviewShowSummary;
        }

        public int getPosition() {
            for (int i = 0; i < ORDERED_LIST.length; i++) {
                if (ORDERED_LIST[i].equals(this)) {
                    return i;
                }
            }
            return -1;
        }

        public static Step[] getOrderedList() {
            return ORDERED_LIST;
        }
    }

    @Inject
    CheckoutStepsMvpPresenter<CheckoutStepsMvpView> mPresenter;

    @BindView(R.id.controller_checkout_steps_toolbar_container)
    ViewGroup mToolbarContainer;

    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mToolbarLeftView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightView;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.controller_checkout_steps_step_cart)
    ViewGroup mPageButtonCart;

    @BindView(R.id.controller_checkout_steps_step_contact)
    ViewGroup mPageButtonContact;

    @BindView(R.id.controller_checkout_steps_step_shipping)
    ViewGroup mPageButtonShipping;

    @BindView(R.id.controller_checkout_steps_step_payment)
    ViewGroup mPageButtonPayment;

    @BindView(R.id.controller_checkout_steps_pager_container)
    MainCustomViewPager mPagerContainer;

    @BindView(R.id.checkout_steps_step_circle_1)
    ImageView mStepCircleImageView1;
    @BindView(R.id.checkout_steps_step_circle_text_1)
    TextView mStepCircleTextView1;
    @BindView(R.id.checkout_steps_step_text_1)
    TextView mStepTextView1;

    @BindView(R.id.checkout_steps_step_circle_2)
    ImageView mStepCircleImageView2;
    @BindView(R.id.checkout_steps_step_circle_text_2)
    TextView mStepCircleTextView2;
    @BindView(R.id.checkout_steps_step_text_2)
    TextView mStepTextView2;

    @BindView(R.id.checkout_steps_step_circle_3)
    ImageView mStepCircleImageView3;
    @BindView(R.id.checkout_steps_step_circle_text_3)
    TextView mStepCircleTextView3;
    @BindView(R.id.checkout_steps_step_text_3)
    TextView mStepTextView3;

    @BindView(R.id.checkout_steps_step_circle_4)
    ImageView mStepCircleImageView4;
    @BindView(R.id.checkout_steps_step_circle_text_4)
    TextView mStepCircleTextView4;
    @BindView(R.id.checkout_steps_step_text_4)
    TextView mStepTextView4;

    private final List<ImageView> stepCircleImageViews = new ArrayList<>();
    private final List<TextView> stepCircleTextViews = new ArrayList<>();
    private final List<TextView> stepTextViews = new ArrayList<>();

    private String mPostcode = null;
    private String mPickupPoint = null;
    private List<CartListener> cartListeners = new ArrayList<>();
    private boolean shouldShowToolbar = true;
    private Step currentStep = Step.CART;
    private int previousPagerPosition = Step.CART_INDEX;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Router> routers = new HashMap<>();


    public boolean shouldShowToolbar() {
        return shouldShowToolbar;
    }

    public void setShouldShowToolbar(boolean shouldShowToolbar) {
        this.shouldShowToolbar = shouldShowToolbar;
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_steps, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        mPresenter.onDetach();
        stepCircleImageViews.clear();
        stepCircleTextViews.clear();
        stepTextViews.clear();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (mPresenter.checkIsLoggedIn()) {
            mPresenter.loadCart(mPostcode, mPickupPoint, false);
        } else {
            gotoCheckoutEmpty();
        }
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.loadCart(mPostcode, mPickupPoint, false);
    }

    @Override
    protected void setUp(View view) {
        mToolbarContainer.setVisibility(shouldShowToolbar ? View.VISIBLE : View.GONE);
        mToolbarRightView.setImageResource(R.drawable.ic_cart_placeholder);
        mToolbarRightView.setVisibility(View.VISIBLE);
        mToolbarTitle.setText(view.getResources().getText(R.string.checkout));
        mPageButtonCart.setOnClickListener(v -> mPagerContainer.setCurrentItem(Step.CART_INDEX, true));
        mPageButtonContact.setOnClickListener(v -> mPagerContainer.setCurrentItem(Step.CONTACT_INDEX, true));
        mPageButtonShipping.setOnClickListener(v -> mPagerContainer.setCurrentItem(Step.SHIPPING_INDEX, true));
        mPageButtonPayment.setOnClickListener(v -> mPagerContainer.setCurrentItem(Step.PAYMENT_INDEX, true));

        stepCircleImageViews.add(mStepCircleImageView1);
        stepCircleImageViews.add(mStepCircleImageView2);
        stepCircleImageViews.add(mStepCircleImageView3);
        stepCircleImageViews.add(mStepCircleImageView4);

        stepCircleTextViews.add(mStepCircleTextView1);
        stepCircleTextViews.add(mStepCircleTextView2);
        stepCircleTextViews.add(mStepCircleTextView3);
        stepCircleTextViews.add(mStepCircleTextView4);

        stepTextViews.add(mStepTextView1);
        stepTextViews.add(mStepTextView2);
        stepTextViews.add(mStepTextView3);
        stepTextViews.add(mStepTextView4);

        setupViewPager();
        stepChanged();
    }

    @Override
    public void showCart() {
        mActivity.getMainController().showCartItemsSize();
        if (mPresenter.getCart() == null ||
                mPresenter.getCart().getItems() == null ||
                mPresenter.getCart().getItems().isEmpty()) {
            gotoCheckoutEmpty();
            return;
        }

        for (CartListener cartListener : cartListeners) {
            cartListener.cartLoaded(currentStep);
        }
        refreshCurrentController(currentStep.getPosition());
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackPressed() {
        if (currentStep.getPosition() == 0) {
            mActivity.onBackPressed();
        } else if (currentStep.getPosition() > 0) {
            mPagerContainer.setCurrentItem(currentStep.getPosition() - 1);
        }
    }

    public void refreshCart(boolean willForceRefresh) {
        mPresenter.loadCart(mPostcode, mPickupPoint, willForceRefresh);
    }

    public void addCartListener(CartListener cartListener) {
        cartListeners.add(cartListener);
    }

    public void removeCartListener(CartListener cartListener) {
        cartListeners.remove(cartListener);
    }

    private void changeStep(Step step) {
        currentStep = step;
        stepChanged();
    }

    private void stepChanged() {
        for (CartListener cartListener : cartListeners) {
            cartListener.stepChanged(currentStep);
        }

        final int currentPosition = currentStep.getPosition();

        if (currentPosition == Step.CART_INDEX || currentPosition == Step.PAYMENT_INDEX) {
            mPresenter.loadCart(mPostcode, mPickupPoint, false);
        }

        mToolbarLeftView.setVisibility(currentPosition == 0 ? View.GONE : View.VISIBLE);
        mToolbarRightView.setVisibility(currentPosition == Step.CART_INDEX ? View.GONE : View.VISIBLE);

        refreshCurrentController(currentPosition);

        for (int i = 0; i < Step.ORDERED_LIST.length; i++) {
            if (i <= currentPosition) {
                stepCircleImageViews.get(i).setImageResource(R.drawable.checkout_step_circle_active);
                stepCircleTextViews.get(i).setTextColor(mActivity.getResources().getColor(R.color.white));
                stepTextViews.get(i).setTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
            } else {
                stepCircleImageViews.get(i).setImageResource(R.drawable.checkout_step_circle_inactive);
                stepCircleTextViews.get(i).setTextColor(mActivity.getResources().getColor(R.color.gray));
                stepTextViews.get(i).setTextColor(mActivity.getResources().getColor(R.color.gray));
            }
        }
    }

    private Controller getCurrentController(int stepIndex) {
        final Router router = routers.get(stepIndex);
        if (router == null) {
            return null;
        }
        final List<RouterTransaction> backstack = router.getBackstack();
        return backstack.get(backstack.size() - 1).controller();
    }

    private void refreshCurrentController(int stepIndex) {
        final Controller controller = getCurrentController(stepIndex);
        if (controller instanceof BaseController) {
            ((BaseController) controller).refreshContents();
        }
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void showCartReview() {
        final CheckoutStepsCartReviewController cartReviewController = new CheckoutStepsCartReviewController();
        cartReviewController.setShouldShowItems(currentStep.willReviewShowItems());
        cartReviewController.setShouldShowSummary(currentStep.willReviewShowSummary());
        cartReviewController.setWillShowLargeImages(false);
        getRouter().pushController(
                RouterTransaction.with(cartReviewController)
                        .tag(CheckoutStepsCartReviewController.TAG)
                        .pushChangeHandler(new ReverseVerticalChangeHandler())
                        .popChangeHandler(new ReverseVerticalChangeHandler()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager() {

        RouterPagerAdapter viewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                setupRouterAtPosition(router, position);
            }

            @Override
            public int getCount() {
                return Step.getOrderedList().length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };

        mPagerContainer.setAdapter(viewPagerAdapter);
        mPagerContainer.setMyScroller();

        mPagerContainer.setOffscreenPageLimit(Step.getOrderedList().length);

        mPagerContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == previousPagerPosition) {
                    return;
                }
                onPageSwitch(previousPagerPosition, false);
                onPageSwitch(position, true);
                previousPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mPagerContainer.setIsSwipeable(false);

        mPagerContainer.setCurrentItem(Step.CART.getPosition(), false);
    }

    private void onPageSwitch(int position, boolean isCurrent) {
        if (isCurrent) {
            changeStep(Step.getOrderedList()[position]);
        }
    }

    private void setupRouterAtPosition(Router router, int position) {
        routers.put(position, router);

        switch (position) {
            case Step.CART_INDEX:
                setupCartRouter(router);
                break;
            case Step.CONTACT_INDEX:
                setupContactRouter(router);
                break;
            case Step.SHIPPING_INDEX:
                setupShippingRouter(router);
                break;
            case Step.PAYMENT_INDEX:
                setupPaymentRouter(router);
                break;
            default:
                return;
        }

        CommonControllerChangeListener.addToRouter(router);
    }

    private void setupCartRouter(Router router) {
        if (router == null) {
            return;
        }

        CheckoutStepsCartReviewController controller = new CheckoutStepsCartReviewController();
        controller.setWillShowLargeImages(true);
        controller.setShouldShowToolbar(false);
        controller.setShouldShowSummary(!mPresenter.isTablet());

        router.setRoot(RouterTransaction.with(controller)
                .tag(CheckoutStepsCartReviewController.TAG)
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupContactRouter(Router router) {
        if (router == null) {
            return;
        }

        router.setRoot(RouterTransaction.with(new CheckoutStepsContactController())
                .tag(CheckoutStepsContactController.TAG)
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupShippingRouter(Router router) {
        if (router == null) {
            return;
        }

        router.setRoot(RouterTransaction.with(new CheckoutStepsShippingController())
                .tag(CheckoutStepsShippingController.TAG)
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupPaymentRouter(Router router) {
        if (router == null) {
            return;
        }

        router.setRoot(RouterTransaction.with(new CheckoutStepsPaymentController())
                .tag(CheckoutStepsPaymentController.TAG)
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler()));
    }

    private void gotoCheckoutEmpty() {
        mActivity.getMainController().resetCheckoutRouter();
    }

    public interface CartListener {
        void cartLoaded(Step step);

        void stepChanged(Step step);
    }
}
