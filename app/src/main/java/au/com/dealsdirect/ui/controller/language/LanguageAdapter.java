package au.com.dealsdirect.ui.controller.language;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul on 6/22/17.
 */

public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Language> mLanguages;
    private String mSelectedLanguage;
    private LanguageMvpPresenter mPresenter;
    private Context context;

    public LanguageAdapter(ArrayList<Language> languages, Context context, LanguageMvpPresenter presenter) {
        this.mLanguages = languages;
        this.context = context;
        this.mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_language, parent, false);
        LanguagesViewHolder vh = new LanguagesViewHolder(view, mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mSelectedLanguage.equals(mLanguages.get(position).getID()) || mSelectedLanguage == mLanguages.get(position).getID()) {
            ((LanguagesViewHolder) holder).mLanguageText.setTextColor(context.getResources().getColor(R.color.colorAccent));
            if (((LanguagesViewHolder) holder).mLanguageCheckIcon!=null) ((LanguagesViewHolder) holder).mLanguageCheckIcon.setVisibility(View.VISIBLE);
        }

        ((LanguagesViewHolder) holder).mLanguageText.setText(mLanguages.get(position).getName());

        ((LanguagesViewHolder) holder).itemView.setOnClickListener(v ->{
            notifyDataSetChanged();
            mPresenter.onLanguageItemClick(mLanguages.get(position));
            ((LanguagesViewHolder) holder).mLanguageText.setTextColor(context.getResources().getColor(R.color.colorAccent));
        });
    }

    @Override
    public int getItemCount() {
        if (mLanguages!=null)
            return mLanguages.size();
        return 0;
    }

    public void replaceData(List<Language> languages, String selectedLanguage){
        mLanguages = languages;
        mSelectedLanguage = selectedLanguage;
    }

    static class LanguagesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_check_icon)
        ImageView mLanguageCheckIcon;

        @BindView(R.id.row_text_language)
        TextView mLanguageText;

        LanguageMvpPresenter mPresenter;

        public LanguagesViewHolder(View itemView, LanguageMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);
        }
    }
}
