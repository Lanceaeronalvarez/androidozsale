package au.com.dealsdirect.ui.controller.searchfilter.facetfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;

/**
 * Created by smartwave on 04/12/2017.
 */

public class FacetFilterController extends BaseController {

    public static FacetFilterController newInstance() {
        return new FacetFilterController(new BundleBuilder(new Bundle())
                .build());
    }

    public FacetFilterController(Bundle args) {
        super(args);
    }
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
    }

    @Override
    protected void setUp(View view) {

    }
}
