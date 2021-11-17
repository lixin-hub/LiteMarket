package com.cqut.market.model;

import com.cqut.market.view.FindPasswordView;
import com.cqut.market.view.activity.FindPasswordActivity;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FindPasswordModel {
    private static final String mail = Constant.HOST + "mail?action=verify_password&UserMail=";
    private final String url = Constant.HOST + "login";

    public void verification(String phone, FindPasswordView view) {
        String uuid = new DeviceId(((FindPasswordActivity) view)).getDeviceUuid().toString();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("verification_phone", phone);
        hashMap.put("uuid", uuid);
        NetWorkUtil.sendRequestAddParms(url, hashMap, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                view.onVerification(Constant.VERIFICATION_PHONE_FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                String responseCode = JsonUtil.getResponseCode(string);
                if (responseCode == null || responseCode.equals(Constant.VERIFICATION_PHONE_FAILURE)) {
                    view.onVerification(Constant.VERIFICATION_PHONE_FAILURE);
                    return;
                }
                if (responseCode.equals(Constant.VERIFICATION_PHONE_SUCCESS)) {
                    view.onVerification(Constant.VERIFICATION_PHONE_SUCCESS);
                    return;
                }
                if (responseCode.equals(Constant.VERIFICATION_PHONE_AND_UUID_SUCCESS)) {
                    String password = Document.parse(string).getString("password");
                    view.onVerification(password);
                } else view.onVerification(Constant.VERIFICATION_PHONE_FAILURE);
            }
        });
    }

    public void verificationEmail(String email, String phone, FindPasswordView view) {
        NetWorkUtil.sendRequestAddParms(mail + email, "phoneNumber", phone, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                view.onVerification(Constant.SEND_MAIL_FAILED);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                String responseCode = JsonUtil.getResponseCode(string);
                if (responseCode != null) {
                    if (responseCode.equals(Constant.SEND_MAIL_SUCCESS))
                        view.onVerification(Constant.SEND_MAIL_SUCCESS);
                    else if (responseCode.equals(Constant.VERIFICATION_EMAIL_FAILURE))
                        view.onVerification(Constant.VERIFICATION_EMAIL_FAILURE);
                    else view.onVerification(Constant.SEND_MAIL_FAILED);
                } else view.onVerification(Constant.SEND_MAIL_FAILED);
            }
        });
    }
}
