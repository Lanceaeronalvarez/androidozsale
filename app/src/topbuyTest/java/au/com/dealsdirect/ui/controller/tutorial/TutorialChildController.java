package au.com.dealsdirect.ui.controller.tutorial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by dp on 11/10/17.
 */

public class TutorialChildController extends BaseController {

    private static final String KEY_TUTORIAL = "ChildController.tutorial";
    private static final String KEY_BG_COLOR = "ChildController.bgColor";
    private static final String KEY_COLOR_IS_RES = "ChildController.colorIsResId";

    @BindView(R.id.controller_tutorial_image)
    ImageView mTutorialChildControllerImage;

    @BindView(R.id.controller_tutorial_child_container)
    RelativeLayout mTutorialChildControllerContainer;

    String tutorial;
    public TutorialChildController(Bundle args) {
        super(args);
    }

    public static TutorialChildController newInstance(String tutorial, int backgroundColor) {
        return new TutorialChildController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_TUTORIAL, tutorial)
                        .putInt(KEY_BG_COLOR, backgroundColor)
                        .build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_tutorial_child_screen, container, false);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        AppLogger.d("splash" + "setup");

        int backgroundColor = getArgs().getInt(KEY_BG_COLOR);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        switch (backgroundColor){
            case R.color.teal:
                mTutorialChildControllerImage.setImageDrawable(mActivity.getDrawable(R.drawable.tutorial_first_step));
                backgroundColor = ContextCompat.getColor(getActivity(), backgroundColor);
                mTutorialChildControllerContainer.setBackgroundColor(backgroundColor);
                break;
            case R.color.yellow:
                mTutorialChildControllerImage.setImageDrawable(mActivity.getDrawable(R.drawable.tutorial_second_step));
                backgroundColor = ContextCompat.getColor(getActivity(), backgroundColor);
                mTutorialChildControllerContainer.setBackgroundColor(backgroundColor);
                break;
            case R.color.red:
                mTutorialChildControllerImage.setImageDrawable(mActivity.getDrawable(R.drawable.tutorial_third_step));
                backgroundColor = ContextCompat.getColor(getActivity(), backgroundColor);
                mTutorialChildControllerContainer.setBackgroundColor(backgroundColor);
                break;
            case R.color.blue:
                mTutorialChildControllerImage.setImageDrawable(mActivity.getDrawable(R.drawable.tutorial_fourth_step));
                backgroundColor = ContextCompat.getColor(getActivity(), backgroundColor);
                mTutorialChildControllerContainer.setBackgroundColor(backgroundColor);
                break;
            default:
                mTutorialChildControllerImage.setImageDrawable(mActivity.getDrawable(R.drawable.tutorial_first_step));
                backgroundColor = ContextCompat.getColor(getActivity(), backgroundColor);
                mTutorialChildControllerContainer.setBackgroundColor(backgroundColor);
                break;

        }

    }
}
