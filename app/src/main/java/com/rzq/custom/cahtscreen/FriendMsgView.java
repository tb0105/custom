package com.rzq.custom.cahtscreen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rzq.custom.R;


public class FriendMsgView extends RecyclerView.ViewHolder {
    public ImageView iv_head, iv_img;
    public ProgressBar pb_resend;

    public TextView tv_msg;

    public FriendMsgView(@NonNull View itemView) {
        super(itemView);
        iv_head = itemView.findViewById(R.id.iv_head);
        pb_resend = itemView.findViewById(R.id.iv_resend);
        tv_msg = itemView.findViewById(R.id.tv_msg);
        iv_img = itemView.findViewById(R.id.iv_img);
    }
}
