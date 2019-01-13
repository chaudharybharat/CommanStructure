package com.example.agc_linux.accounting.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Bharat on 9/5/2017.
 */

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onDoubleClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return true;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }


    /**
     * Returns a human-readable string describing the type of touch that triggered a MotionEvent.
     */

    public static String getTouchType(MotionEvent e) {

        String touchTypeDescription = " ";
        int touchType = e.getToolType(0);

        switch (touchType) {
            case MotionEvent.TOOL_TYPE_FINGER:
                touchTypeDescription += "(finger)";
                break;
            case MotionEvent.TOOL_TYPE_STYLUS:
                touchTypeDescription += "(stylus, ";
                //Get some additional information about the stylus touch
                float stylusPressure = e.getPressure();
                touchTypeDescription += "pressure: " + stylusPressure;

                if (Build.VERSION.SDK_INT >= 21) {
                    touchTypeDescription += ", buttons pressed: " + getButtonsPressed(e);
                }

                touchTypeDescription += ")";
                break;
            case MotionEvent.TOOL_TYPE_ERASER:
                touchTypeDescription += "(eraser)";
                break;
            case MotionEvent.TOOL_TYPE_MOUSE:
                touchTypeDescription += "(mouse)";
                break;
            default:
                touchTypeDescription += "(unknown tool)";
                break;
        }

        return touchTypeDescription;
    }

    /**
     * Returns a human-readable string listing all the stylus buttons that were pressed when the
     * input MotionEvent occurred.
     */
    @TargetApi(21)
    private static String getButtonsPressed(MotionEvent e) {
        String buttons = "";

        if (e.isButtonPressed(MotionEvent.BUTTON_PRIMARY)) {
            buttons += " primary";
        }

        if (e.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
            buttons += " secondary";
        }

        if (e.isButtonPressed(MotionEvent.BUTTON_TERTIARY)) {
            buttons += " tertiary";
        }

        if (e.isButtonPressed(MotionEvent.BUTTON_BACK)) {
            buttons += " back";
        }

        if (e.isButtonPressed(MotionEvent.BUTTON_FORWARD)) {
            buttons += " forward";
        }

        if (buttons.equals("")) {
            buttons = "none";
        }

        return buttons;
    }
    public static interface ClickListener {

        public void onClick(View view, int position);

        public void onLongClick(View view, int position);

        public void onDoubleClick(View view, int position);
    }
}