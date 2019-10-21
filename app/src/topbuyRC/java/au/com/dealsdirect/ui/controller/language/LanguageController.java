package au.com.dealsdirect.ui.controller.language;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by Paul on 6/22/17.
 */

public class LanguageController extends SwipeableBaseToolBarController implements LanguageMvpView {

    @Inject
    LanguageMvpPresenter<LanguageMvpView> mPresenter;

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
    public void showLanguages(List<Language> languages, String selectedLanguage) {
        mAdapter.replaceData(languages, selectedLanguage);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLanguageLanguageDialog(String language) {
        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                language);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);
        fillContent(inflater.inflate(R.layout.controller_user_languages, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.getUserLanguages();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setupSwipingBehavior();
        mToolbarTitle.setText("language");
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mAdapter = new LanguageAdapter(new ArrayList<>(), mActivity, mPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.getUserLanguages();
    }

    @Override
    public void onBackPress() {
        mActivity.onBackPressed();
    }
}
