package com.cqut.market.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.cqut.market.R;
import com.cqut.market.view.CustomView.MyDialog;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateApp {
    private final Activity context;
    private final ProgressDialog progressDialog;
    private final String path;
    String fileUrl = Constant.HOST + "file";
    private File appFile;
    private final FileListener listener = new FileListener() {
        @Override
        public void onSuccess(File file) {
            context.runOnUiThread(() -> {
                progressDialog.setTitle("下载完成，即将安装");
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                installApp(file);
            });

        }

        @Override
        public void onProgress(int progress) {
            context.runOnUiThread(() -> progressDialog.setProgress(progress));
        }

        @Override
        public void onFailed() {
            appFile = new File(path, "LiteMarket.apk");
            if (appFile.exists())
                appFile.delete();
            context.runOnUiThread(() -> {
                progressDialog.setTitle("下载失败");
                progressDialog.setCancelable(true);
            });
        }
    };
    private final CheckVisionListener checkVisionListener = new CheckVisionListener() {
        @Override
        public void onSuccess(int code, String content, Long date) {
            if (code != 0) {
                if (code > getVersionCode()) {
                    context.runOnUiThread(() -> {
                        AlertDialog.Builder dialog = MyDialog.getDialog(context, "版本更新", content);
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("立即更新", (dialog12, which) -> {
                            progressDialog.show();
                            downloadApp();
                        });
                        dialog.setNegativeButton("下次一定", (dialog1, which) -> {
                            MyDialog.getDialog(context, "不愧是你", "这点流量都舍不得。。。")
                                    .setCancelable(true)
                                    .setIcon(R.drawable.about)
                                    .create().show();
                        });
                        dialog.show();
                    });
                } else {
                    MyDialog.showToast(context, "当前为最新版本");
                }
            }
        }
    };

    public UpdateApp(Activity context) {
        this.context = context;
        path = context.getExternalCacheDir().getAbsolutePath();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("文件下载");
    }

    public void update() {
        checkUpdate();
    }


    private void installApp(File file) {
        String authority = "com.cqut.market.fileprovider";
        Uri apkUri = FileProvider.getUriForFile(context, authority, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        context.startActivity(intent);
        context.finish();
//        Process.killProcess(android.os.Process.myPid());
    }

    private void downloadApp() {
        NetWorkUtil.sendRequestAddParms(fileUrl, "action", "action_update", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onFailed();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                appFile = new File(path, "LiteMarket.apk");
                if (appFile.exists())
                    appFile.delete();
                else {
                    appFile.createNewFile();
                }
                InputStream is = null;
                FileOutputStream fos = null;
                byte[] buff = new byte[2048];
                int len;
                Message message = new Message();
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(appFile);
                    long total = response.body().contentLength();
                    long sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中，更新下载进度
                        listener.onProgress(progress);
                    }
                    fos.flush();
                    //4.下载完成，安装apk
                    listener.onSuccess(appFile);
                } catch (Exception e) {
                    Log.e("Expr=etion", e.getMessage() + "\n");
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void checkUpdate() {
        NetWorkUtil.sendRequestAddParms(fileUrl, "action", "checkUpdate", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                checkVisionListener.onSuccess(0, null, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonStr = response.body().string();
                Document document = org.bson.Document.parse(jsonStr);
                if (document != null) {
                    Integer version = document.getInteger("version");
                    String content = document.getString("content");
                    Long date = document.getLong("date");
                    checkVisionListener.onSuccess(version, content, date);
                }
            }
        });
    }

    private int getVersionCode() {
        int version = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public String getVersionName() {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface FileListener {
        void onSuccess(File file);

        void onProgress(int progress);

        void onFailed();
    }

    public interface CheckVisionListener {
        void onSuccess(int code, String content, Long date);
    }

}
