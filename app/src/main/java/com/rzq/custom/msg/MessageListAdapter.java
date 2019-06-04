package com.rzq.custom.msg;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rzq.custom.R;
import com.rzq.custom.base.BaseListAdapter;
import com.rzq.custom.base.utils.DateUtils;
import com.rzq.custom.base.utils.StringUtil;

public class MessageListAdapter extends BaseListAdapter<MessageListBean, RecyclerView.ViewHolder> {
    private CheckCallBback checkCallBback;

    public interface CheckCallBback {
        void itemCall(MessageListBean bean, int i);

        void itemLongCall(MessageListBean bean, int i);

        void itemDeleteCall(MessageListBean bean, int i);
    }

    public MessageListAdapter(Context mContext) {
        super(mContext);

    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup viewGroup, int i) {
        return new FriendItemView(LayoutInflater.from(mContext).inflate(R.layout.item_frend_list_msg, viewGroup, false));
    }

    @Override
    protected void iniBind(final RecyclerView.ViewHolder viewHolder, final int i) {
        try {
            if (viewHolder instanceof FriendItemView) {
                final MessageListBean bean = getItemList().get(i);
                viewHolder.itemView.setBackgroundResource(R.drawable.white_check_shape);
                ((FriendItemView) viewHolder).iv_delete.setVisibility(View.GONE);
                ((FriendItemView) viewHolder).tv_name.setText(StringUtil.initstr(bean.getName()));
                if (bean.getMsgCount() > 0) {
                    ((FriendItemView) viewHolder).tv_count.setVisibility(View.VISIBLE);
                    ((FriendItemView) viewHolder).tv_count.setText(bean.getMsgCount() + "");
                } else {
                    ((FriendItemView) viewHolder).tv_count.setVisibility(View.GONE);
                }
                ((FriendItemView) viewHolder).tv_time.setText(StringUtil.initstr(DateUtils.getdayTime(bean.getUpdatedt())));
                ((FriendItemView) viewHolder).tv_hint.setText(StringUtil.initstr(bean.getTopMsg()));
                Glide.with(mContext).asBitmap().load(bean.getHead()).error(getHeadstr(bean.getHead())).into(((FriendItemView) viewHolder).iv_head);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((FriendItemView) viewHolder).iv_delete.getVisibility() == View.VISIBLE) {
                            ((FriendItemView) viewHolder).iv_delete.setVisibility(View.GONE);
                            notifyDataSetChanged();
                        } else {
                            if (checkCallBback != null) checkCallBback.itemCall(bean, i);
                        }
                    }
                });
                ((FriendItemView) viewHolder).iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkCallBback != null) checkCallBback.itemDeleteCall(bean, i);
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ((FriendItemView) viewHolder).iv_delete.setVisibility(View.VISIBLE);
                        if (checkCallBback != null) checkCallBback.itemLongCall(bean, i);
                        viewHolder.itemView.setBackgroundResource(R.color.WhiteSmoke);
                        return true;
                    }
                });
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private int getHeadstr(String o) {
        if (o == null) {
            return R.drawable.logo;
        }
        switch (o) {
            case "tx_1":
                return R.drawable.logo;
            case "tx_2":
                return R.drawable.logo;
            case "tx_3":
                return R.drawable.logo;
            case "tx_4":
                return R.drawable.logo;
            case "tx_5":
                return R.drawable.logo;
            case "tx_6":
                return R.drawable.logo;
            case "tx_7":
                return R.drawable.logo;
            default:
                return R.drawable.logo;
        }
    }

    public void setCheckCallBback(CheckCallBback checkCallBback) {
        this.checkCallBback = checkCallBback;
    }

    private class FriendItemView extends RecyclerView.ViewHolder {
        public ImageView iv_head, iv_delete;
        public TextView tv_name, tv_hint, tv_time, tv_count;

        public FriendItemView(View inflate) {
            super(inflate);
            iv_head = inflate.findViewById(R.id.iv_head);
            iv_delete = inflate.findViewById(R.id.iv_delete);
            tv_name = inflate.findViewById(R.id.tv_name);
            tv_hint = inflate.findViewById(R.id.tv_hint);
            tv_time = inflate.findViewById(R.id.tv_time);
            tv_count = inflate.findViewById(R.id.tv_count);
        }
    }
}
