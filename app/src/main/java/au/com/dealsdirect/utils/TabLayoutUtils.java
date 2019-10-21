package au.com.dealsdirect.utils;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.tabs.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import java.util.List;

import au.com.dealsdirect.R;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/*
 * Created by Ayi on 31/05/2017.
 */

public class TabLayoutUtils {

    public static void setupWithCustomFont(Context context, TabLayout tabLayout, List<String> tabTitles, String fontPath) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tabCustomView = LayoutInflater.from(context).inflate(R.layout.tab_custom_font, null);
            TextView tabTitle = (TextView) tabCustomView.findViewById(R.id.tab_title);
            tabTitle.setText(tabTitles.get(i));
            CalligraphyUtils.applyFontToTextView(tabTitle, Typeface.createFromAsset(context.getAssets(), fontPath));
            tabLayout.getTabAt(i).setCustomView(tabCustomView);
        }
    }

    public static void setupWithCustomTextView(Context context, TabLayout tabLayout, List<String> tabTitles) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tabCustomView = LayoutInflater.from(context).inflate(R.layout.tab_custom_font, null);
            TextView tabTitle = (TextView) tabCustomView.findViewById(R.id.tab_title);
            tabTitle.setText(tabTitles.get(i));
            tabLayout.getTabAt(i).setCustomView(tabCustomView);
        }
    }

//    public static void setupWithLeftIcon(Context context, TabLayout tabLayout, String[] tabTitles, int[] tabIcons) {
//        for (int i = 0; i < tabTitles.length; i++) {
//            RelativeLayout tabCustomView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.tab_left_icon, null);
//            TextView tabTitle = (TextView) tabCustomView.findViewById(R.id.tab_title);
//            tabTitle.setText(tabTitles[i]);
//            ImageView tabIcon = (ImageView) tabCustomView.findViewById(R.id.tab_icon);
//            tabIcon.setImageResource(tabIcons[i]);
//            tabLayout.getTabAt(i).setCustomView(tabCustomView);
//        }
//    }
}
