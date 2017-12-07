package au.com.dealsdirect.ui.controller.tutorial;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by dp on 11/10/17.
 */

public class TutorialController extends BaseController {

    private int[] PAGE_COLORS = new int[]{R.color.teal, R.color.yellow, R.color.red, R.color.blue};

    @BindView(R.id.controller_tutorial_view_pager)
    ViewPager tutorialViewPager;

    @BindView(R.id.controller_tutorial_get_started_button)
    Button tutorialButton;

    private final RouterPagerAdapter pagerAdapter;


    public TutorialController(Bundle args) {
        super(args);

        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    router.setRoot(RouterTransaction.with(TutorialChildController.newInstance("test",PAGE_COLORS[position])));
                }
            }

            @Override
            public int getCount() {
                return PAGE_COLORS.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };
    }

    public static TutorialController newInstance() {
        return new TutorialController(new BundleBuilder(new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_tutorial_screen, container, false);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.activered));
        }

        CircleIndicator indicator = view.findViewById(R.id.controller_tutorial_indicator);

        tutorialViewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(tutorialViewPager);
        pagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());

        tutorialViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TutorialScreen", "page selected = "+position);
                changeFragmentThemeColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        if (!getActivity().isChangingConfigurations()) {
            tutorialViewPager.setAdapter(null);
        }
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        AppLogger.d("splash" + "setup");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkteal));

        }

    }


    private void changeFragmentThemeColor(int position){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            switch (position){
                case 0:
                    window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkteal));
                    tutorialButton.setTextColor(getActivity().getResources().getColor(R.color.teal));
                    break;
                case 1:
                    window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkyellow));
                    tutorialButton.setTextColor(getActivity().getResources().getColor(R.color.yellow));
                    break;
                case 2:
                    window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkred));
                    tutorialButton.setTextColor(getActivity().getResources().getColor(R.color.red));
                    break;
                case 3:
                    window.setStatusBarColor(getActivity().getResources().getColor(R.color.blue));
                    tutorialButton.setTextColor(getActivity().getResources().getColor(R.color.blue));
                    break;
                default:
                    window.setStatusBarColor(getActivity().getResources().getColor(R.color.blue));
                    tutorialButton.setTextColor(getActivity().getResources().getColor(R.color.blue));

                    break;
            }
        }

    }

    @OnClick(R.id.controller_tutorial_get_started_button)
    void onGetStartedButtonClick(){

        if (getActivity() != null) ((MainActivity) getActivity()).splashShownCallback();
    }

}
