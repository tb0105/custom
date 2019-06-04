package com.rzq.custom.cahtscreen;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rzq.custom.R;
import com.rzq.custom.base.utils.OnNextCall;

public class MyMsgView extends RecyclerView.ViewHolder {
    public ImageView iv_head, iv_img;
    public ProgressBar pb_resend;
    public TextView tv_msg, tv_resend;
    public RelativeLayout rl_resend;
    private CountDownTimer timer;

    public MyMsgView(@NonNull View itemView) {
        super(itemView);
        iv_head = itemView.findViewById(R.id.iv_head);
        pb_resend = itemView.findViewById(R.id.iv_resend);
        tv_msg = itemView.findViewById(R.id.tv_msg);
        iv_img = itemView.findViewById(R.id.iv_img);
        rl_resend = itemView.findViewById(R.id.rl_resend);
        tv_resend = itemView.findViewById(R.id.tv_resend);
    }

    /**
     * 倒数计时器
     */
    public void initTimer(long start, long on, final OnNextCall<Object> onNextCall) {
        timer = new CountDownTimer(start, on) {
            /**
             * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
             * @param millisUntilFinished
             */
            @Override
            public void onTick(long millisUntilFinished) {
            }

            /**
             * 倒计时完成时被调用
             */
            @Override
            public void onFinish() {
                onNextCall.onNext("");
                pb_resend.setVisibility(View.GONE);
                tv_resend.setVisibility(View.VISIBLE);
            }
        };
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void start() {
        if (timer != null) {
            timer.start();
        }
    }
}
