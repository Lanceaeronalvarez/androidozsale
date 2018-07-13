package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.Personalisation;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.utils.RegexInputFilter;

/**
 * Created by Ayi on 5/29/18.
 */

public class PersonalisationLayout extends LinearLayout {

    private static final String ERROR_TEXT_TAG = "ERROR_TEXT_TAG";

    private static final String LABEL_TEXT_TAG = "LABEL_TEXT_TAG";

    private BaseActivity mBaseActivity;

    private Personalisation mPersonalisation;

    public PersonalisationLayout(Context context) {
        super(context);
    }

    public PersonalisationLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PersonalisationLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PersonalisationLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void inflateForCheckout(Context context, List<Personalisation.CustomizableItemDetails>
            customizableItemDetailsList) {

        removeAllViews();
        setOrientation(VERTICAL);
        setVisibility(View.GONE);

        if (!(context instanceof BaseActivity) || customizableItemDetailsList == null ||
                customizableItemDetailsList.isEmpty()) return;

        mBaseActivity = (BaseActivity) context;

        for (Personalisation.CustomizableItemDetails details : customizableItemDetailsList) {

            View row = mBaseActivity.getLayoutInflater().inflate(R.layout.personalisation_checkout_row, null);

            TextView keyTextView = (TextView) row.findViewById(R.id.personalisation_checkout_row_key);

            TextView valueTextView = (TextView) row.findViewById(R.id.personalisation_checkout_row_value);

            keyTextView.setText(details.getKey().concat(": "));

            valueTextView.setText(details.getValue());

            addView(row);
        }

        setVisibility(View.VISIBLE);
    }

    public void inflateForProductDetails(Context context, Personalisation personalisation) {

        setOrientation(VERTICAL);

        if (!(context instanceof BaseActivity)) return;

        mBaseActivity = (BaseActivity) context;

        mPersonalisation = personalisation;

        View header = mBaseActivity.getLayoutInflater().inflate(R.layout.personalisation_section_header, null);
        addView(header);

        LinkedHashMap<String, Personalisation.Property> propertyHashMap = mPersonalisation.getProperties();

        Collection<String> keys = propertyHashMap.keySet();

        // Using iterator instead of foreach avoids a ConcurrentModificationException
        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {

            String currentKey = iterator.next();

            Personalisation.Property property = propertyHashMap.get(currentKey);

            View row = mBaseActivity.getLayoutInflater().inflate(R.layout.personalisation_section_row, null);

            TextView rowLabel = (TextView) row.findViewById(R.id.personalisation_section_row_label);

            EditText rowEditText = (EditText) row.findViewById(R.id.personalisation_section_row_edit_text);

            TextView rowErrorText = (TextView) row.findViewById(R.id.personalisation_section_row_error);

            TextView rowSubText = (TextView) row.findViewById(R.id.personalisation_section_row_subtext);

            View border = row.findViewById(R.id.personalisation_section_row_border);

            boolean isRequiredField = mPersonalisation.getRequired().contains(currentKey);

            String labelText = isRequiredField ? property.getTitle().concat("*") : property.getTitle();

            // Set label text
            rowLabel.setText(labelText);

            rowLabel.setTag(currentKey.concat(LABEL_TEXT_TAG));

            boolean isOzsale = mBaseActivity.getResources().getBoolean(R.bool.is_ozsale_app);

            // Set edit text hint
            rowEditText.setHint(!isOzsale ? property.getWatermark() : mBaseActivity.getResources()
                    .getString(R.string.max_characters).concat(property.getMaxLength().toString()));

            // Set edit text max number of characters and pattern checking
            if (property.getPattern() == null || property.getPattern().equals("")) {
                rowEditText.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(property.getMaxLength())});
            } else {
                rowEditText.setFilters(new InputFilter[]{
                        new RegexInputFilter(property.getPattern()),
                        new InputFilter.LengthFilter(property.getMaxLength())});
            }

            // Set subtext to show max number of characters
            rowSubText.append(property.getMaxLength().toString());

            // For easier access to the child EditTexts
            rowEditText.setTag(currentKey);

            // Error text also needs easy access for hide / show on form validation
            rowErrorText.setTag(currentKey.concat(ERROR_TEXT_TAG));

            if (!iterator.hasNext()) {
                border.setVisibility(View.VISIBLE);

                if (isOzsale) {
                    View layoutFooter = row.findViewById(R.id.personalisation_section_footer);
                    View innerBorder = row.findViewById(R.id.personalisation_section_row_inner_border);
                    layoutFooter.setVisibility(View.VISIBLE);
                    innerBorder.setVisibility(View.GONE);

                    // For OZ, we specifically need to access the last error text
                    rowErrorText.setTag(ERROR_TEXT_TAG);
                }
            }

            addView(row);
        }

        setVisibility(View.VISIBLE);
    }

    public boolean verifyRequiredFields() {

        boolean verified = true;

        // If personalisation was not initialized, return success because no fields are required
        if (mPersonalisation == null) return true;

        // Else check required fields if they have input
        for (String currentKey : mPersonalisation.getRequired()) {

            boolean isOzsale = mBaseActivity.getResources().getBoolean(R.bool.is_ozsale_app);

            TextView rowLabel = (TextView) findViewWithTag(currentKey.concat(LABEL_TEXT_TAG));

            EditText rowEditText = (EditText) findViewWithTag(currentKey);

            TextView rowErrorText = (TextView) findViewWithTag(isOzsale ? ERROR_TEXT_TAG :
                    currentKey.concat(ERROR_TEXT_TAG));

            String inputText = rowEditText.getText().toString().trim();

            // If a required field was found empty, return verify failed
            if (inputText.isEmpty() || inputText.equals("")) {

                OnFocusChangeListener onFocusChangeListener;

                if (isOzsale) {

                    rowLabel.setTextColor(mBaseActivity.getResources().getColor(R.color.personalisation_error_color));

                    onFocusChangeListener = (v, hasFocus) -> {
                        if (hasFocus) {
                            rowErrorText.setVisibility(View.GONE);

                            rowLabel.setTextColor(mBaseActivity.getResources().getColor(R.color.text_medium));
                        }
                    };

                } else {

                    rowEditText.setBackgroundResource(R.drawable.personalisation_section_background_error);

                    onFocusChangeListener = (v, hasFocus) -> {
                        if (hasFocus) {
                            rowErrorText.setVisibility(View.GONE);

                            v.setBackgroundResource(R.drawable.personalisation_section_background);
                        }
                    };

                }

                rowEditText.setOnFocusChangeListener(onFocusChangeListener);

                rowErrorText.setVisibility(View.VISIBLE);

                // Do not return false right away so that all fields will be checked if an error needs to be shown
                verified = false;
            }
        }

        // If all required fields were passed and found not empty, return verify success
        return verified;
    }

    public LinkedHashMap<String, String> getDataForAddToCart() {

        LinkedHashMap<String, String> personalizationData = new LinkedHashMap<>();

        // If personalisation was not initialized, return null because no fields are present
        if (mPersonalisation == null) return null;

        LinkedHashMap<String, Personalisation.Property> propertyHashMap = mPersonalisation.getProperties();

        Collection<String> keys = propertyHashMap.keySet();

        // Using iterator instead of foreach avoids a ConcurrentModificationException
        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {

            String currentKey = iterator.next();

            EditText rowEditText = (EditText) findViewWithTag(currentKey);

            String inputText = rowEditText != null ? rowEditText.getText().toString().trim() : "";

            personalizationData.put(currentKey, inputText);
        }

        return personalizationData;
    }
}
