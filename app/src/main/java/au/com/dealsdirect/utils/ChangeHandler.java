package au.com.dealsdirect.utils;

import android.os.Build;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;

/**
 * dp Created by Admin on 5/18/18.
 */

public class ChangeHandler {

    /**
     * Returns the appropriate change handler depending on the SDK version
     * with remove from view on push option
     *
     * @param removesFromViewOnPush
     * @return ControllerChangeHandler
     */
    public ControllerChangeHandler fadeChangeHandler(boolean removesFromViewOnPush) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return new FadeChangeHandler(removesFromViewOnPush);
        return new SharedArcFadePopChangeHandler();
    }

    /**
     * Returns the appropriate change handler depending on the SDK version
     *
     * @return ControllerChangeHandler
     */
    public ControllerChangeHandler fadeChangeHandler(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return new FadeChangeHandler();
        return new SharedArcFadePopChangeHandler();
    }
}
