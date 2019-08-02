package com.rzq.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.rzq.custom.base.BaseAdapter;

/**
 * 在线客服列表适配器
 */
public class CustomAdapter extends BaseAdapter<CustomBean, CustomAdapter.ItemViewHoder> {

    private CustomBeanListener<CustomBean> customBeanListener;

    public CustomAdapter(Context mContext) {
        super(mContext, false);
    }

    public void setCustomBeanListener(CustomBeanListener<CustomBean> customBeanListener) {
        this.customBeanListener = customBeanListener;
    }


    @Override
    protected void initBind(ItemViewHoder viewHolder, int i) {
        try {
            final CustomBean bean = getItemList().get(i);
            int tx;
            if (i % 2 == 0) {
                tx = R.drawable.touxiang_orange;
            } else {
                tx = R.drawable.touxiang_red;
            }
            Glide.with(mContext).load(tx)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.iv_head);
            viewHolder.tv_name.setText(bean.name);
            viewHolder.bt_consult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customBeanListener != null)
                        customBeanListener.Call(bean);
                }
            });
            if (bean.notnumber > 0) {
                viewHolder.tv_num.setVisibility(View.VISIBLE);
                viewHolder.tv_num.setText(bean.notnumber + "");
            } else {
                viewHolder.tv_num.setVisibility(View.GONE);
                viewHolder.tv_num.setText(bean.notnumber + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected ItemViewHoder initHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHoder(LayoutInflater.from(mContext).inflate(R.layout.item_custom_service, viewGroup, false));
    }

    public class ItemViewHoder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_num;
        ImageView iv_head;
        Button bt_consult;

        public ItemViewHoder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_head = itemView.findViewById(R.id.iv_head);
            tv_num = itemView.findViewById(R.id.tv_num);
            bt_consult = itemView.findViewById(R.id.bt_consult);
        }
    }
}
