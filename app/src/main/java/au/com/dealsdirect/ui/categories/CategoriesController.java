package au.com.dealsdirect.ui.categories;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController implements CategoriesMvpView {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

    }


    @Override protected void setUp(View view) {

    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }


}
