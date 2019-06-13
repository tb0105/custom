package com.rzq.custom;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.rzq.custom.base.BaseAc;
import com.rzq.custom.base.BaseAction;
import com.rzq.custom.base.BaseBroadcastReciver;
import com.rzq.custom.base.BradcastHelper;
import com.rzq.custom.base.RefreshView;
import com.rzq.custom.base.utils.DateUtils;
import com.rzq.custom.base.utils.HttpUtil;
import com.rzq.custom.base.utils.OnNextCall;
import com.rzq.custom.base.utils.RxMianUtli;
import com.rzq.custom.cahtscreen.AcChatScreen;
import com.rzq.custom.cahtscreen.ChatService;
import com.rzq.custom.cahtscreen.db.FrendBean;
import com.rzq.custom.cahtscreen.db.FriendDao;
import com.rzq.custom.cahtscreen.db.MesageLockDao;
import com.rzq.custom.cahtscreen.db.MessageBean;
import com.rzq.custom.cahtscreen.db.MessageDao;
import com.rzq.custom.cahtscreen.db.MessageLockBean;
import com.rzq.custom.msg.MessageListAdapter;
import com.rzq.custom.msg.MessageListBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;

public class MainActivity extends BaseAc implements RefreshView.LoadingListenner, MessageListAdapter.CheckCallBback {
    private final String TAG = "MainActivity";
    RefreshView refreshView;
    MessageListAdapter adapter;
    BradcastHelper bradcastHelper;
    private RxMianUtli<List<MessageListBean>> messageRx;
    CustomHttpUtil customHttpUtils;

    @Override
    protected AcBean initAc() {
        return new AcBean(this, R.layout.fragment_message, "客服", true, true);
    }

    @Override
    protected void initBar() {
        super.initBar();
        rl_bar.setBackgroundResource(R.color.newBlueBtn);
    }

