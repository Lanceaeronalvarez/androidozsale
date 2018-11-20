package au.com.dealsdirect.ui.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.Personalisation;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.RegexInputFilter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ayi on 5/29/18.
 */

public class PersonalisationLayout extends LinearLayout {

    // Personalisation Schema Update
    private static final String DATE_PICKER = "date_picker";
    private static final String YEAR_PICKER = "year_picker";
    private static final String ICONS_LIST = "picture_list";
    private static final String TEXT_LIST = "text_list";
    private static final String FILE_PICKER = "file_picker";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 9999;

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

        LinkedList<Personalisation.Property> list = new LinkedList<Personalisation.Property>();
        while (iterator.hasNext()) {
            String currentKey = iterator.next();
            Personalisation.Property property = propertyHashMap.get(currentKey);
            property.setKey(currentKey);
            list.add(property);
        }

        Collections.sort(list, new Comparator<Personalisation.Property>() {
            @Override
            public int compare(Personalisation.Property o1, Personalisation.Property o2) {
                return o1.getSorting().compareTo(o2.getSorting());
            }
        });

        for (int i = 0; i < list.size(); i++) {
            Personalisation.Property property = list.get(i);
            String currentKey = property.getKey();

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

            boolean isGroupedStyle = mBaseActivity.getResources().getBoolean(R.bool.is_personalisation_grouped_style);

            // Set edit text hint
            rowEditText.setHint(property.getWatermark().equals("") ? property.getTitle() : property.getWatermark());

            // Set edit text max number of characters and pattern checking
            if (!property.getControl().equals(TEXT_LIST)) {
                if (property.getPattern().equals("") && property.getMaxLength() != -1) {

                    rowEditText.setFilters(new InputFilter[]{
                            new InputFilter.LengthFilter(property.getMaxLength())});

                } else if (!property.getPattern().equals("") && property.getMaxLength() != -1) {

                    rowEditText.setFilters(new InputFilter[]{
                            new RegexInputFilter(property.getPattern()),
                            new InputFilter.LengthFilter(property.getMaxLength())});
                }
            }

            // Set edit text other controls for updated personalisation schema
            if (!property.getControl().equals("")) handleOtherControls(property, rowEditText);

            // Set subtext to show max number of characters
            rowSubText.append(property.getMaxLength().toString());

            // For easier access to the child EditTexts
            rowEditText.setTag(currentKey);

            // Error text also needs easy access for hide / show on form validation
            rowErrorText.setTag(currentKey.concat(ERROR_TEXT_TAG));

            if (i == list.size() - 1) {
                border.setVisibility(View.VISIBLE);

                if (isGroupedStyle) {
                    View layoutFooter = row.findViewById(R.id.personalisation_section_footer);
                    View innerBorder = row.findViewById(R.id.personalisation_section_row_inner_border);
                    layoutFooter.setVisibility(View.VISIBLE);
                    innerBorder.setVisibility(View.GONE);

                    // For grouped style, we specifically need to access the last error text
                    rowErrorText.setTag(ERROR_TEXT_TAG);
                }
            }

            addView(row);
        }

