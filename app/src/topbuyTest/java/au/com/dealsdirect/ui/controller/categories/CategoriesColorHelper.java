package au.com.dealsdirect.ui.controller.categories;

import java.util.HashMap;

/**
 * Created by Admin on 12/29/16.
 */
public class CategoriesColorHelper {

    static String[] firstSet = {
            "#DF00B5E9",
            "#DF9013FE",
            "#DFFF785F",
            "#DFFFBD00",
            "#DFBED100",
            "#DF00B1B5",
            "#DFFF9100",
            "#DFD95358",
            "#DF4A6172"
    };

    static String[] secondSet = {
            "#DF00A9D9",
            "#DF820DEA",
            "#DFED6C54",
            "#DFFFB300",
            "#DFB3C503",
            "#DF02A8AC",
            "#DFFF8300",
            "#DFD1474C",
            "#DF3F5362"
    };

    static String[] thirdSet = {
            "#DF00B5E9",
            "#DF9013FE",
            "#DFFF785F",
            "#DFFFBD00",
            "#DF00B1B5",
            "#DFFF9100",
            "#DFD95358",
            "#DF4A6172"
    };

    static String[] fourthSet = {
            "#DF00B5E9",
            "#DF9013FE",
            "#DFFF785F",
            "#DFFFBD00",
            "#DF00B1B5",
            "#DFFF9100",
            "#DFD95358",
            "#DF4A6172"
    };

    static String[] fifthSet = {
            "#DF00B5E9",
            "#DF9013FE",
            "#DFFF785F",
            "#DFFFBD00",
            "#DF00B1B5",
            "#DFFF9100",
            "#DFD95358",
            "#DF4A6172"
    };


    public static HashMap<Integer, String[]> categoryBackgroundColors = new HashMap<>();

    public CategoriesColorHelper() {
    }

    public String[] getBackgroundColor(int iteration){
        categoryBackgroundColors.put(0, firstSet);
        categoryBackgroundColors.put(1, firstSet);
        categoryBackgroundColors.put(2, secondSet);
        categoryBackgroundColors.put(3, thirdSet);
        categoryBackgroundColors.put(4, fourthSet);
        categoryBackgroundColors.put(5, fifthSet);

        if (iteration > 5){
            iteration = 5;
        }
        return categoryBackgroundColors.get(iteration);
    }




}
