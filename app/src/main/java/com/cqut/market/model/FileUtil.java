package com.cqut.market.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class FileUtil {
    public static void clearCache() {
        saveData("", "goods");
        saveData("", "userInfo");
        saveData("","message");
    }

    public static void saveData(String data, String fileName) {
        File file = new File(Constant.EXTERNAL_STORAGE, fileName);
        try {
            if (!file.exists())
                file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveDataAppend(String data, String fileName) {
        File file = new File(Constant.EXTERNAL_STORAGE, fileName);
        try {
            if (!file.exists())
                file.createNewFile();
            FileWriter writer=new FileWriter(file,true);
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCacheSize(Context context) {
        try {
            long folderSize = getFolderSize(new File(context.getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR));
            long folderSize1 = getFolderSize(new File(Constant.EXTERNAL_STORAGE,"message"));
            long folderSize2 = getFolderSize(new File(Constant.EXTERNAL_STORAGE,"goods"));
            long folderSize3 = getFolderSize(new File(Constant.EXTERNAL_STORAGE,"userInfo"));
            return getFormatSize(folderSize+folderSize1+folderSize2+folderSize3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "b";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "kb";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    private static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getData(String fileName) {
        File file = new File(Constant.EXTERNAL_STORAGE, fileName);
        if (!file.exists())
            return null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            inputStream.close();
            reader.close();
            return builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveImage(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getHeadImage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MY_MARKET_NAME, Context.MODE_PRIVATE);
        String path = sharedPreferences.getString(Constant.HEADIMAGE_PATH, "null");
        return BitmapFactory.decodeFile(path);
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns
                    .DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static void clearException() {
        saveData("","uncaughtException.txt");
    }
}
