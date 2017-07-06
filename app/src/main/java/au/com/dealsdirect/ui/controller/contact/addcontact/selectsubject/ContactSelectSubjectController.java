package au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.addcontact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.adapter.ContactSubjectAdapter;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.listener.ContactSubjectClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectSubjectController extends BaseController
        implements ContactSelectSubjectMvpView, ContactSubjectClickListener {

    public static final String TAG = "ContactSelectSubjectController";
    private static final String KEY_TEXT = "ContactSelectSubjectController.KEY_TEXT";
    private static final String KEY_SUBJECTS = "ContactSelectSubjectController.KEY_SUBJECTS";

    List<String> myContactSubjects;
    private ContactSubjectClickListener mContactSubjectItemListener;


    @Inject
    ContactSelectSubjectMvpPresenter<ContactSelectSubjectMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mContactSelectSubjectControllerTitle;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mContactSelectSubjectControllerFilter;


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
        View view = inflater.inflate(R.layout.controller_contact_select_subject, container, false);

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

        mContactSelectSubjectControllerTitle.setText("Message Subject");

        mContactSubjectItemListener = this;
        mContactSelectSubjectControllerRecyclerView =
                (RecyclerView) view.findViewById(R.id.my_contact_select_subject_recycler_view);

        final ContactSubjectAdapter adapter
                = new ContactSubjectAdapter
                (myContactSubjects,getActivity(), mContactSubjectItemListener);

        mContactSelectSubjectControllerRecyclerView.setAdapter(adapter);
        mContactSelectSubjectControllerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContactSelectSubjectControllerFilter.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        myContactSubjects = null;
        super.onDestroyView(view);
    }

    @Override
    public void onContactSubjectItemClicked(String contactSubject) {
        ContactPreferenceHelper.setChosenSubjectString(
                getActivity(),
                contactSubject);

        getActivity().onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClicked(){
        getActivity().onBackPressed();
    }
}
