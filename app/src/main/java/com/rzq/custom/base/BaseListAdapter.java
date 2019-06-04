package com.rzq.custom.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${tb0105} on ${DATA}.
 */
public abstract class BaseListAdapter<B, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    List<B> itemList;
    protected Context mContext;

    public List<B> getItemList() {
        return itemList;
    }

    public void setItemList(List<B> itemList) {
        this.itemList = itemList;
    }

    public BaseListAdapter(Context mContext) {
        this.mContext = mContext;
        itemList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return initViewHolder(viewGroup, i);
    }

    protected abstract VH initViewHolder(ViewGroup viewGroup, int i);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        iniBind((VH) viewHolder, i);
    }

    protected abstract void iniBind(VH viewHolder, int i);

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getItemCount() {
        return itemList.size();
    }


}
