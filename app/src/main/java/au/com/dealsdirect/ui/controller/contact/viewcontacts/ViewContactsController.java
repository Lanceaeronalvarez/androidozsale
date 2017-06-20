package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.viewcontactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsController extends BaseController implements ViewContactsMvpView {

    public static final String TAG = "ContactController";
    private static final String KEY_TEXT = "ContactController.KEY_TEXT";

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mViewContactsToolarTitle;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mViewContactsToolbarRightOption;

    @Inject
    ViewContactsMvpPresenter<ViewContactsMvpView> mPresenter;

    public static ViewContactsController newInstance() {

        return new ViewContactsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ViewContactsController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_view_contacts, container, false);

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
        mPresenter.loadContacts();
        mViewContactsToolarTitle.setText("Contact Us");
        mViewContactsToolbarRightOption.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void onBackClick() {
        getActivity().onBackPressed();
    }


    @Override
    public void showContactItems(GetContactsResponse.Response myContacts) {

    }

    @OnClick(R.id.controller_view_contacts_add_button)
    void addContact(){
        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }
}
