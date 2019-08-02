package com.rzq.custom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.rzq.custom.base.BaseAc;
import com.rzq.custom.base.BaseAdapter;
import com.rzq.custom.base.utils.QMUITipDialog;
import com.rzq.custom.cahtscreen.AcChatScreen;
import com.rzq.custom.cahtscreen.UserInfo;
import com.rzq.custom.cahtscreen.db.FrendBean;
import com.rzq.custom.cahtscreen.db.MesageLockDao;
import com.rzq.custom.cahtscreen.db.MessageBean;
import com.rzq.custom.cahtscreen.db.MessageDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcCustomService extends BaseAc implements CustomBeanListener<CustomBean>, View.OnClickListener {
    RecyclerView rv_listt;
    private BaseAdapter adapter;
    private QMUITipDialog dialog;


    @Override
    protected AcBean initAc() {
        return new AcBean(this, R.layout.ac_custom_service, getString(R.string.custom_call), true, false);
    }

    @Override
    protected void initBar() {
        super.initBar();
        marginTop(rl_bar);
        ib_back.setImageResource(R.drawable.back_dark);
        tv_titlle.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    protected void initViews() {
        rv_listt = findViewById(R.id.refreshView);
        rv_listt.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new CustomAdapter(mContext);
        ((CustomAdapter) adapter).setCustomBeanListener(this);
        rv_listt.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            List<CustomBean> customBeans = new ArrayList<>();

            customBeans.add(new CustomBean("客服1", "", "", 2));
            customBeans.add(new CustomBean("客服2", "", "", 4));
            customBeans.add(new CustomBean("客服3", "", "", 5));
            for (MessageBean bean : new MessageDao(this).queryAll()) {
                if (!new MesageLockDao(this).Exist(bean.getMsgid())) {
                    for (CustomBean customBean : customBeans) {
                        if (customBean.code == bean.getSenderid()) {
                            customBean.setNotnumber(customBean.getNotnumber() + 1);
                        }
                    }
                }
            }
            adapter.setItemList(customBeans);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override//item回调
    public void Call(CustomBean customBean) {
        UserInfo.setUserId(customBean.code);
        UserInfo.setUsername(customBean.name);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        if (isQQClientAvailable(mActivity)) {
//            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + customBean.qqwnum;
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            // 跳转前先判断Uri是否存在，如果打开一个不存在的Uri，App可能会崩溃
//            if (isValidIntent(mActivity, intent)) {
//                startActivity(intent);
//            }
//        } else {
//            NalertDialog nalertDialog = new NalertDialog(mActivity);
//            nalertDialog.NSetMsg("请安装手机版QQ才能发起和客服对话").show();
//        }
    }

    public boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 Uri是否有效
     */
    public boolean isValidIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }

    }
}
