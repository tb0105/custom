package com.rzq.custom.socketClient;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by ${tb0105} on ${DATA}.
 */
public class PopuWindowUtil {
    PopupWindow popupWindow;
    Activity activity;
    private View rootView;


    public PopuWindowUtil(Activity activity, int viewId) {
        this.activity = activity;
        initView(viewId);
    }

    public View getRootView() {
        return rootView;
    }

    private View initView(int viewId) {
        rootView = LayoutInflater.from(activity).inflate(viewId, null, false);
        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(rootView);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return rootView;
    }

    /**
    /**
     * @param pw     popupWindow
     * @param anchor v
     * @param xoff   x轴偏移
     * @param yoff   y轴偏移
     */
    public void showAsDropDown(final PopupWindow pw, final View anchor, final int xoff, final int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            pw.setHeight(height);
            pw.showAsDropDown(anchor, xoff, yoff);
        } else {
            pw.showAsDropDown(anchor, xoff, yoff);
        }
    }

    public void show(View view) {
        popupWindow.showAsDropDown( view, 0, 0);
    }

    public void show(View view, int x, int y) {
        popupWindow.showAsDropDown( view, x, y);
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
