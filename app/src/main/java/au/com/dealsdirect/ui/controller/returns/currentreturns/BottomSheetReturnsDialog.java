package au.com.dealsdirect.ui.controller.returns.currentreturns;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.ActionConstants;

/**
 * Created by MTC on 2019-06-21.
 */
public class BottomSheetReturnsDialog extends BottomSheetDialogFragment {

    private ArrayList<String> arrayList = new ArrayList<>();
    private BottomSheetButtonListener listener;

    public BottomSheetReturnsDialog(@NonNull BottomSheetButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_returns_content, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.containsKey(ActionConstants.ORDER_ARRAYS)) {
                arrayList = bundle.getStringArrayList(ActionConstants.ORDER_ARRAYS);
            }
        }

        TextView mContactUsText = v.findViewById(R.id.bottom_need_help_text);
        mContactUsText.setOnClickListener(v6 -> {
            BottomSheetReturnsDialog.this.dismiss();
            listener.onNeedHelpPressed();
        });

        mContactUsText.setVisibility(View.VISIBLE);

        return v;
    }

    public interface BottomSheetButtonListener {
        void onNeedHelpPressed();
    }
}
