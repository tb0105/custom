package com.rzq.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件工具类
 *
 * @author fengjian
 */
public class FileUtil {
    private String file = Environment.getExternalStorageDirectory() + "/beike/temp.jpg";
    public static final String IMAGEPATH = Environment.getExternalStorageDirectory() + "/upload/image/";
    private static String rootPath = null;
    private static String imagePath = null;
    private static String logPath = null;

    /**
     * 获取该应用缓存的根目录路径
     *
     * @return 缓存的根目录路径
     */
    public static String getCachePath() {
        if (rootPath == null) {
        }
        return rootPath;
    }

    /**
     * 获取该应用图片缓存目录
     *
     * @return 图片缓存目录
     */
    public static String getImageRootPath() {
        if (imagePath == null) {
            imagePath = getCachePath() + File.separator + "image";
        }
        return imagePath;
    }

    /**
     * 获取该应用日志缓存目录
     *
     * @return 日志缓存目录
     */
    public static String getLogRootPath() {
        if (logPath == null) {
            logPath = getCachePath() + File.separator + "log";
        }
        return logPath;
    }

    /**
     * 判断指定路劲的文件是否存在
     *
     * @param path 文件路劲
     * @return 存在返回true;否则返回false
     */
    public static boolean isExistFile(String path) {
        File file = new File(path);
        boolean exists = file.exists();
        return exists;
    }

    /**
     * 获取储存根目录
     *
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        } else {
        }
        return sdDir.toString();
    }

    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public static List getExtSDCardPath() {
        List lResult = new ArrayList();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    /**
     * 创建文件
     *
     * @param file        原文件
     * @param isDeleteOld 是否删除原文件
     * @return 创建后的文件
     */
    public static File createNewFile(File file, boolean isDeleteOld) {
        try {
            if (file.exists() && isDeleteOld) {
                file.delete();
                file.createNewFile();
            } else {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 读取文件内容
     *
     * @param path 文件路劲
     * @return 文件内容
     */
    public static String read(String path) {
        BufferedReader br = null;
        String line = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    path), "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
        }
        return sb.toString().trim();
    }

    /**
     * 根据路径获取父文件
     *
     * @param path 文件路径
     * @return 父文件
     */
    public static File getParentFile(String path) {
        File file = new File(path);
        return file.getParentFile();
    }

    /**
     * 向文件写入内容
     *
     * @param content            要写入的内容
     * @param path               文件路径
     * @param append             是否以追加的方式写入
     * @param deleteOldWhenExist 当指定文件存在时是否先进行删除
     * @return 返回是否写入成功
     */
    public static boolean write(String content, String path, boolean append,
                                boolean deleteOldWhenExist) {
        return write(content, new File(path), append, deleteOldWhenExist);
    }

    /**
     * 判断是否挂载SD卡
     *
     * @return 是否挂载的布尔值结果
     */
    public static boolean isExistSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 是否为只读状态
     *
     * @return 是否只读
     */
    public static boolean isReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 判断SD卡是否可写入
     *
     * @return 是否可写
     */
    public static boolean isCanWrite() {
        return isExistSDCard() && !isReadOnly();
    }

    /**
     * 向文件写入内容
     *
     * @param content            要写入的内容
     * @param file               文件路径
     * @param append             是否以追加的方式写入
     * @param deleteOldWhenExist 当指定文件存在时是否先进行删除
     * @return 返回是否写入成功
     */
    public static boolean write(String content, File file, boolean append,
                                boolean deleteOldWhenExist) {
        if (!isCanWrite()) {
            return false;
        }
        file = createNewFile(file, deleteOldWhenExist);
        if (file == null) {
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                bw = null;
            }
        }
        return false;
    }