    @Override
    protected void initViews() {
        customHttpUtils = new CustomHttpUtil(mContext);
        refreshView = findViewById(R.id.refreshView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        refreshView.setLayoutManager(linearLayoutManager);
        refreshView.setLoadinglistenner(this);
        adapter = new MessageListAdapter(mContext);
        refreshView.setAdapter(adapter);
        adapter.setCheckCallBback(this);
        bradcastHelper = new BradcastHelper(mContext, new ChatMessageReciver(this, mContext));
        ArrayList<BaseAction> actionlist = new ArrayList<>();
        actionlist.add(new BaseAction(ChatService.SOCKETMSG_CHAT, null));
        actionlist.add(new BaseAction(ChatService.SOCKETMSG_NEW_FRIEND_INFO, null));
        bradcastHelper.setActions(actionlist);
        bradcastHelper.regist();
        initMessageRx();

    }

    private void initMessageRx() {
        messageRx = new RxMianUtli<List<MessageListBean>>() {
            @Override
            protected void initService(Observer<? super List<MessageListBean>> observer) {
                try {
                    FriendDao friendDao = new FriendDao(mContext);
                    MessageDao messagedo = new MessageDao(mContext);
                    MesageLockDao mesageLockDao = new MesageLockDao(mContext);

                    List<MessageListBean> messagelist = new ArrayList<>();

                    List<MessageBean> list = messagedo.queryChatOline();
                    for (int i = 0; i < list.size(); i++) {
                        MessageListBean bean = new MessageListBean();
                        MessageBean msgBean = messagedo.querymsgid(list.get(i));

                        FrendBean friendBean;
                        Intent intent = new Intent(mActivity, ChatService.class);
                        intent.setAction(ChatService.GETUSERINFO);
                        if (msgBean.getSenderid() == 2) {
                            friendBean = friendDao.query(msgBean.getReceiverid() + "");
                            intent.putExtra("user", msgBean.getReceiverid());
                        } else {
                            friendBean = friendDao.query(msgBean.getSenderid() + "");
                            intent.putExtra("user", msgBean.getSenderid());
                        }
                        if (friendBean == null) {
                            startService(intent);
                        } else {
                            bean.setName(friendBean.getComment());
                        }
//                        if (friendBean == null) {
//                            Map<String, Object> dataMap = customHttpUtils.getshowinfobyuserid(UserInfo.getMap(msgBean.getSenderid() + ""));
//                            String resultdata = (String) dataMap.get("result");
//                            if (!resultdata.equals("[]")) {
//                                JSONArray arry = JSON.parseArray(resultdata);
//                                if (arry.size() > 0) {
//                                    JSONObject obj = arry.getJSONObject(0);
//                                    //"result" -> "[{"userid":6,"showname":"出来","myhead":"1"}]"
//                                    bean.setHead(obj.getString("myhead"));
//                                    bean.setName(obj.getString("showname") + "(" + mContext.getResources().getString(R.string.msr) + ")");
//                                }
//                            }
//                        }

                        int count = 0;
                        List<MessageBean> countlist = messagedo.queryStatusCount(msgBean.getSenderid());
                        for (MessageBean messageBean : countlist) {
                            MessageLockBean lockb = mesageLockDao.query(messageBean.getMsgid());
                            if (null != lockb && lockb.getState() == -1)
                                count++;
                        }
                        bean.setMsgCount(count);

                        bean.setTopMsg(messagedo.queryStatusTomsg(msgBean.getSenderid()) + "");
                        Date dyt = messagedo.queryStatusToUpdatedt(msgBean);
                        bean.setUpdatedt(dyt);
                        bean.setFriend(msgBean.getSenderid() + "");
                        messagelist.add(bean);
                    }
                    Collections.sort(messagelist, new Comparator<MessageListBean>() {
                        @Override
                        public int compare(MessageListBean o1, MessageListBean o2) {
                            try {
                                long o1start = DateUtils.dateToLong(o1.getUpdatedt());
                                long o2start = DateUtils.dateToLong(o2.getUpdatedt());
                                if (o1start < o2start) {
                                    return 1;
                                } else if (o1start == o2start) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });

                    if (list != null) {
                        if (messagelist != null) {
                            observer.onNext(messagelist);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onError(new Throwable(e));
                }
                observer.onComplete();
            }
        }.

                setOnStartCall(new OnNextCall() {
                    @Override
                    public void onNext(Object next) {

                    }
                }).

                setOnNextCall(new OnNextCall<List<MessageListBean>>() {
                    @Override
                    public void onNext(List<MessageListBean> next) {
                        refreshView.setItemList(next);
                    }
                }).

                setOnErroCall(new OnNextCall<Throwable>() {
                    @Override
                    public void onNext(Throwable next) {
//                ToastUtils.show(mContext, next.getMessage());
                    }
                }).

                setOnCompleteCall(new OnNextCall() {
                    @Override
                    public void onNext(Object next) {
                        refreshView.notifyDataSetChanged();
                    }
                }).

                start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setalarmaneger();
        initMessageRx();
        try {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = (NotificationChannel) mNotificationManager.getNotificationChannel("111");
                if (mChannel != null)
                    mNotificationManager.deleteNotificationChannel(mChannel.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            bradcastHelper.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent pi;

    /**
     * 设置心跳闹钟
     */
    private void setalarmaneger() {
        Intent intent = new Intent(this, ChatService.class);
        intent.setAction(ChatService.SOCKETMSG_PULLMSG);
        pi = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1 * 1000, 30 * 1000, pi);

    }

    @Override
    public void upLoad() {

    }

    @Override
    public void downRefresh() {
        initMessageRx();
    }

    @Override
    public void itemCall(MessageListBean bean, int i) {
        try {
            Intent intent = new Intent(mActivity, AcChatScreen.class);
            FrendBean frendBean = new FrendBean();
            frendBean.setComment(bean.getName());
            frendBean.setHead(bean.getHead());
            frendBean.setGrop(bean.getTopMsg());
            frendBean.setFriendid(Integer.parseInt(bean.getFriend()));
            frendBean.setUpdatedt(null);
            intent.putExtra("data", new Gson().toJson(frendBean));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemLongCall(MessageListBean bean, int i) {

    }

    @Override
    public void itemDeleteCall(MessageListBean bean, int i) {
        refreshView.getItemList().remove(i);
        refreshView.getRecyclerView().removeViewAt(i);
        new MessageDao(mContext).delete(bean.getFriend());
        refreshView.notifyDataSetChanged();
    }

    private class ChatMessageReciver extends BaseBroadcastReciver<MainActivity> {

        public ChatMessageReciver(MainActivity fragmentMessage, Context context) {
            super(fragmentMessage, context);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ChatService.SOCKETMSG_CHAT: {//消息
                    classf.upMessage();
                }
                break;
                case ChatService.SOCKETMSG_NEW_FRIEND_INFO: {
                    classf.upMessage();
                }
                break;
            }
        }
    }

    /**
     * 更新消息
     */
    private void upMessage() {
        initMessageRx();
    }
}
