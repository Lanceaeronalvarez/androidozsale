package au.com.dealsdirect.ui.controller.language;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by Paul on 6/22/17.
 */

public class LanguageController extends BaseController implements LanguageMvpView {

    @Inject
    LanguageMvpPresenter<LanguageMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mFilterView;

    @BindView(R.id.partial_toolbar_arrow_view)
    ImageView mArrowImage;

    @BindView(R.id.controller_recycler_details)
    RecyclerView mRecyclerView;

    private LanguageAdapter mAdapter;

    public static LanguageController newInstance() {
        return new LanguageController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public LanguageController(Bundle args) {
        super(args);
    }

    @Override
    public void showLanguages(List<Language> languages) {
        mAdapter.replaceData(languages);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLanguageLanguageDialog(String language) {
        CustomAlertDialog.showCustomAlertDialog(getActivity(),
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                language);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_user_languages, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText("Language");
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setOnClickListener(v -> {
            onBackPress();
        });

        mAdapter = new LanguageAdapter(new ArrayList<>(), getActivity(), mPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.getUserLanguages();
    }

    @Override
    public void onBackPress() {
        getActivity().onBackPressed();
    }
}
