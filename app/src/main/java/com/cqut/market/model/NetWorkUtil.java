package com.cqut.market.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetWorkUtil {

    private static final int TIME_OUT = 8 * 1000;                          //超时时间
    private static final String CHARSET = "utf-8";                         //编码格式
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";                        //换行

    public static void sendRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequestAddParms(String address, String key, String value, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add(key, value)
                .build();
        Request request = new okhttp3.Request.Builder().url(address).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequestAddParms(String address, HashMap<String, String> params, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        RequestBody body = builder.build();
        Request request = new okhttp3.Request.Builder().url(address).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static boolean isNetworkAvailable(Context context) {
        /*
         *@methodName:isNetworkAvailable
         *@Description:判断网络是否可用
         *@author:lixin
         *@Date:2020/10/26 11:37
         *@Param:[context]
         *@Return:boolean true表示可以
         */
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                // 当前所连接的网络可用
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * post请求方法
     */
    public static void uploadFile(String requestUrl, final Map<String, String> strParams, final Map<String, File> fileParams, HttpUrlConnectionCallBack callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(requestUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);//Post 请求不能使用缓存
                //设置请求头参数
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                /**
                 * 请求体
                 */
                //上传参数
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                //getStrParams()为一个
                dos.writeBytes(getStrParams(strParams).toString());
                dos.flush();

                //文件上传
                StringBuilder fileSb = new StringBuilder();
                for (Map.Entry<String, File> fileEntry : fileParams.entrySet()) {
                    fileSb.append(PREFIX)
                            .append(BOUNDARY)
                            .append(LINE_END)
                            /**
                             * 这里重点注意： name里面的值为服务端需要的key 只有这个key 才可以得到对应的文件
                             * filename是文件的名字，包含后缀名的 比如:abc.png
                             */
                            .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                    + fileEntry.getKey() + "\"" + LINE_END)
                            .append("Content-Type: image/jpg" + LINE_END) //此处的ContentType不同于 请求头 中Content-Type
                            .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                            .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                    dos.writeBytes(fileSb.toString());
                    dos.flush();
                    InputStream is = new FileInputStream(fileEntry.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    is.close();
                    dos.writeBytes(LINE_END);
                }
                //请求结束标志
                dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
                dos.flush();
                dos.close();
                //读取服务器返回信息
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    callback.onSuccess(response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailed(e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * 对post参数进行编码处理
     */
    private static StringBuilder getStrParams(Map<String, String> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, String> entry : strParams.entrySet()) {
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + CHARSET + LINE_END)
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END)// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append(LINE_END);
        }
        return strSb;
    }

    public interface HttpUrlConnectionCallBack {
        void onSuccess(String message);

        void onFailed(String message);
    }

}
