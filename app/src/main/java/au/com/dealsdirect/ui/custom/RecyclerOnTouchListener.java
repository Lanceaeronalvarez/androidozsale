package au.com.dealsdirect.ui.custom;

/**
 * Created by smartwave on 21/06/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerOnTouchListener extends RecyclerView.SimpleOnItemTouchListener{

    private OnItemClickListener mListener;
    GestureDetector mGestureDetector;

    public interface OnItemClickListener{
        public void onItemClick(View v, int position);
    }

    public RecyclerOnTouchListener(Context context, OnItemClickListener listener) {
        mListener=listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(),e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        super.onTouchEvent(rv, e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.onRequestDisallowInterceptTouchEvent(disallowIntercept);
    }
}
