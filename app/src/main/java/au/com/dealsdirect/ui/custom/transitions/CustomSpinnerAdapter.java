package au.com.dealsdirect.ui.custom.transitions;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul on 7/6/17.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> stringList;
    private LayoutInflater mInflater;

    public CustomSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        this.context = context;
        stringList = objects;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_custom_spinner_drop_down, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mSpinnerText.setText(stringList.get(position));
        return convertView;
    }

    public static class ViewHolder {

        @BindView(R.id.row_spinner_text)
        TextView mSpinnerText;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
