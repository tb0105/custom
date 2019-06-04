package com.rzq.custom;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.rzq.custom.base.utils.OnNextCall;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class CameraUtil {
    private static final int REQUEST_CODE_IMAGE_CAMERA = 121;
    private static final int REQUEST_CODE_IMAGE_OP = 122;
    private static final String TAG = "CameraUtil";
    private static final int REQUEST_CODE_OP = 123;
    private static Uri uri;

    public static void checked(Activity activity, int which) {
        switch (which) {
            case 1:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
                break;
            case 0:
                Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
                getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
                getImageByalbum.setType("image/jpeg");
                activity.startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
                break;
            default:
                ;
        }

    }

    /**
     * 取得返回图片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(Context mContext, int requestCode, int resultCode, Intent data, OnNextCall<Bitmap> onNextCall) {
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            try {
                Uri mPath = uri;
                String file = ImageUtils.getPath(mContext, mPath);
                int lenth = file.length();
                Bitmap bmp = ImageUtils.decodeImage(file);
                if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
                    Log.e(TAG, "error");
                } else {
                    Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
                }
                onNextCall.onNext(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            try {
                Uri mPath = data.getData();
                String file = ImageUtils.getPath(mContext, mPath);
                int lenth = file.length();
                Bitmap bmp = ImageUtils.decodeImage(file);
                onNextCall.onNext(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_OP) {
            try {
                Log.i(TAG, "RESULT =" + resultCode);
                if (data == null) {
                    return;
                }
                Bundle bundle = data.getExtras();
                String path = bundle.getString("imagePath");
                Log.i(TAG, "path=" + path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取得返回图片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResultFile(Context mContext, int requestCode, int resultCode, Intent data, OnNextCall<File> onNextCall) {
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            try {
                Uri mPath = uri;
                String file = ImageUtils.getPath(mContext, mPath);
                Bitmap bmp = ImageUtils.decodeImage(file);
                if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
                    Log.e(TAG, "error");
                } else {
                    Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
                }
                onNextCall.onNext(new File(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            try {
                Uri mPath = data.getData();
                String file = ImageUtils.getPath(mContext, mPath);
                Bitmap bmp = ImageUtils.decodeImage(file);
                onNextCall.onNext(new File(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_OP) {
            try {
                Log.i(TAG, "RESULT =" + resultCode);
                if (data == null) {
                    return;
                }
                Bundle bundle = data.getExtras();
                String path = bundle.getString("imagePath");
                Log.i(TAG, "path=" + path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
