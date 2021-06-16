package com.cqut.market.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cqut.market.R;
import com.cqut.market.beans.User;
import com.cqut.market.model.Constant;
import com.cqut.market.presenter.LoginPresenter;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.LoginView;

public class LoginActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView {

    private EditText ed_username;
    private EditText ed_password;
    private ProgressDialog loginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideStatueBar();
        setContentView(R.layout.activity_login);
        Button bt_login = findViewById(R.id.login_button);
        TextView text_forget_password = findViewById(R.id.login_forget_password);
        TextView text_youke = findViewById(R.id.activity_login_youke);
        TextView text_register = findViewById(R.id.login_singup);
        ed_password = findViewById(R.id.login_inout_password);
        ed_username = findViewById(R.id.login_inout_userName);
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            String password = intent.getStringExtra("password");
            if (userName != null)
                ed_username.setText(userName);
            if (password != null)
                ed_password.setText(password);
        }
        bt_login.setOnClickListener(v -> {
            String username = ed_username.getText().toString().trim();
            String password = ed_password.getText().toString().trim();
            if (username.equals("")) {
                Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.equals("")) {
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            getPresenter().login(username, password);
            loginDialog = MyDialog.getProgressDialog(this, "正在登录", "请稍候...");
            loginDialog.setCancelable(false);
            loginDialog.show();
        });
        text_forget_password.setOnClickListener(v -> {
            Intent intent1 = new Intent(LoginActivity.this, FindPasswordActivity.class);
            startActivity(intent1);
        });

        text_register.setOnClickListener(v -> {
            Intent intent1 = new Intent(LoginActivity.this, SignUpActivity.class);
            intent1.putExtra("mode", 1);
            startActivity(intent1);
            finish();
        });
        text_youke.setOnClickListener(v -> {
            Intent intent1 = new Intent(LoginActivity.this, SignUpActivity.class);
            intent1.putExtra("mode", 2);
            startActivity(intent1);
            finish();
        });

    }


    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected LoginView createView() {
        return this;
    }


    @Override
    public void onLoginResult(User user, String code) {
        if (code.equals(Constant.CONNECT_FAILED)) {
            MyDialog.showToast(this, "连接异常");
            return;
        }
        if (code.equals(Constant.LOGIN_FAILED)) {
            MyDialog.showToast(this, "登录失败");
        }
        if (code.equals(Constant.CONTENT_ERROR)) {
            MyDialog.showToast(this, "密码或用户名错误");
        }
        if (loginDialog != null && loginDialog.isShowing())
            loginDialog.dismiss();
        if (code.equals(Constant.LOGIN_SUCCESS)) {
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userName", user.getUserName());
                intent.putExtra("password", user.getPassword());
                SharedPreferences sharedPreferences = this.getSharedPreferences(Constant.MY_MARKET_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constant.USER_NAME, user.getUserName());
                editor.putString(Constant.PASSWORD, user.getPassword());
                editor.putString(Constant.USER_ID, user.getId());
                editor.putLong(Constant.LAST_LOGIN_TIME, System.currentTimeMillis());
                editor.putLong(Constant.LAST_GOOD_IMAGE_UPDATE_TIME, System.currentTimeMillis());
                editor.apply();
                startActivity(intent);
                finish();
            } else {
                MyDialog.showToast(this, "登录失败，检查密码或帐号");

            }
        }
    }
}
