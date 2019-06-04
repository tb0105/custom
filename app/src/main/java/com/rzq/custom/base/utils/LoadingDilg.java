package com.rzq.custom.base.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.rzq.custom.R;


/**
 * Created by Administrator on 2019/1/14.
 */

public class LoadingDilg extends AlertDialog {
    private final Context mContext;
    private String msg;
    private OnNextCall<String> listener;
    private LoadingDilg dialog;
    private TextView tv_dialog;

    public String getMsg() {
        return msg;
    }

    public TextView getTv_dialog() {
        return tv_dialog;
    }

    public LoadingDilg(Context context, String msg) {
        super(context, R.style.MyDialogStyle);
        this.msg = msg;
        this.mContext = context;

    }

    public LoadingDilg(Context context, String msg, OnNextCall nextCall) {
        super(context, R.style.MyDialogStyle);
        this.msg = msg;
        this.listener = nextCall;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        tv_dialog = findViewById(R.id.tv_dialog);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null)
                    listener.onNext("dismiss");
            }
        });
        if (msg != null) {
            tv_dialog.setText(msg);
        }
    }

    public void setdialog(String msg) {
        if (tv_dialog != null)
            tv_dialog.setText(msg);
    }

    public LoadingDilg start() {
        show();
        return this;
    }
}
