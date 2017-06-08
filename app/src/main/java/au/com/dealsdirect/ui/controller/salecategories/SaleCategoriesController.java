package au.com.dealsdirect.ui.controller.salecategories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.BannerResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleCategoriesController extends BaseController implements SaleCategoriesMvpView{

    private static final String KEY_TEXT = "SaleCategoriesController.KEY_TEXT";

    private static final String KEY_TITLE = "SaleCategoriesController.title";
    private static final String KEY_FROM_POSITION = "SaleCategoriesController.position";

    private String title;
    private int fromPosition;

    @BindView(R.id.controller_sale_categories_image)
    ImageView mSaleCategoriesImage;

    @Inject
    SaleCategoriesMvpPresenter<SaleCategoriesMvpView> mPresenter;

    public SaleCategoriesController(String title, int fromPosition) {

        this(new BundleBuilder(new Bundle())
                     .putString(KEY_TITLE, title)
                     .putInt(KEY_FROM_POSITION, fromPosition)
                     .build());
    }

    public SaleCategoriesController(Bundle args) {
        super(args);
        title = getArgs().getString(KEY_TITLE);
        fromPosition = getArgs().getInt(KEY_FROM_POSITION);

    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_sale_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mSaleCategoriesImage.setTransitionName(title+fromPosition);

    }

    @Override protected void setUp(View view) {

    }

    @Override public void showSaleCategories(BannerResponse bannerResponse) {

    }
}
