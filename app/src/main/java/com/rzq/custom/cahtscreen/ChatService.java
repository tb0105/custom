package com.rzq.custom.cahtscreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rzq.custom.Defines;
import com.rzq.custom.Notification;
import com.rzq.custom.ShareUtil;
import com.rzq.custom.base.service.BaseService;
import com.rzq.custom.base.utils.StringUtil;
import com.rzq.custom.base.utils.ToastUtils;
import com.rzq.custom.cahtscreen.db.FrendBean;
import com.rzq.custom.cahtscreen.db.FriendDao;
import com.rzq.custom.cahtscreen.db.MesageLockDao;
import com.rzq.custom.cahtscreen.db.MessageBean;
import com.rzq.custom.cahtscreen.db.MessageDao;
import com.rzq.custom.cahtscreen.db.MessageLockBean;
import com.rzq.custom.socketClient.ConnectHandler;
import com.rzq.custom.socketClient.Disposable;
import com.rzq.custom.socketClient.LiteSocketClient;
import com.rzq.custom.socketClient.MessageHandler;
import com.rzq.custom.socketClient.SocketClient;
import com.rzq.custom.socketClient.TextProtocols;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatService extends BaseService implements BaseService.ReceiveCallBack {
    public static final String SOCKETMSG_CHATGIFT = "com.rzq.rose.service.ChatService.SOCKETMSG_CHATGIFT";
    public static final String ONSELFASSETS = "onselfassets";
    public static final String GETUSERINFO = "getuuserinfo";
    public static final String SOCKETMSG_NEW_FRIEND_INFO = "SOCKETMSG_NEW_FRIEND_INFO";
    private final String TAG = "ChatService";
    private LiteSocketClient connector;
    private Disposable chb;
    private Disposable cha;
    public static final String SOCKETMSGACTION = "com.rzq.rose.service.ChatService.SOCKETMSGACTION";
    public static final String SOCKETMSG = "com.rzq.rose.service.ChatService.SOCKETMSG";
    public static final String SOCKETMSG_CHAT = "com.rzq.rose.service.ChatService.SOCKETMSG_CHAT";
    public static final String SOCKETMSG_PULLMSG = "com.rzq.rose.service.ChatService.SOCKETMSG_PULLMSG";
    private MessageDao messagedao;
    private MesageLockDao messagelockdao;
    private FriendDao friendDao;

    @Override
    public void onCreate() {
        super.onCreate();
        setReceiveCallBack(this);
        Log.e(TAG, "Service Oncreate");
        ReCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (connector == null) {
            ReCreate();
        }
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case SOCKETMSG_CHAT: {
                    String msg = intent.getStringExtra(SOCKETMSG);
                    SendRequest("sendchat " + msg);
                    Log.e(TAG, "sendchat " + msg);
                }
                break;
                case SOCKETMSG_PULLMSG: {
                    String id = UserInfo.getUserId() + "";
                    Log.e(TAG, "pullmsg " + id);
                    SendRequest("pullmsg " + id);
                }
                break;
                case GETUSERINFO: {
                    int user = intent.getIntExtra("user", -1);
                    Log.e(TAG, "QueryUser " + user);
                    SendRequest("QueryUser " + user);
                }
                break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void SendRequest(String msg) {
        connector.send(msg);
    }

    private void ReCreate() {
        SocketClient client = new SocketClient.Builder()
                .ip(Defines.getAuctionServer(CustomApp.instance)) //设置ip、端口
                .port(Defines.getPort2())
                .setKeepAlive(true) //设置socket选项
                .build();
        connector = new LiteSocketClient.Builder()
                .client(client) //设置SocketClient
                .protocols(TextProtocols.create()) //使用text协议
                .pingInterval(15) //设置心跳间隔（秒）大于0打开心跳功能
                .build();


        cha = connector.onConnect(new ConnectHandler() {

            @Override
            public void connectSuccess() {
                Log.e(TAG, "===连接成功===");
            }

            @Override
            public void connectFail() {

                ShareUtil.toast(getApplicationContext(), "连接服务器失败");
            }

            @Override
            public void disconnect() {
                Log.e(TAG, "===连接断开===");
            }

        });

        chb = connector.onMessage(new MessageHandler() {
            @Override
            public void receive(byte[] data) {
                String str = new String(data);
                Log.e("DEBUG", str);

                try {
                    Map<String, String> map = JSON.parseObject(str, new TypeReference<Map<String, String>>() {
                    });
                    String response = map.get("request");
                    switch (response) {
                        case "Pong": {
                            break;
                        }
                        case "PriceUp": {
                            break;
                        }
                        case "QueryUser": {
                            try {
                                String result = map.get("result");
                                String code = map.get("code");
                                Log.e(TAG, "QueryUser:" + result);
                                //{"request":"QueryUser","code":"1","result":"[{\"username\":\"pppp11\",\"mycode\":\"68428081\"}]"}

                                if (friendDao == null)
                                    friendDao = new FriendDao(getApplicationContext());
                                if (StringUtil.isnotempty(result)) {
                                    JSONObject infoobj = new JSONArray(result).getJSONObject(0);
                                    FrendBean friend = new FrendBean();
                                    friend.setComment(infoobj.getString("username"));
                                    friend.setGrop("");
                                    friend.setHead("");
                                    friend.setStatus(1);
                                    friend.setUserid(UserInfo.getUserId());
                                    friend.setUpdatedt(new Date(System.currentTimeMillis()));
                                    friend.setFriendid(Integer.parseInt(code));
                                    friendDao.replace(friend);
                                    Intent intent = new Intent(SOCKETMSG_NEW_FRIEND_INFO);
                                    sendBroadcast(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                        case "sendrespone": {
                            //{"request":"sendrespone","code":"1","result":"6,15,7,0"}
                            try {
                                String result = map.get("result");
                                String code = map.get("result");
                                Log.e(TAG, "sendrespone:" + result);

                                if (messagedao == null)
                                    messagedao = new MessageDao(getApplicationContext());
                                if (messagelockdao == null)
                                    messagelockdao = new MesageLockDao(getApplicationContext());
                                String[] resuldata = result.split(",");
                                MessageBean bean = messagedao.query(resuldata[3]);
                                bean.setStatus(MessageBean.OK);
                                bean.setMsgid(Integer.parseInt(resuldata[1]));
                                messagedao.replace(bean);
                                Intent intent = new Intent(SOCKETMSG_CHAT);
                                sendBroadcast(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "newmsg": {//usermsg    为其它用户向你发送了消息 //TODO{"request":"newmsg","code":"7","result":"10,%20%E7%9C%8B%E8%AE%B0%E5%BD%95"}

                            try {
                                /**
                                 * code":"2",
                                 * "result":"[{\
                                 * "senderid\":7,
                                 * \"msgid\":10,
                                 * \"receiverid\":6,
                                 * \"msginfo\":\"%20%E7%9C%8B%E8%AE%B0%E5%BD%95\",
                                 * \"updatedt\":\"2019-05-26T11:24:35.083\",
                                 * \"sendtime\":\"2019-05-26T11:24:35.083\",
                                 * \"status\":\"0\"}
                                 */
                                String result = map.get("result");
                                Log.e(TAG, "newmsg:" + result);
                                if (messagedao == null)
                                    messagedao = new MessageDao(getApplicationContext());
                                if (messagelockdao == null)
                                    messagelockdao = new MesageLockDao(getApplicationContext());
                                MessageBean bean = new MessageBean();
                                String[] resultdata = result.split(",");
                                bean.setSendtime(new Date(System.currentTimeMillis()));
                                bean.setUpdatedt(new Date(System.currentTimeMillis()));
                                bean.setStatus(MessageBean.OK);
                                bean.setMsgid(Integer.parseInt(resultdata[0]));
                                bean.setMsginfo(Uri.decode(resultdata[1]));
                                bean.setSenderid(Integer.parseInt(map.get("code")));
                                bean.setReceiverid(UserInfo.getUserId());
                                messagedao.replace(bean);
                                if (!messagelockdao.Exist(bean.getMsgid())) {
                                    MessageLockBean lockBean = new MessageLockBean();
                                    lockBean.setMsgid(bean.getMsgid());
                                    lockBean.setState(MessageLockBean.NOT);
                                    messagelockdao.replace(lockBean);
                                }
                                new Notification().sendNotification(getApplicationContext(), 1, "111");
                                Intent intent = new Intent(SOCKETMSG_CHAT);
                                sendBroadcast(intent);
                                SendRequest("received " + UserInfo.getUserId() + " " + bean.getMsgid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "pullmsg": {
                            try {
                                String result = map.get("result");
                                Log.e(TAG, "pullmsg:" + result);
                                if (messagedao == null)
                                    messagedao = new MessageDao(getApplicationContext());
                                if (messagelockdao == null)
                                    messagelockdao = new MesageLockDao(getApplicationContext());
                                List<MessageBean> messageList = JSON.parseObject(result, new TypeReference<List<MessageBean>>() {
                                });
                                StringBuffer ids = new StringBuffer(" ");
                                for (MessageBean bean : messageList) {
                                    bean.setMsginfo(Uri.decode(bean.getMsginfo()));
                                    ids.append(" " + bean.getMsgid());
                                    if (bean.getReceiverid() != bean.getSenderid()) {
                                        messagedao.replacemsgid(bean);
                                        if (!messagelockdao.Exist(bean.getMsgid())) {
                                            MessageLockBean lockBean = new MessageLockBean();
                                            lockBean.setMsgid(bean.getMsgid());
                                            lockBean.setState(MessageLockBean.NOT);
                                            messagelockdao.replace(lockBean);
                                        }
                                    }
                                }
                                if (messageList.size() > 0 && !ToastUtils.isAppOnForeground(getApplicationContext()))
                                    new Notification().sendNotification(getApplicationContext(), messageList.size(), "111");
                                Intent intent = new Intent(SOCKETMSG_CHAT);
                                sendBroadcast(intent);
                                SendRequest("received " + UserInfo.getUserId() + ids);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        default:
                            break;
                    }
                } catch (
                        Exception e) {
                    ShareUtil.logstacktrace(e);
                }
            }
        });
        connector.connect();
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            cha.dispose();
            chb.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == BASESERVICEACTION) {
            Log.w(TAG, intent.getStringExtra(BASESERVICEMSG) + "");
            ToastUtils.show(context, intent.getStringExtra(BASESERVICEMSG) + "");
        }
    }
}
