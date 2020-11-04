package au.com.dealsdirect.ui.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;

public class DelayedProgressDialog extends ProgressDialog {
    private Handler handler = null;
    private final Runnable runnable = () -> {
        if (getHandler() != null) {
            setHandler(null);
        }
        if (getWindow() != null) {
            getWindow().getDecorView().setVisibility(View.VISIBLE);
        }
    };

    public DelayedProgressDialog(Context context) {
        super(context);
    }

    public DelayedProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public void show(int afterDelay) {
        show();
        if (getWindow() != null) {
            getWindow().getDecorView().setVisibility(View.INVISIBLE);
        }
        handler = new Handler();
        handler.postDelayed(runnable, afterDelay);
    }

    @Override
    public void cancel() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        super.cancel();
    }

    private Handler getHandler() {
        return handler;
    }

    private void setHandler(Handler handler) {
        this.handler = handler;
    }
}
