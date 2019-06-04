package com.rzq.custom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by ${tb0105} on ${DATA}.
 */
public class PopuWindowUtils {
    PopupWindow popupWindow;
    Activity activity;
    private View rootView;


    public PopuWindowUtils(Activity activity, int viewId) {
        this.activity = activity;
        initView(viewId);
    }

    public View getRootView() {
        return rootView;
    }

    private View initView(int viewId) {
        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(LayoutInflater.from(activity).inflate(viewId, null,false));
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        rootView = popupWindow.getContentView();
        return rootView;
    }

    public void show(View view) {
        popupWindow.showAsDropDown(view);
    }

    public void show(View view, int x, int y) {
        popupWindow.showAsDropDown(view, x, y);
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
