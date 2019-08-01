package com.rzq.custom;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rzq.custom.base.BaseAc;
import com.rzq.custom.base.utils.ToastUtils;
import com.rzq.custom.cahtscreen.UserInfo;

/**
 * Created by ${tb0105} on ${DATA}.
 */
public class AcLogin extends BaseAc {
    EditText et_id;
    Button bt_login;

    @Override
    protected AcBean initAc() {
        return new AcBean(this, R.layout.ac_login, "切换客服", true, false);
    }

    @Override
    protected void initBar() {
        super.initBar();
    }

    @Override
    protected void initViews() {
        et_id = findViewById(R.id.et_id);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                if (id.length() == 0) {
                    ToastUtils.show(getApplicationContext(), "请输入ID");
                    return;
                }
                UserInfo.setUserId(Integer.parseInt(id));
                startAc(MainActivity.class);
                finish();
            }
        });
    }
}
