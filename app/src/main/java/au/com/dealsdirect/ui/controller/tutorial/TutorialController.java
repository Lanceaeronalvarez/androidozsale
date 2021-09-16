package au.com.dealsdirect.ui.controller.tutorial;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;

/**
 * Created by smartwave on 04/12/2017.
 */

public class TutorialController extends BaseController {

    public TutorialController(Bundle args) {
        super(args);
    }

    public static TutorialController newInstance() {
        return new TutorialController(new BundleBuilder(new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
    }

    @Override
    protected void setUp(View view) {

    }
}