        setVisibility(View.VISIBLE);
    }

    private void handleOtherControls(Personalisation.Property property, EditText editText) {

        editText.setInputType(InputType.TYPE_NULL);
        editText.setClickable(true);
        editText.setFocusableInTouchMode(false);
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_gray_chevron, 0);

        OnClickListener onClickListener = view -> {

            boolean isGroupedStyle = mBaseActivity.getResources().getBoolean(R.bool.is_personalisation_grouped_style);

            TextView rowLabel = (TextView) findViewWithTag(property.getKey().concat(LABEL_TEXT_TAG));
            EditText rowEditText = (EditText) findViewWithTag(property.getKey());
            TextView rowErrorText = (TextView) findViewWithTag(isGroupedStyle ? ERROR_TEXT_TAG :
                    property.getKey().concat(ERROR_TEXT_TAG));

            if (isGroupedStyle) {
                rowErrorText.setVisibility(View.GONE);
                rowLabel.setTextColor(mBaseActivity.getResources().getColor(R.color.text_medium));
            } else {
                rowErrorText.setVisibility(View.GONE);
                rowEditText.setBackgroundResource(R.color.transparent);
            }

            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_number_picker);
            final TextView header = (TextView) dialog.findViewById(R.id.dialog_number_picker_header);
            Button positiveButton = (Button) dialog.findViewById(R.id.dialog_number_picker_positive);
            Button negativeButton = (Button) dialog.findViewById(R.id.dialog_number_picker_negative);
            final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.dialog_number_picker);
            header.setText(property.getTitle());
            negativeButton.setOnClickListener(v -> dialog.dismiss());

            switch (property.getControl()) {
                case DATE_PICKER:

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
                    try {
                        cal.setTime(dateFormat.parse(editText.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            R.style.DatePickerTheme,
                            (v, year, month, dayOfMonth) -> {

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                editText.setText(dateFormat.format(calendar.getTime()));

                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            rowEditText.setBackgroundResource(R.color.transparent);
                        }
                    });
                    datePickerDialog.show();

                    break;
                case YEAR_PICKER:

                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    numberPicker.setMaxValue(MAX_YEAR);
                    numberPicker.setMinValue(MIN_YEAR);
                    numberPicker.setValue(editText.getText().toString().equals("") ? currentYear :
                            Integer.parseInt(editText.getText().toString()));
                    positiveButton.setOnClickListener(v -> {
                        editText.setText(String.valueOf(numberPicker.getValue()));
                        dialog.dismiss();
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            rowEditText.setBackgroundResource(R.color.transparent);
                        }
                    });
                    dialog.show();

                    break;
                case TEXT_LIST:

                    if (property.getEnumElements() == null) break;

                    numberPicker.setMaxValue(property.getEnumElements().size() - 1);
                    numberPicker.setFormatter(value -> property.getEnumElements().get(value).getTitle());
                    int index = property.getIndexOfEnumFromTitle(editText.getText().toString());
                    if (index >= 0) {
                        numberPicker.setValue(index);
                    }
                    positiveButton.setOnClickListener(v -> {
                        editText.setText(property.getEnumElements()
                                .get(numberPicker.getValue()).getTitle());
                        dialog.dismiss();
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            rowEditText.setBackgroundResource(R.color.transparent);
                        }
                    });
                    dialog.show();

                    break;
                case ICONS_LIST:

                    if (property.getEnumElements() == null) break;

                    dialog.setContentView(R.layout.dialog_icon_picker);
                    TextView iconHeader = (TextView) dialog.findViewById(R.id.dialog_icon_picker_header);
                    negativeButton = (Button) dialog.findViewById(R.id.dialog_icon_picker_negative);
                    RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.dialog_icon_recycler);

                    iconHeader.setText(property.getTitle());
                    recyclerView.setLayoutManager(new LinearLayoutManager(
                            getContext(), LinearLayout.HORIZONTAL, false));
                    recyclerView.setAdapter(new PersonalisationIconAdapter(mBaseActivity,
                            property.getEnumElements(), editText, dialog));
                    negativeButton.setOnClickListener(v -> dialog.dismiss());
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            rowEditText.setBackgroundResource(R.color.transparent);
                        }
                    });
                    dialog.show();

                    break;
                case FILE_PICKER:
                    break;
            }

        };

        editText.setOnClickListener(onClickListener);
    }

    public boolean verifyRequiredFields() {

        boolean verified = true;

        // If personalisation was not initialized, return success because no fields are required
        if (mPersonalisation == null) return true;

        // Else check required fields if they have input
        for (String currentKey : mPersonalisation.getRequired()) {

            boolean isGroupedStyle = mBaseActivity.getResources().getBoolean(R.bool.is_personalisation_grouped_style);

            TextView rowLabel = (TextView) findViewWithTag(currentKey.concat(LABEL_TEXT_TAG));

            EditText rowEditText = (EditText) findViewWithTag(currentKey);

            TextView rowErrorText = (TextView) findViewWithTag(isGroupedStyle ? ERROR_TEXT_TAG :
                    currentKey.concat(ERROR_TEXT_TAG));

            String inputText = rowEditText.getText().toString().trim();

            // If a required field was found empty, return verify failed
            if (inputText.isEmpty() || inputText.equals("")) {

                OnFocusChangeListener onFocusChangeListener;

                if (isGroupedStyle) {

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
                        }
                        v.setBackgroundResource(R.color.transparent);
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

            Personalisation.Property property = propertyHashMap.get(currentKey);

            EditText rowEditText = (EditText) findViewWithTag(currentKey);

            String inputText = rowEditText != null ? rowEditText.getText().toString().trim() : null;

            switch (property.getControl()) {
                case TEXT_LIST:
                    inputText = property.getValueFromTitle(inputText);
                    break;
                default:
                    break;
            }

            if (inputText != null && !inputText.isEmpty()) {
                personalizationData.put(currentKey, inputText);
            }
        }

        return personalizationData;
    }

    public class PersonalisationIconAdapter extends RecyclerView.Adapter<PersonalisationIconAdapter.ViewHolder> {

        private Context mContext;
        private List<Personalisation.EnumElement> mData;
        private TextView mTextView;
        private Dialog mDialog;

        public PersonalisationIconAdapter(Context context, List<Personalisation.EnumElement> data, TextView textView, Dialog dialog) {
            this.mContext = context;
            this.mData = data;
            this.mTextView = textView;
            this.mDialog = dialog;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.row_personalisation_icon)
            ImageView image;

            @BindView(R.id.row_personalisation_label)
            TextView label;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_personalisation_icon, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Personalisation.EnumElement item = mData.get(position);

            ImageUtils.loadImage(item.getImageUrl(), holder.image);

            holder.label.setText(item.getValue());

            holder.setIsRecyclable(false);

            holder.itemView.setOnClickListener(v -> {
                mTextView.setText(item.getValue());
                mDialog.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
