package com.cqut.market.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cqut.market.R;
import com.cqut.market.model.Constant;
import com.cqut.market.presenter.SignUpPresenter;
import com.cqut.market.beans.User;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.SignUpView;

import java.util.Timer;
import java.util.TimerTask;

public class SignUpActivity extends BaseActivity<SignUpView, SignUpPresenter> implements SignUpView {

    private Button bt_signup;
    private EditText ed_username;
    private EditText ed_password;
    private EditText ed_password_again;
    private EditText ed_check_code;
    private Button bt_check_code;
    private String cheekCode;
    private int counter = 50;
    private Timer timer = null;
    private AlertDialog signDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideStatueBar();
      int mode=getIntent().getIntExtra("mode",1);
      switch (mode){
          case 1://邮箱
              signupView();
              break;
          case 2://免验证
              signupYouKeView();
              break;

      }    }
    private void signupYouKeView(){
        setContentView(R.layout.activity_signup_youke);
        bt_signup = findViewById(R.id.signup_button);
        ed_password = findViewById(R.id.signup_inout_password);
        ed_username = findViewById(R.id.signup_inout_userName);
        ed_password_again = findViewById(R.id.signup_inout_password_again);
        bt_signup.setOnClickListener(v -> {
            String username = ed_username.getText().toString().trim();
            String password = ed_password.getText().toString().trim();
            String password_again = ed_password_again.getText().toString().trim();
            if (username.equals("")) {
                Toast.makeText(SignUpActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.equals("")) {
                Toast.makeText(SignUpActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password_again.equals("")) {
                Toast.makeText(SignUpActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(password_again)) {
                Toast.makeText(SignUpActivity.this, "两次密码不一样", Toast.LENGTH_SHORT).show();
                return;
            }
                AlertDialog.Builder builder=MyDialog.getDialog(this,"正在注册","请稍候");
                 signDialog=builder.create();
                 signDialog.show();
                getPresenter().signup(username, password);
        });
    }
    private void signupView(){
        setContentView(R.layout.activity_signup);
        bt_signup = findViewById(R.id.signup_button);
        ed_password = findViewById(R.id.signup_inout_password);
        ed_username = findViewById(R.id.signup_inout_userName);
        ed_password_again = findViewById(R.id.signup_inout_password_again);
        ed_check_code = findViewById(R.id.signup_inout_checkcode);
        bt_check_code = findViewById(R.id.signup_send_check_code);
        bt_check_code.setOnClickListener(v -> {

            String username = ed_username.getText().toString().trim();
            String password = ed_password.getText().toString().trim();
            String password_again = ed_password_again.getText().toString().trim();
            if (username.equals("")) {
                Toast.makeText(SignUpActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!username.contains("@") || username.length() < 10) {
                Toast.makeText(SignUpActivity.this, "您的用户名格式可能不太对", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.equals("")) {
                Toast.makeText(SignUpActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password_again.equals("")) {
                Toast.makeText(SignUpActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(password_again)) {
                Toast.makeText(SignUpActivity.this, "两次密码不一样", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "密码至少6位", Toast.LENGTH_SHORT).show();
                return;
            }
            char[] arr = String.valueOf(System.currentTimeMillis()).toCharArray();
            cheekCode = arr[arr.length - 1] + "" + arr[arr.length - 3] + "" + arr[arr.length - 2] + "" + arr[arr.length - 4];
            getPresenter().sendCheckCode(username, cheekCode);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    counter--;
                    if (counter > 0) {
                        runOnUiThread(() -> {
                            bt_check_code.setEnabled(false);
                            bt_check_code.setText(counter + "s后重新获取");
                        });

                    } else {
                        runOnUiThread(() -> {
                            bt_check_code.setEnabled(true);
                            bt_check_code.setText("重新获取");
                        });
                        timer.cancel();
                        counter = 40;
                    }
                }
            }, 0, 1000);
        });
        bt_signup.setOnClickListener(v -> {
            String username = ed_username.getText().toString().trim();
            String password = ed_password.getText().toString().trim();
            String password_again = ed_password_again.getText().toString().trim();
            String cheekCode1 = ed_check_code.getText().toString().trim();
            if (username.equals("")) {
                Toast.makeText(SignUpActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.equals("")) {
                Toast.makeText(SignUpActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password_again.equals("")) {
                Toast.makeText(SignUpActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(password_again)) {
                Toast.makeText(SignUpActivity.this, "两次密码不一样", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cheekCode1.equals("")) {
                Toast.makeText(SignUpActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cheekCode1.equals(cheekCode)) {
                AlertDialog.Builder builder=MyDialog.getDialog(this,"正在注册","请稍候");
                signDialog=builder.create();
                signDialog.show();
                getPresenter().signup(username, password);
            }else Toast.makeText(SignUpActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();

        });
    }
    @Override
    protected SignUpPresenter createPresenter() {
        return new SignUpPresenter();
    }

    @Override
    protected SignUpView createView() {
        return this;
    }


    @Override
    public void onSignResult(User user, String code) {
        if (signDialog!=null&&signDialog.isShowing())
            signDialog.dismiss();
        if (code.equals(Constant.CONNECT_FAILED)) {
            MyDialog.showToast(this, "连接异常");
            return;
        }
        if (code.equals(Constant.USER_EXISTED)) {
            MyDialog.showToast(this, "用户名已经存在");
            return;
        }
        if (code.equals(Constant.SIGNUP_SUCCESS)) {
            if (user != null) {
                MyDialog.showToast(this, "注册成功");
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.putExtra("userName", user.getUserName());
                intent.putExtra("password", user.getPassword());
                startActivity(intent);
                finish();
            } else {
                MyDialog.showToast(this, "注册失败");
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
