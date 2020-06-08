package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

/**
 * Created by smartwave on 15/08/2018.
 */

public class AdaptiveTabLayout extends TabLayout {

    private int mTabCount = 0;

    public AdaptiveTabLayout(Context context) {
        super(context);
    }

    public AdaptiveTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMinTabWidth();
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        addTab(tab, getTabCount() == 0);
    }

    @Override
    public void addTab(@NonNull Tab tab, int position) {
        addTab(tab, position, getTabCount() == 0);
    }

    @Override
    public void addTab(@NonNull Tab tab, boolean setSelected) {
        addTab(tab, getTabCount(), setSelected);
    }

    @Override
    public void addTab(@NonNull TabLayout.Tab tab, int position, boolean setSelected) {
        super.addTab(tab, position, setSelected);

        //ANDR - Fit filters on the screen (TAB) https://apacsale.atlassian.net/browse/GEN-9022
        //set tabs layout weight to 1 so that when rotated to landscape even still in scrollable mode, it will fill whole width
        LinearLayout view = tab.view;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.weight = 1;
        view.setLayoutParams(lp);
    }

    public void setTabCount(int tabCount) {
        this.mTabCount = tabCount;
    }

    public void setMinTabWidth() {
        try {
            if (mTabCount == 0)
                return;
            Field field = TabLayout.class.getDeclaredField("mScrollableTabMinWidth");
            field.setAccessible(true);
            field.set(this, (int) (getMeasuredWidth() / (float) mTabCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
