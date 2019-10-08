package au.com.dealsdirect.ui.controller.language;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguagesViewHolder> {

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
    public LanguageAdapter.LanguagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_language, parent, false);
        LanguagesViewHolder vh = new LanguagesViewHolder(view, mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(LanguageAdapter.LanguagesViewHolder holder, int position) {
        boolean isSelected = mSelectedLanguage.equals(mLanguages.get(position).getID()) || mSelectedLanguage.equals(mLanguages.get(position).getID());
        holder.mLanguageTextView.setTextColor(context.getResources().getColor(isSelected ? R.color.language_selected : R.color.text_medium));
        holder.mLanguageCheckIcon.setVisibility(isSelected && context.getResources().getBoolean(R.bool.language_check_enabled) ? View.VISIBLE : View.GONE);

        holder.mLanguageTextView.setText(mLanguages.get(position).getName());

        holder.itemView.setOnClickListener(v -> {
            notifyDataSetChanged();
            mPresenter.onLanguageItemClick(mLanguages.get(position));
            holder.mLanguageTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        });
    }

    @Override
    public int getItemCount() {
        if (mLanguages != null)
            return mLanguages.size();
        return 0;
    }

    public void replaceData(List<Language> languages, String selectedLanguage) {
        mLanguages = languages;
        mSelectedLanguage = selectedLanguage;
    }

    static class LanguagesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_check_icon)
        ImageView mLanguageCheckIcon;

        @BindView(R.id.row_text_language)
        TextView mLanguageTextView;

        LanguageMvpPresenter mPresenter;

        public LanguagesViewHolder(View itemView, LanguageMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);
        }
    }
}
