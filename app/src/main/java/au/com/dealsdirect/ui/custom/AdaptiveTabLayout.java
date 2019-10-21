package au.com.dealsdirect.ui.custom;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import android.util.AttributeSet;

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
