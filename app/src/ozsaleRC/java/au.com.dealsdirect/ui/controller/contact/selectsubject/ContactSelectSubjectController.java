package au.com.dealsdirect.ui.controller.contact.selectsubject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.selectsubject.adapter.ContactSubjectAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectSubjectController extends BaseController
        implements ContactSelectSubjectMvpView {

    public static final String TAG = "ContactSelectSubjectController";
    private static final String KEY_TEXT = "ContactSelectSubjectController.KEY_TEXT";
    private static final String KEY_SUBJECTS = "ContactSelectSubjectController.KEY_SUBJECTS";

    private List<String> myContactSubjects;
    private ContactSubjectAdapter mAdapter;


    @Inject
    ContactSelectSubjectMvpPresenter<ContactSelectSubjectMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mViewContactsToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mViewContactsToolbarRightOption;

    @BindView(R.id.my_contact_select_subject_recycler_view)
    RecyclerView mContactSelectSubjectControllerRecyclerView;

    public static ContactSelectSubjectController newInstance() {
        return new ContactSelectSubjectController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ContactSelectSubjectController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_contact_select_subject, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mPresenter.loadContactUsSubjects();
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mViewContactsToolarTitle.setText(getResource().getString(R.string.select_a_subject));
        mViewContactsToolbarRightOption.setVisibility(View.INVISIBLE);

        mAdapter = new ContactSubjectAdapter(myContactSubjects, mPresenter);

        mContactSelectSubjectControllerRecyclerView.setAdapter(mAdapter);
        mContactSelectSubjectControllerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void onContactSubjectItemSelected(String contactSubject) {

        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.CONTACT_SUBJECT, contactSubject);

        GateKeeper.push(getRouter(), GateKeeper.Destination.ADD_CONTACT, bundle,
                new HorizontalChangeHandler(), new HorizontalChangeHandler());
    }

    @Override
    public void showContactSubjects(List<String> contactSubjectList) {
        mAdapter.replaceData(contactSubjectList);
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        mActivity.onBackPressed();
    }
}
