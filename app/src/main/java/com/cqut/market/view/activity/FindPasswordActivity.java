package com.cqut.market.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cqut.market.R;
import com.cqut.market.model.Constant;
import com.cqut.market.model.LoginModel;
import com.cqut.market.model.Util;
import com.cqut.market.presenter.FindPasswordPresenter;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.FindPasswordView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import static com.cqut.market.view.activity.ShowMineItemActivity.CALL_PHONE;

public class FindPasswordActivity extends BaseActivity<FindPasswordView, FindPasswordPresenter> implements FindPasswordView, TextWatcher {
    private EditText phone;
    private ProgressDialog dialog;
    private LinearLayout emailLayout;
    private ImageView bt_phone;
    private EditText edit_email;
    private ImageView bt_email;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        //设置修改状态栏
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色
        window.setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        SharedPreferences preferences = getSharedPreferences(Constant.MY_MARKET_NAME, MODE_PRIVATE);
        phone = findViewById(R.id.activity_find_input_phone);
        emailLayout = findViewById(R.id.email);
        phone.addTextChangedListener(this);
        bt_phone = findViewById(R.id.activity_find_get_into_phone);
        edit_email = findViewById(R.id.activity_find_input_email);
        bt_phone.setOnClickListener(v -> {
            String trim = getPhoneNumber();
            if (LoginModel.isMobileNumber(trim)) {
                getPresenter().verificationPhone(trim);
                dialog = MyDialog.getProgressDialog(this, "正在验证", "一会就好");
                dialog.show();
            } else {
                Util.clickAnimator(phone);
                phone.setTextColor(getResources().getColor(R.color.design_default_color_error));
                Snackbar.make(phone, "号码不正确", Snackbar.LENGTH_SHORT).show();
            }
        });
        bt_email = findViewById(R.id.activity_find_get_into_email);
        bt_email.setOnClickListener(v -> {
            if (!LoginModel.isMobileNumber(getPhoneNumber())) {
                phone.setTextColor(getResources().getColor(R.color.design_default_color_error));
                Snackbar.make(phone, "号码不正确", Snackbar.LENGTH_SHORT).show();
                return;
            }
            String input = edit_email.getText().toString();
            if (input.contains("@") && input.length() > 5) {
                getPresenter().verificationEmail(input, getPhoneNumber());
                bt_email.setEnabled(false);
            }
        });


    }

    @NotNull
    private String getPhoneNumber() {
        StringBuilder trim = new StringBuilder(phone.getText().toString().trim());
        String[] s = trim.toString().split(" ");
        trim.delete(0, trim.length());
        for (String s1 : s) {
            trim.append(s1);
        }
        return trim.toString();
    }

    @Override
    protected FindPasswordPresenter createPresenter() {
        return new FindPasswordPresenter();
    }

    @Override
    protected FindPasswordActivity createView() {
        return this;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > before)
            if (s.length() == 3)
                phone.append(" ");
            else if (s.length() == 8) {
                phone.append(" ");
            } else {
                phone.setTextColor(getResources().getColor(R.color.black));
            }
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onVerification(String message) {
        runOnUiThread(() -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (message.equals(Constant.SEND_MAIL_SUCCESS)) {
                MyDialog.showToastLong(this, "我们向您的邮箱发送了一封邮件，请注意查收");
                return;
            } else if (message.equals(Constant.SEND_MAIL_FAILED)) {
                MyDialog.showToast(this, "发送失败，请在营业时间重试或者直接联系我们");
                bt_email.setEnabled(true);
                return;
            }
            if (message.equals(Constant.VERIFICATION_EMAIL_FAILURE)) {
                MyDialog.showToast(this, "邮箱地址错误");
                bt_email.setEnabled(true);
                return;
            }
            if (message.equals(Constant.VERIFICATION_PHONE_SUCCESS)) {
                AlertDialog.Builder builder = MyDialog.getDialog(this, "错误", "设备验证失败，该设备未与此手机号码绑定");
                builder.setCancelable(true);
                builder.setPositiveButton("继续验证", (dialog, which) -> {
                    bt_phone.setEnabled(false);
                    Util.aphAnim(bt_phone, 1, 0);
                    Util.transAnim(bt_phone, "translationY", 0, -50);
                    emailLayout.setVisibility(View.VISIBLE);
                    Util.aphAnim(emailLayout, 0, 1);
                    Util.transAnim(emailLayout, "translationY", 0, -50);

                });
                builder.show();

            } else if (message.equals(Constant.VERIFICATION_PHONE_FAILURE)) {
                AlertDialog.Builder builder = MyDialog.getDialog(this, "错误", "手机号码验证失败，请确保在上次登录的设备上验证\n有疑问请联系我们");
                builder.setPositiveButton("联系我们", (dialog, which) -> {
                    if (ContextCompat.checkSelfPermission(FindPasswordActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FindPasswordActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                    } else {
                        callPhone(Constant.MY_PHONE_NUMBER);
                    }
                });
                builder.setIcon(getResources().getDrawable(R.drawable.error, getTheme()));
                builder.setCancelable(true);
                builder.show();
            } else {
                AlertDialog.Builder builder = MyDialog.getDialog(this, "设备验证成功", "您的密码是:\n" + message + "\n请到个人中心修改密码");
                builder.setCancelable(true);
                builder.show();
            }
        });
    }


    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(Constant.MY_PHONE_NUMBER);
            } else {
                MyDialog.showToastLong(this, "没有权限无法拨号:" + Constant.MY_PHONE_NUMBER + ",即将打开手机qq");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.QQ_URL)));
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}