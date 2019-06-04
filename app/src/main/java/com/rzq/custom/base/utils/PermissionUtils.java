package com.rzq.custom.base.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rzq.custom.R;


/**
 * Created by Administrator on 2019/1/15.
 */

public class PermissionUtils {
    public static int REQUESTCODE = 0x123;
    private Context context;
    public static String[] permission = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private Activity activity;


    public PermissionUtils(Context mContext) {
        this.context = mContext;
    }

    public PermissionUtils(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }


    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有权限  false-表示权限已开启
     */
    public static boolean lacksPermissions(Context mContexts, String[] permissions) {
        for (String permission : permissions) {
            if (lacksPermission(mContexts, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     */
    private static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public static PermissionUtils init(Context mContext) {
        return new PermissionUtils(mContext);
    }

    public static PermissionUtils init(Activity activity) {
        return new PermissionUtils(activity);
    }

    public void showPermissions() {
        if (activity == null)
            return;
        final Dialog dialog = new android.app.AlertDialog.Builder(activity).create();
        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_permissions, null);
        dialog.show();
        dialog.setContentView(v);

        Button btn_add = (Button) v.findViewById(R.id.btn_add);
        Button btn_diss = (Button) v.findViewById(R.id.btn_diss);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                activity.startActivity(intent);
            }
        });

        btn_diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public boolean request() {
        return lacksPermissions(context, permission);
    }

    public static void onRequestPermissionsResult(Context mContext, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUESTCODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != -1) {
                    //T.showShort(mContext,"权限设置成功");
                } else {
                    //T.showShort(mContext,"拒绝权限");
                    // 权限被拒绝，弹出dialog 提示去开启权限
                    PermissionUtils.init(mContext).showPermissions();
                    break;
                }
            }
        }
    }
}
