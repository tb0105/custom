package com.rzq.custom.cahtscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rzq.custom.R;
import com.rzq.custom.base.BaseListAdapter;
import com.rzq.custom.base.utils.OnNextCall;
import com.rzq.custom.base.utils.StringUtil;
import com.rzq.custom.cahtscreen.db.MessageBean;
import com.rzq.custom.cahtscreen.db.MessageDao;
import com.rzq.custom.cahtscreen.db.MessageLockBean;

public class ChatScreenAdapter extends BaseListAdapter<MessageBean, RecyclerView.ViewHolder> {
    private Bitmap myHead;
    private Bitmap friendHead;
    private OnNextCall<MessageBean> checkHeadCallBack;
    private MsgOnNextCall<MessageBean> reSendCallBack;
    private MsgOnNextCall<MessageBean> sendCallBack;
    private MessageDao messageDao;
    private OnNextCall<MessageBean> itemCheck;
    private OnNextCall<MessageBean> itemLongCheck;

    public void setItemLongCheck(OnNextCall<MessageBean> itemLongCheck) {
        this.itemLongCheck = itemLongCheck;
    }

    public void setSendCallBack(MsgOnNextCall<MessageBean> sendCallBack) {
        this.sendCallBack = sendCallBack;
    }

    public void setCheckHeadCallBack(OnNextCall<MessageBean> checkHeadCallBack) {
        this.checkHeadCallBack = checkHeadCallBack;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
    }

    public void setReSendCallBack(MsgOnNextCall<MessageBean> reSendCallBack) {
        this.reSendCallBack = reSendCallBack;
    }

    public void setFriendHead(Bitmap friendHead) {
        this.friendHead = friendHead;
    }

    public void setMyHead(Bitmap myHead) {
        this.myHead = myHead;
    }

    public ChatScreenAdapter(Context mContext) {
        super(mContext);
        messageDao = new MessageDao(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemList().get(position).getSenderid();
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup viewGroup, int i) {
        int user = UserInfo.getUserId();
        if (i == user)
            return new MyMsgView(LayoutInflater.from(mContext).inflate(R.layout.item_my_msg, viewGroup, false));
        else
            return new FriendMsgView(LayoutInflater.from(mContext).inflate(R.layout.item_frend_msg, viewGroup, false));

    }

    @Override
    protected void iniBind(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MyMsgView) {
            initBindMyMsgView((MyMsgView) viewHolder, i);
        } else if (viewHolder instanceof MyRedPacketView) {
            initBindMyRedPacketView((MyRedPacketView) viewHolder, i);
        } else if (viewHolder instanceof FriendRedPacketView) {
            initBindFriendRedPacketView((FriendRedPacketView) viewHolder, i);
        } else if (viewHolder instanceof FriendMsgView) {
            initBindFriendMsgView((FriendMsgView) viewHolder, i);
        }
    }

    /**
     * 我的消息
     *
     * @param viewHolder
     * @param i
     */
    private void initBindMyMsgView(final MyMsgView viewHolder, int i) {
        try {
            final MessageBean bean = getItemList().get(i);
            viewHolder.iv_head.setImageBitmap(myHead);
            viewHolder.iv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemCheck != null)
                        itemCheck.onNext(bean);
                }
            });
            if (StringUtil.isnotempty(bean.getMsginfo()) && bean.getMsginfo().contains("[pic]")) {
                viewHolder.iv_img.setVisibility(View.VISIBLE);

                viewHolder.tv_msg.setVisibility(View.GONE);
                Glide.with(mContext).asBitmap().load(bean.getMsginfo().replace("[pic]", "")).into(viewHolder.iv_img);
            } else {
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (itemLongCheck != null)
                            itemLongCheck.onNext(bean);
                        return true;
                    }
                });
                if (StringUtil.isnotempty(bean.getImg())) {
                    viewHolder.iv_img.setVisibility(View.VISIBLE);
                    viewHolder.tv_msg.setVisibility(View.GONE);

                    Glide.with(mContext).asBitmap().load(bean.getImg()).into(viewHolder.iv_img);
                } else {
                    viewHolder.iv_img.setVisibility(View.GONE);
                    viewHolder.tv_msg.setVisibility(View.VISIBLE);
                    viewHolder.tv_msg.setText(bean.getMsginfo());
                }
            }
            if (bean.getStatus() == MessageLockBean.NOT) {
                viewHolder.pb_resend.setVisibility(View.GONE);
                viewHolder.tv_resend.setVisibility(View.VISIBLE);
                viewHolder.rl_resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reSendCallBack != null) {
                            reSendCallBack.onNext(bean, viewHolder);
                        }
                    }
                });
            } else if (bean.getStatus() == MessageBean.SEND) {
                viewHolder.pb_resend.setVisibility(View.VISIBLE);
                viewHolder.tv_resend.setVisibility(View.GONE);
                viewHolder.initTimer(10 * 1000, 1000, new OnNextCall<Object>() {
                    @Override
                    public void onNext(Object next) {
                        if (messageDao.query(bean.getIdx() + "").getStatus() != MessageBean.OK) {
                            bean.setStatus(MessageBean.NOT);
                            messageDao.replace(bean);
                            viewHolder.rl_resend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (reSendCallBack != null) {
                                        reSendCallBack.onNext(bean, viewHolder);
                                    }
                                }
                            });
                        }
                    }
                });
                viewHolder.start();
                if (sendCallBack != null) {
                    sendCallBack.onNext(bean, viewHolder);
                }
            } else {
                viewHolder.stop();
                viewHolder.pb_resend.setVisibility(View.GONE);
                viewHolder.tv_resend.setVisibility(View.GONE);
            }
            viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkHeadCallBack != null) {
                        checkHeadCallBack.onNext(bean);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 朋友的
     *
     * @param viewHolder
     * @param i
     */
    private void initBindFriendMsgView(FriendMsgView viewHolder, int i) {
        try {
            final MessageBean bean = getItemList().get(i);
            viewHolder.iv_head.setImageBitmap(friendHead);
            viewHolder.pb_resend.setVisibility(View.GONE);
            viewHolder.iv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemCheck != null)
                        itemCheck.onNext(bean);
                }
            });
            if (StringUtil.isnotempty(bean.getMsginfo()) && bean.getMsginfo().contains("[pic]")) {
                viewHolder.iv_img.setVisibility(View.VISIBLE);
                viewHolder.tv_msg.setVisibility(View.GONE);
                Glide.with(mContext).asBitmap().load(bean.getMsginfo().replace("[pic]", "")).into(viewHolder.iv_img);
            } else {
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (itemLongCheck != null)
                            itemLongCheck.onNext(bean);
                        return true;
                    }
                });
                viewHolder.iv_img.setVisibility(View.GONE);
                viewHolder.tv_msg.setVisibility(View.VISIBLE);
                viewHolder.tv_msg.setText(bean.getMsginfo());
            }
            viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkHeadCallBack != null) {
                        checkHeadCallBack.onNext(bean);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 朋友发的包
     *
     * @param viewHolder
     * @param i
     */
    private void initBindFriendRedPacketView(FriendRedPacketView viewHolder, int i) {

    }

    /**
     * 我发的包
     *
     * @param viewHolder
     * @param i
     */
    private void initBindMyRedPacketView(MyRedPacketView viewHolder, int i) {

    }

    public void setItemCheck(OnNextCall<MessageBean> itemCheck) {
        this.itemCheck = itemCheck;
    }

    public OnNextCall<MessageBean> getItemCheck() {
        return itemCheck;
    }


    public interface MsgOnNextCall<T> {
        void onNext(T bean, MyMsgView viewHolder);
    }
}
