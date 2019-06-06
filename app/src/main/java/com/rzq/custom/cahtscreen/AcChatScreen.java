package com.rzq.custom.cahtscreen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.rzq.custom.CameraUtil;
import com.rzq.custom.FileUtil;
import com.rzq.custom.AlertdialogUtil;
import com.rzq.custom.R;
import com.rzq.custom.base.BaseAc;
import com.rzq.custom.base.BaseAction;
import com.rzq.custom.base.BaseBroadcastReciver;
import com.rzq.custom.base.BradcastHelper;
import com.rzq.custom.base.RefreshView;
import com.rzq.custom.base.utils.DateUtils;
import com.rzq.custom.base.utils.OnNextCall;
import com.rzq.custom.base.utils.PermissionUtils;
import com.rzq.custom.base.utils.StringUtil;
import com.rzq.custom.base.utils.ToastUtils;
import com.rzq.custom.cahtscreen.db.FrendBean;
import com.rzq.custom.cahtscreen.db.MessageBean;
import com.rzq.custom.cahtscreen.db.MessageDao;
import com.rzq.custom.socketClient.PopuWindowUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.rzq.custom.base.utils.PermissionUtils.REQUESTCODE;


public class AcChatScreen extends BaseAc implements OnNextCall<MessageBean>, RefreshView.LoadingListenner {
    public static final String SOCKETMSG = "com.rzq.rose.service.ChatService.SOCKETMSG";
    public static final String SOCKETMSG_CHAT = "com.rzq.rose.service.ChatService.SOCKETMSG_CHAT";
    public static final String SOCKETMSG_PULLMSG = "com.rzq.rose.service.ChatService.SOCKETMSG_PULLMSG";

    public static final int SEND_REDPACKET_CODE = 151;
    public static final int SEND_REDPACKET_CODE_ROSE = 152;
    public static final int SEND_REDPACKET_CODE_MONNEY = 153;
    public static final String CHECK_CODE_RESULT = "check_code_result";
    public static final int FRIENDVIEW = -1;
    public static final int MYVIEW = 0;
    public static final int FRIENDGIFTVIEW = 1;
    public static final int MYGIFTVIEW = 2;
    public static final String MSG_SEND_OK_RESULT = "MSG_SEND_OK_RESULT";
    private static final String TAG = "AcChatScreen";
    private FrendBean databean;
    BaseChatScreenModle chatScreenModle;
    public RefreshView refreshView;
    ImageView iv_send;
    EditText et_put;
    public FrendBean frend;
    public ChatScreenAdapter adapter;

