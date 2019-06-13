package com.rzq.custom.cahtscreen;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.rzq.custom.FTP;
import com.rzq.custom.R;
import com.rzq.custom.base.RefreshView;
import com.rzq.custom.base.utils.OnNextCall;
import com.rzq.custom.base.utils.RxMianUtli;
import com.rzq.custom.base.utils.StringUtil;
import com.rzq.custom.base.utils.ToastUtils;
import com.rzq.custom.cahtscreen.db.MesageLockDao;
import com.rzq.custom.cahtscreen.db.MessageBean;
import com.rzq.custom.cahtscreen.db.MessageDao;
import com.rzq.custom.cahtscreen.db.MessageLockBean;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.List;

import io.reactivex.Observer;

public abstract class BaseChatScreenModle implements ChatScreenModle {
    protected final AcChatScreen acChatScreen;
    protected final Context mContext;
    private final MessageDao dao;
    private final FTPClient mFTPClient;

    public BaseChatScreenModle(AcChatScreen acChatScreen, Context mContext) {
        this.acChatScreen = acChatScreen;
        this.mContext = mContext;
        dao = new MessageDao(mContext);
        mFTPClient = new FTPClient();
    }

    @Override
    public void initRv(RefreshView refreshView, Context mContext) {
        refreshView.isshowNotData(false);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setReverseLayout(true);
        refreshView.setLayoutManager(manager);
    }

    /**
     * 发送给朋友消息
     * 向SOCKET 发送以下消息
     * <p>
     * sendchat Friendid  请求的IDX 我的USERID  UTF-8消息内容（支持EMOJI）
     */
    public void sendMsg(MessageBean msg) {
        try {
            msg.setMsgid(dao.querymaxMsg() + 1);
            dao.replace(msg);
            msg = dao.queryupdatedt(msg.getUpdatedt());
            Intent intent = new Intent(acChatScreen, ChatService.class);
            intent.setAction(ChatService.SOCKETMSG_CHAT);
            if (StringUtil.isnotempty(msg.getImg())) {
                String[] imgdata = msg.getImg().split("/");
                String imag = "http://27.159.82.32:909/pic/" + imgdata[imgdata.length - 1];
                intent.putExtra(ChatService.SOCKETMSG, msg.getReceiverid() + " " + msg.getIdx() + " " + msg.getSenderid() + " " + StringUtil.urlEnCode("[pic]" + imag));
                Log.e("AAA", Uri.decode(StringUtil.urlEnCode("[pic]" + imag)));
            } else
                intent.putExtra(ChatService.SOCKETMSG, msg.getReceiverid() + " " + msg.getIdx() + " " + msg.getSenderid() + " " + StringUtil.urlEnCode(msg.getMsginfo()));
            acChatScreen.startService(intent);
            getMessageList(acChatScreen.refreshView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param adapter
     * @param head
     * @param userHead
     */
    public void setHead(ChatScreenAdapter adapter, String head, String userHead) {
        adapter.setFriendHead(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo));
        adapter.setMyHead(BitmapFactory.decodeResource(mContext.getResources(), getHeadstr(userHead)));
    }

    private int getHeadstr(String o) {
            return R.drawable.logo;
    }

    /**
     * 刷新列表
     *
     * @param refreshView
     */
    public void getMessageList(final RefreshView refreshView) {
        new RxMianUtli<List<MessageBean>>() {
            @Override
            protected void initService(Observer<? super List<MessageBean>> observer) {
                try {
                    MesageLockDao mesageLockDao = new MesageLockDao(mContext);
                    List<MessageBean> chatList = dao.querychat(acChatScreen.frend.getFriendid());
                    if (chatList != null) {

                        if (refreshView.getItemList() != null) {
                            for (MessageBean bean : chatList) {
                                MessageLockBean lockb = new MessageLockBean();
                                lockb.setState(MessageLockBean.OK);
                                lockb.setMsgid(bean.getMsgid());
                                mesageLockDao.replace(lockb);
                            }
                        }

                    }

                    refreshView.setItemList(chatList);
                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onError(new Throwable(new NetworkErrorException(acChatScreen.getResources().getString(R.string.net_erro))));
                }
                observer.onComplete();
            }
        }.setOnErroCall(new OnNextCall<Throwable>() {
            @Override
            public void onNext(Throwable next) {

            }
        }).setOnCompleteCall(new OnNextCall() {
            @Override
            public void onNext(Object next) {
                refreshView.notifyDataSetChanged();
            }
        }).start();
    }

    public void pull(final MessageBean bean, final OnNextCall<Object> oqnNextCall) {
        new RxMianUtli<Object>() {
            @Override
            protected void initService(final Observer<? super Object> observer) {
                try {
                    File file = new File(bean.getImg());
                    String[] imgdata = bean.getImg().split("/");
                    //单文件上传
                    new FTP().uploadSingleFile(file, "/", new FTP.UploadProgressListener() {

                        @Override
                        public void onUploadProgress(String currentStep, long uploadSize, File file) {
                            // TODO Auto-generated method stub
                            if (currentStep.equals("ok")) {
                                oqnNextCall.onNext("");
                                Log.e("---", "ok");
                            } else if (currentStep.equals("no")) {
                                Log.e("---", "no");
                                observer.onNext("no");
                            } else if (currentStep.equals("进度")) {
                                long fize = file.length();
                                float num =  uploadSize /  fize;
                                Log.e("---------", num + "%");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                observer.onComplete();
            }
        }.setOnNextCall(new OnNextCall<Object>() {
            @Override
            public void onNext(Object next) {
                if (next instanceof String && next.equals("no")) {
                    ToastUtils.show(mContext, mContext.getResources().getString(R.string.send_no));
                }
            }
        }).

                setOnCompleteCall(new OnNextCall() {
                    @Override
                    public void onNext(Object next) {

                    }
                }).

                start();


    }
}
