package com.rzq.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class ShareUtil {
    public static void ShowToast(Context mContext, CharSequence msg) {
        Toast toast = Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toast(Context context,CharSequence msg)
    {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toast(Context context,int msgid)
    {
        Toast toast = Toast.makeText(context, context.getString(msgid), Toast.LENGTH_SHORT);
        toast.show();
    }

    public static String formartString(Context context,int id,Object... args)
    {
        String sAgeFormat1 = context.getResources().getString(id);
        return String.format(sAgeFormat1,args);
    }


    public static String decompress(String zipText) throws IOException {
        byte[] compressed = Base64.decode(zipText,Base64.DEFAULT);
        if (compressed.length > 4)
        {
            GZIPInputStream gzipInputStream = new GZIPInputStream(
                    new ByteArrayInputStream(compressed, 0,
                            compressed.length ));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int value = 0; value != -1;) {
                value = gzipInputStream.read();
                if (value != -1) {
                    baos.write(value);
                }
            }
            gzipInputStream.close();
            baos.close();
            String sReturn = new String(baos.toByteArray(), "UTF-8");
            return sReturn;
        }
        else
        {
            return "";
        }
    }

    public static Date stringToDate(String str){
        Date rcreate=new Date();
        java.text.SimpleDateFormat newstr=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            rcreate=newstr.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rcreate;
    }

    public static void logstacktrace(Throwable ex)
    {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        Log.e("DEBUG", writer.toString());
    }

    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }


    public static void SetPerfenceInfo(Context context,String idx,String value){

        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("Config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(idx,value);
        editor.commit();
    }
    public  static String GetPerfenceInfo(Context context,String idx){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("Config", Context.MODE_PRIVATE);
        return sp.getString(idx, "");
    }

    public static File getFile(Context context,String dic,String filename)
    {
       File file= new File(context.getFilesDir(),dic);
       if(!file.exists())
           file.mkdir();
        return new File(file,filename);
    }

    public static File getDir(Context context,String dic)
    {
        return new File(context.getFilesDir(),dic);
    }

    public static void down_file(String url, File file, Handler handler) throws IOException {
        String path=file.getPath()+".tmp";
        //获取文件名
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        int fileSize = conn.getContentLength();//根据响应获取文件大小
        if (file.exists() )
        {
            if(file.length() == fileSize)
            {
                Log.e("DEBUG","EXISTS FILE");
                return;
            }
        }


        if (fileSize <= 0) throw new RuntimeException("Unknown File size");
        if (is == null) throw new RuntimeException("stream is null");
        FileOutputStream fos = new FileOutputStream(path);
        //把数据存入路径+文件名
        byte buf[] = new byte[1024];
        int downLoadFileSize = 0;
        do
        {
            //循环读取
            int numread = is.read(buf);
            if (numread == -1)
            {
                break;
            }
            fos.write(buf, 0, numread);
            downLoadFileSize += numread;
            if(handler != null)
            {
                Message msg= handler.obtainMessage();
                msg.what=-8888;
                msg.obj= (new DecimalFormat("0.00").format (downLoadFileSize  * 100 / (float)fileSize))+"%";
                handler.sendMessage(msg);
            }
        } while (true);

        try
        {
            fos.close();
            is.close();
        } catch (Exception ex)
        {
            ShareUtil.logstacktrace(ex);
        }
        File newfile=new File(path);
        if(newfile.length() !=fileSize)
        {
            return;
        }

        if (file.exists())
        {
            if(file.length()==fileSize)
            {
                newfile.delete();
                return;
            }
            else
                file.delete();
        }
        newfile.renameTo(file);
    }

    public static String getDeviceId(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String ReadTxtFile(File file)
    {
        String content = ""; //文件内容字符串

        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    private final static int kSystemRootStateUnknow=-1;
    private final static int kSystemRootStateDisable=0;
    private final static int kSystemRootStateEnable=1;
    private static int systemRootState=kSystemRootStateUnknow;

    public static boolean isRootSystem()
    {
        if(systemRootState==kSystemRootStateEnable)
        {
            return true;
        }
        else if(systemRootState==kSystemRootStateDisable)
        {

            return false;
        }
        File f=null;
        final String kSuSearchPaths[]={"/system/bin/","/system/xbin/","/system/sbin/","/sbin/","/vendor/bin/"};
        try{
            for(int i=0;i<kSuSearchPaths.length;i++)
            {
                f=new File(kSuSearchPaths[i]+"su");
                if(f!=null&&f.exists())
                {
                    systemRootState=kSystemRootStateEnable;
                    return true;
                }
            }
        }catch(Exception e)
        {
            ShareUtil.logstacktrace(e);
        }
        systemRootState=kSystemRootStateDisable;
        return false;
    }
}