    BradcastHelper bradcastHelper;
    /**
     * 重新发
     */
    private ChatScreenAdapter.MsgOnNextCall<MessageBean> resendCallBack = new ChatScreenAdapter.MsgOnNextCall<MessageBean>() {
        @Override
        public void onNext(final MessageBean bean, MyMsgView viewHolder) {
            try {
                bean.setStatus(MessageBean.SEND);
                chatScreenModle.sendMsg(bean);
                viewHolder.tv_resend.setVisibility(View.GONE);
                viewHolder.pb_resend.setVisibility(View.VISIBLE);
                viewHolder.initTimer(10 * 1000, 1000, new OnNextCall<Object>() {
                    @Override
                    public void onNext(Object next) {
                        MessageDao messageDao = new MessageDao(mContext);
                        if (messageDao.query(bean.getIdx() + "").getStatus() != MessageBean.OK) {
                            bean.setStatus(MessageBean.NOT);
                            messageDao.replace(bean);
                        }
                    }
                });
                viewHolder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private PendingIntent pi;
    private PopuWindowUtil popuWindowUtils;

    @Override
    protected AcBean initAc() {
        return new AcBean(this, R.layout.ac_chat_screen, "", true, false);
    }

    @Override
    protected void initBar() {
        super.initBar();
        try {
            rl_bar.setBackgroundResource(R.color.purple);
            tv_titlle.setTextColor(getResources().getColor(R.color.white));
            String datas = getIntent().getStringExtra("data");
            frend = JSON.parseObject(datas, new TypeReference<FrendBean>() {
            });
            tv_titlle.setText(frend.getComment() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initViews() {
        refreshView = findViewById(R.id.refreshView);
        chatScreenModle = new ChatScreenModleImp(this, mContext);
        chatScreenModle.initRv(refreshView, mContext);
        adapter = new ChatScreenAdapter(mContext);
        adapter.setCheckHeadCallBack(this);
        adapter.setReSendCallBack(resendCallBack);
        adapter.setItemCheck(new OnNextCall<MessageBean>() {
            @Override
            public void onNext(MessageBean next) {
                showPopu(next);

            }
        });
        adapter.setItemLongCheck(new OnNextCall<MessageBean>() {
            @Override
            public void onNext(MessageBean next) {
                try {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText(null, next.getMsginfo()));
                    ToastUtils.show(mContext, mContext.getResources().getString(R.string.copyOk));
                } catch (Exception e) {
                }
            }
        });
        refreshView.setAdapter(adapter);
        refreshView.setLoadinglistenner(this);
        bradcastHelper = new BradcastHelper(mContext, new ChatScreenReciver(this, mContext));
        List<BaseAction> list = new ArrayList<>();
        list.add(new BaseAction(SOCKETMSG_CHAT, null));
        bradcastHelper.setActions(list);
        bradcastHelper.regist();
        try {
            String datastr = getIntent().getStringExtra("data");
            databean = JSON.parseObject(datastr, new TypeReference<FrendBean>() {
            });
            tv_titlle.setText("" + databean.getComment());
            chatScreenModle.setHead(adapter, databean.getHead(), "1");
            chatScreenModle.getMessageList(refreshView);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        iv_more.setVisibility(View.VISIBLE);
        iv_send = findViewById(R.id.iv_send);
        et_put = findViewById(R.id.et_putmsg);
        iv_send.setOnClickListener(this);
        et_put.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    String msg = et_put.getText().toString();
                    if (msg == null || msg.length() == 0) {
                        ToastUtils.show(mContext, getString(R.string.putmsg));
                        return true;
                    }
                    if (msg.length() > 500) {
                        ToastUtils.show(mContext, getString(R.string.Maxmsg));
                        return true;
                    }
                    MessageBean message = new MessageBean();
                    message.setSenderid(UserInfo.getUserId());
                    message.setMsginfo(msg);
                    message.setUpdatedt(new Date(System.currentTimeMillis()));
                    message.setSendtime(new Date(System.currentTimeMillis()));
                    if (UserInfo.getUserId() == frend.getFriendid()) {
                        return true;
                    }
                    message.setReceiverid(frend.getFriendid());
                    message.setStatus(MessageBean.SEND);
                    chatScreenModle.sendMsg(message);
                    et_put.setText("");
                }
                return true;
            }
        });

    }

    private void showPopu(MessageBean next) {
        if (popuWindowUtils != null)
            popuWindowUtils.dismiss();
        popuWindowUtils = new PopuWindowUtil(mActivity, R.layout.image_show);
        PhotoView iv_img = popuWindowUtils.getRootView().findViewById(R.id.iv_img);
        try {
            if (next.getMsginfo() != null && next.getMsginfo().contains("[pic]"))
                Glide.with(mActivity).load(next.getMsginfo().replace("[pic]", "")).into(iv_img);
            else Glide.with(mActivity).load(next.getImg()).into(iv_img);
            popuWindowUtils.show(getRootView(mActivity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bradcastHelper.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_send:
                if (PermissionUtils.init(mActivity).request()) {//权限没开启
                    ActivityCompat.requestPermissions(mActivity, PermissionUtils.permission, REQUESTCODE);
                } else {
                    //权限已开启
                    new AlertDialog.Builder(this)
                            .setTitle("")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(new String[]{getString(R.string.openphoto), getString(R.string.paiPhoto)}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CameraUtil.checked(mActivity, which);
                                }
                            })
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CameraUtil.onActivityResultFile(mContext, requestCode, resultCode, data, new OnNextCall<File>() {
            @Override
            public void onNext(final File next) {
                if (next != null) {
                    DecimalFormat df = new DecimalFormat("#.0");
                    double size = Double.parseDouble(df.format((double) next.length() / 1048576));
                    if (size > 1.5) {
                        ToastUtils.show(mContext, getString(R.string.photo_max));
                        return;
                    }
                }
                if (next != null) {
                    final MessageBean message = new MessageBean();
                    message.setSenderid(UserInfo.getUserId());
                    message.setUpdatedt(new Date(System.currentTimeMillis()));
                    message.setSendtime(new Date(System.currentTimeMillis()));
                    if (UserInfo.getUserId() == frend.getFriendid()) {
                        return;
                    }
                    File file = FileUtil.setimagefile(FileUtil.getDiskBitmap(next.getPath()), FileUtil.IMAGEPATH, UserInfo.getUserId() +
                            "-" + DateUtils.getyarmmTime(new Date(System.currentTimeMillis())) + "fff.jpg");
                    message.setImg(file.getPath());
                    message.setReceiverid(frend.getFriendid());
                    message.setStatus(MessageBean.SEND);
                    chatScreenModle.pull(message, new OnNextCall<Object>() {
                        @Override
                        public void onNext(Object next) {
                            chatScreenModle.sendMsg(message);
                        }
                    });
                }
            }
        });
        if (requestCode == SEND_REDPACKET_CODE) {
            switch (resultCode) {
                case SEND_REDPACKET_CODE_ROSE: {
                    String resultData = data.getStringExtra(CHECK_CODE_RESULT);
                    analysisMsg(resultData);
                }
                break;
                case SEND_REDPACKET_CODE_MONNEY: {
                    String resultData = data.getStringExtra(CHECK_CODE_RESULT);
                }
                break;
            }
        }
    }

    /**
     * @param resultData
     */
    private void analysisMsg(String resultData) {
    }

    @Override
    public void onNext(MessageBean next) {
    }

    @Override
    public void upLoad() {

    }

    @Override
    public void downRefresh() {
        chatScreenModle.getMessageList(refreshView);
    }

    private class ChatScreenReciver extends BaseBroadcastReciver<AcChatScreen> {

        public ChatScreenReciver(AcChatScreen acChatScreen, Context context) {
            super(acChatScreen, context);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case SOCKETMSG_CHAT:
                    classf.chatScreenModle.getMessageList(classf.refreshView);
                    break;
            }
        }
    }
}

