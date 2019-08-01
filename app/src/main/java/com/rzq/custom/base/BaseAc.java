package com.rzq.custom.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rzq.custom.R;
import com.rzq.custom.base.utils.ImmersedStatusbarUtils;


public abstract class BaseAc extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BaseAc";
    private AcBean acBean;
    protected Context mContext;
    protected Activity mActivity;
    protected View rl_bar;
    protected TextView tv_titlle;
    protected TextView tv_more;
    protected ImageButton ib_back;

    protected abstract AcBean initAc();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.acBean = initAc();
        setContentView(acBean.layout);
        mActivity = acBean.activity;
        mContext = mActivity.getApplicationContext();
        initBar();
        initViews();
    }

    protected void initBar() {
        try {
            if (acBean.bar) {
                rl_bar = findViewById(R.id.rl_bar);
                tv_titlle = findViewById(R.id.tv_title);
                ib_back = findViewById(R.id.ib_back);
                tv_more=findViewById(R.id.tv_more);
                marginTop(rl_bar);
                tv_titlle.setText(acBean.title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void initViews() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            onBackPressed();
        }
    }

    private long oldTime;

    @Override
    public void onBackPressed() {
        if (acBean.twofh) {
            if ((System.currentTimeMillis() - 2 * 1000) < oldTime) {
                mActivity.finish();
            } else {
                Toast.makeText(mContext, "再按一次退出", Toast.LENGTH_SHORT).show();
                oldTime = System.currentTimeMillis();
            }
        } else
            super.onBackPressed();
    }

    protected void marginTop(View view) {
        ImmersedStatusbarUtils.initAfterSetContentView(mActivity, view);
    }

    public View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public void startAc(Class clas) {
        startActivity(new Intent(mActivity, clas));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public class AcBean {
        public AcBean(Activity activity, int layout, String title, boolean bar, boolean twofh) {
            this.activity = activity;
            this.layout = layout;
            this.title = title;
            this.bar = bar;
            this.twofh = twofh;
        }

        public Activity activity;
        public int layout;
        public String title;
        public boolean bar;
        public boolean twofh;
    }
}
