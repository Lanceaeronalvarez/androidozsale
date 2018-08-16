package au.com.dealsdirect.ui.controller.contact.selectsubject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.addcontact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectsubject.adapter.ContactSubjectAdapter;
import au.com.dealsdirect.ui.controller.contact.selectsubject.listener.ContactSubjectClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectSubjectController extends SwipeableBaseToolBarController
        implements ContactSelectSubjectMvpView, ContactSubjectClickListener {

    public static final String TAG = "ContactSelectSubjectController";
    private static final String KEY_TEXT = "ContactSelectSubjectController.KEY_TEXT";
    private static final String KEY_SUBJECTS = "ContactSelectSubjectController.KEY_SUBJECTS";

    List<String> myContactSubjects;
    private ContactSubjectClickListener mContactSubjectItemListener;


    @Inject
    ContactSelectSubjectMvpPresenter<ContactSelectSubjectMvpView> mPresenter;

    @BindView(R.id.my_contact_select_subject_recycler_view)
    RecyclerView mContactSelectSubjectControllerRecyclerView;

    public static ContactSelectSubjectController newInstance(List<String> contactSubjects) {
        Log.d("clickable", "clicked subject "+contactSubjects.size());

//        myContactSubjects = contactSubjects;
        return new ContactSelectSubjectController(
                new BundleBuilder(new Bundle())
                        .putStringArrayList(KEY_SUBJECTS, (ArrayList<String>) contactSubjects)
                        .build());
    }

    public static ContactSelectSubjectController newInstance() {

//        myContactSubjects = contactSubjects;
        return new ContactSelectSubjectController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ContactSelectSubjectController(Bundle args) {
        super(args);

        if (!args.isEmpty()){
            if (args.containsKey(KEY_SUBJECTS)){
                myContactSubjects = args.getStringArrayList(KEY_SUBJECTS);

            }
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_contact_select_subject, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mToolbarTitle.setText("message subject");
        setupSwipingBehavior();
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mContactSubjectItemListener = this;

        final ContactSubjectAdapter adapter
                = new ContactSubjectAdapter
                (myContactSubjects,mActivity, mContactSubjectItemListener);

        mContactSelectSubjectControllerRecyclerView.setAdapter(adapter);
        mContactSelectSubjectControllerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        myContactSubjects = null;
        super.onDestroyView(view);
    }

    @Override
    public void onContactSubjectItemClicked(String contactSubject) {
        ContactPreferenceHelper.setChosenSubjectString(mActivity, contactSubject);
        getRouter().popCurrentController();
    }
}