    //文件大小
    public static String GetFileSize(File file) {
        String size = "";
        if (file.exists() && file.isFile()) {
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) + "GB";
            }
        } else if (file.exists() && file.isDirectory()) {
            size = "";
        } else {
            size = "0BT";
        }
        return size;
    }

    /**
     * 清除缓存文件
     *
     * @param path         文件路径
     * @param isDelRootDir 是否删除根文件
     * @return 全部删除成功返回true，否则返回false
     */
    public static boolean clearCache(String path, boolean isDelRootDir) {
        return clearCache(new File(path), isDelRootDir);
    }

    /**
     * 清除缓存文件
     *
     * @param file         缓存文件目录
     * @param isDelRootDir 是否删除根文件
     * @return 全部删除成功返回true，否则返回false
     */
    public static boolean clearCache(File file, boolean isDelRootDir) {
        boolean isSuccess = true;
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (!f.delete()) {
                        isSuccess = false;
                    }
                }
            }
            if (isDelRootDir) {
                file.delete();
            }
        }
        return isSuccess;
    }

    /**
     * 删除知道路径的文件
     *
     * @return 删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(String SDPATH) {
        try {
            File dir = new File(SDPATH);
            if (dir == null || !dir.exists()) {
                return true;
            }
            if (!dir.isDirectory()) {
                String tmpPath = dir.getParent() + File.separator + System.currentTimeMillis();
                File tmp = new File(tmpPath);
                dir.renameTo(tmp);
                tmp.delete();
                return true;
            }
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
                    File tmp = new File(tmpPath);
                    file.renameTo(tmp);
                    tmp.delete();
                } else if (file.isDirectory())
                    deleteFile(file.getPath()); // 递规的方式删除文件夹
            }
            String tmpPath = dir.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            dir.renameTo(tmp);
            tmp.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取缓存目录占用空间大小
     *
     * @param file 文件目录
     * @return 文件大小字符串(含单位 ： B / K / M / G)
     */
    public static String getCacheSize(File file) {
        return formatFileSize(getFileSize(file));
    }

    /**
     * 获取缓存目录占用空间大小
     *
     * @param path 目录路径
     * @return 文件大小字符串(含单位 ： B / K / M / G)
     */
    public static String getCacheSize(String path) {
        return getCacheSize(new File(path));
    }

    /**
     * 获取缓存目录占用空间大小
     *
     * @param file 文件目录
     * @return 文件大小(字节数)
     */
    private static long getFileSize(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        long size = 0;
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size = size + getFileSize(files[i]);
            } else {
                size = size + files[i].length();
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 文件大小字节数
     * @return 文件大小字符串(含单位 ： B / K / M / G)
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileSize == 0) {
            return "0B";
        }
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static Bitmap getDiskBitmap(String pathString) {
        if (pathString == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(pathString);
            if (is != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Drawable getDiskDrawable(String pathString) {
        if (pathString == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        FileInputStream is = null;
        Drawable drawable = null;
        try {
            is = new FileInputStream(pathString);
            if (is != null) {
                drawable = Drawable.createFromStream(is, "image");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    public static String copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File fi = new File(newPath);
            if (!fi.getParentFile().exists()) { // 如果不存在就创建
                fi.getParentFile().mkdirs(); // 创建文件夹和文件
            }
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                buffer = null;
                inStream.close();
                return newPath;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
        return "";
    }

    /**
     * 读取
     *
     * @param name
     * @return
     */
    public static String load(String name) {
        String text = null;
        if (name == null) {
            return "000000";
        }
        File file = new File(name);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] b = new byte[fileInputStream.available()];
                fileInputStream.read(b);
                text = new String(b);
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return text;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("读文件出错");
            }
        }
        return "000000";
    }

    public static boolean save(String name, String list) {
        File file = new File(name);
        if (!file.getParentFile().exists()) { // 如果不存在就创建
            file.getParentFile().mkdirs(); // 创建文件夹和文件
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(list.getBytes());
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * //保存数据到文件
     *
     * @param fileName
     * @return
     */

    public static boolean saveDataToSDcard(String fileName, String content) {
        if (content == null)
            return false;
        boolean isAvailable = false;    //SD是否可读
        FileOutputStream fileOutputStream = null;
        //创建File对象
        File fi = new File(fileName);
        if (!fi.getParentFile().exists()) { // 如果不存在就创建
            fi.getParentFile().mkdirs(); // 创建文件夹和文件
        }
        File file = new File(fileName);

        //判断SD卡是否可读写
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            isAvailable = true;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(content.getBytes());
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return isAvailable;
    }

    public static File setimagefile(Bitmap bmp, String path, String fileName) {

        if (bmp == null) {
            return null;
        }
        File imgfile = new File(path + fileName);
        FileUtil.createNewFile(imgfile, false);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgfile));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            if (bos != null) {
                bos.flush();
                bos.close();
            }
            return imgfile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveDataToSDcard_string(String fileName, String list) {
        boolean isAvailable = false;    //SD是否可读
        FileOutputStream fileOutputStream = null;
        //创建File对象
        File fi = new File(fileName);
        if (!fi.getParentFile().exists()) { // 如果不存在就创建
            fi.getParentFile().mkdirs(); // 创建文件夹和文件
        }
        File file = new File(fileName);
        //判断SD卡是否可读写
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(list.getBytes());
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                isAvailable = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return isAvailable;
    }

    public static boolean setinmagefile(byte[] bmp, String path, String fileName) {
        if (bmp == null || bmp.length == 0)
            return false;
        File file = new File(path + fileName);
        if (file.exists()) {
            return true;
        } else {
            file.getParentFile().mkdirs(); // 创建文件夹和文件
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bmp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("存储出错", e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 读取文件内容，并将String转成List<>
     *
     * @param fileName
     * @return
     */
    public static Map<String, String> getDataFromSDcard(String fileName) {
        //读取文件内容保存到resultStr
        String resultStr = null;
        Map<String, String> map = new HashMap<String, String>();
        File file = new File(fileName);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] b = new byte[fileInputStream.available()];
                fileInputStream.read(b);
                resultStr = new String(b);
                b = null;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("读文件出错");
                return null;
            }
            try {

                JSONObject json = new JSONObject(resultStr);
                Iterator<String> it = json.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    map.put(key, json.getString(key));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    //drawrable转bimap
    public static Bitmap drawableToBitmap(Drawable drawable) {


        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }


}
