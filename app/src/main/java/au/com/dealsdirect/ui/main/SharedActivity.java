package au.com.dealsdirect.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import butterknife.BindView;
import butterknife.ButterKnife;

import static au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController.RESULT_EXTRA_CONTROLLER_ID;

/**
 * dp Created by Admin on 7/12/17.
 */

public class SharedActivity extends BaseActivity {

    public static final String TAG = "SharedActivity";
    private Router mSharedRouter;
    private Controller currentController;

    @BindView(R.id.activity_shared_frame)
    ViewGroup mContainer;


    @Override
    public void onBackPressed() {

        ((SaleItemDetailsController)currentController).readyViewsForTransition();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            final Intent resultData = new Intent();
            resultData.putExtra(RESULT_EXTRA_CONTROLLER_ID, getTaskId());
            setResult(RESULT_OK, resultData);
            finishAfterTransition();
        },200);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_shared);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mSharedRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        if (!mSharedRouter.hasRootController()) {
            Log.d("itemDetails", "it's in");
            currentController = SaleItemDetailsController.newInstance(bundle);
            mSharedRouter.setRoot(RouterTransaction.with(currentController)
                    .tag("ItemDetails"));
        }

        setUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivityFromTransitionManager(this);
    }

    @Override
    protected void setUp() {

    }

    private static void removeActivityFromTransitionManager(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        Class transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            if (map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void showLoginController(Router router, AuthHandler handler) {
        //pinapasa yung router, para kahit child router man siya ng kung ano mang view, pwedeng siya ang tumawag.
        router.pushController(RouterTransaction.with(LoginController.newInstance(handler))
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }
}
