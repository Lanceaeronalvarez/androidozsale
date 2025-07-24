package au.com.dealsdirect.ui.controller.checkout.checkout.split;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.CheckoutStepsController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartreview.CheckoutStepsCartReviewController;
import butterknife.BindView;

public class CheckoutSplitController extends BaseController implements CheckoutSplitMvpView {

    public static final String TAG = "CheckoutSplitController";

    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mToolbarLeftView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightView;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.checkout_split_left_container)
    ViewGroup mLeftContainer;

    @BindView(R.id.checkout_split_right_container)
    ViewGroup mRightContainer;

    @Inject
    CheckoutSplitMvpPresenter<CheckoutSplitMvpView> mPresenter;

    private ViewGroup rootView = null;

    private Router mLeftRouter = null;
    private Router mRightRouter = null;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_split, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        rootView = (ViewGroup) view;

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
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        Controller controller = mLeftRouter.getControllerWithTag(CheckoutStepsController.TAG);
        if (controller instanceof CheckoutStepsController) {
            ((CheckoutStepsController) controller).refreshContents();
        }
    }

    @Override
    public Router getDetailRouter() {
        return mLeftRouter;
    }

    @Override
    protected void setUp(View view) {
        mToolbarLeftView.setVisibility(View.INVISIBLE);
        mToolbarRightView.setVisibility(View.INVISIBLE);
        mToolbarTitle.setText(mActivity.getResources().getText(R.string.checkout));

        mLeftRouter = getChildRouter(mLeftContainer);
        mRightRouter = getChildRouter(mRightContainer);

        final CheckoutStepsController stepsController = new CheckoutStepsController();
        stepsController.setShouldShowToolbar(false);
        stepsController.addCartListener(new CheckoutStepsController.CartListener() {
            @Override
            public void cartLoaded(CheckoutStepsController.Step step) {
                refreshCartReview(step.willReviewShowItems(), step.willReviewShowSummary());
            }

            @Override
            public void stepChanged(CheckoutStepsController.Step step) {
                refreshCartReview(step.willReviewShowItems(), step.willReviewShowSummary());
            }
        });
        mLeftRouter.setRoot(RouterTransaction.with(stepsController).tag(CheckoutStepsController.TAG));
        CommonControllerChangeListener.addToRouter(mLeftRouter);

        final CheckoutStepsCartReviewController cartReviewController = new CheckoutStepsCartReviewController();
        cartReviewController.setShouldShowToolbar(false);
        cartReviewController.setBottomPopupViewRoot(rootView);
        mRightRouter.setRoot(RouterTransaction.with(cartReviewController).tag(CheckoutStepsCartReviewController.TAG));
        CommonControllerChangeListener.addToRouter(mRightRouter);

        refreshCart(false);
        refreshCartReview(false, true);
    }

    public void refreshCart(boolean willForceRefresh) {
        Controller controller = mLeftRouter.getControllerWithTag(CheckoutStepsController.TAG);
        if (controller instanceof CheckoutStepsController) {
            ((CheckoutStepsController) controller).refreshCart(willForceRefresh);
        }
    }

    private void refreshCartReview(boolean showItems, boolean showSummary) {
        Controller controller = mRightRouter.getControllerWithTag(CheckoutStepsCartReviewController.TAG);
        if (controller instanceof CheckoutStepsCartReviewController) {
            CheckoutStepsCartReviewController cartReviewController = (CheckoutStepsCartReviewController) controller;
            cartReviewController.setShouldShowItems(showItems);
            cartReviewController.setShouldShowSummary(showSummary);
            cartReviewController.refreshCart();
        }
    }
}
