package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseController implements AddContactMvpView {

    public static final String TAG = "AddContactController";

    private static final String KEY_TEXT = "AddContactController.KEY_TEXT";

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mAddContactToolbarRightOption;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mAddContactToolbarTitle;

    @Inject
    AddContactMvpPresenter<AddContactMvpView> mPresenter;

    public static AddContactController newInstance() {

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public AddContactController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_contact, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        //mPresenter.loadSample(new SampleRequest());
        mAddContactToolbarRightOption.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_check));
        mAddContactToolbarTitle.setText("Create Contact");
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }


    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBack(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_filter_view)
    void onCreate(){

        getRouter().setRoot(RouterTransaction.with(AccountController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));


    }
}
