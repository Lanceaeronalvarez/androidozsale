package au.com.dealsdirect.ui.controller.shops;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;

/**
 * Created by MTC on 2020-03-31.
 */
public class BottomSheetInfoDialog extends BottomSheetDialogFragment {

    private String title = "";
    private String description = "";

    private int layoutId = R.layout.bottom_sheet_info;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);

        TextView textViewDescription = v.findViewById(R.id.bottom_sheet_info_description);
        TextView textViewTitle = v.findViewById(R.id.bottom_sheet_info_title);

        textViewDescription.setText(getDescription());
        textViewTitle.setText(getTitle());

        return v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
