package com.rzq.custom.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.rzq.custom.base.utils.BaseOnClick;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<B, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    List<B> itemList;
    protected Context mContext;
    protected BaseOnClick myOnClick;
    public static final int BOTTOMCODE = -8888;
    public boolean isLoad;


    public BaseAdapter(Context mContext, boolean isLoad) {
        this.mContext = mContext;
        this.isLoad = isLoad;
        itemList = new ArrayList<>();
    }

    public void setMyOnClick(BaseOnClick myOnClick) {
        this.myOnClick = myOnClick;
    }

    public List<B> getItemList() {
        return itemList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        initBind((VH) viewHolder, i);
    }

    protected abstract void initBind(VH viewHolder, int i);

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return initHolder(viewGroup, i);
    }

    protected abstract VH initHolder(ViewGroup viewGroup, int i);


    public void setItemList(List<B> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemList().get(position) == null && isLoad && getItemCount() >= 10 && getItemCount() - 1 == position) {
            return BOTTOMCODE;
        }
        return position;
    }

    public void addItem(List<B> list, boolean bisup) {
        if (getItemCount() >= 10 && bisup) {
            notifyItemRemoved(getItemCount());
            itemList.remove(getItemCount() - 1);
        }
        itemList.addAll(list);
        if (getItemCount() >= 10 && list.size() == 10) {
            itemList.add(null);
        }

    }

    public void setItem(List<B> list, boolean bisup) {
        if (getItemCount() >= 10 && bisup) {
            notifyItemRemoved(getItemCount());
            itemList.remove(getItemCount() - 1);
        }
        itemList = (list);
        if (getItemCount() >= 10 && list.size() == 10) {
            itemList.add(null);
        }

    }

    @Override

    public int getItemCount() {
        return itemList.size();
    }

}
