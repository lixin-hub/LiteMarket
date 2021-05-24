package com.cqut.market.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.cqut.market.R;
import com.cqut.market.beans.Order;
import com.cqut.market.beans.User;
import com.cqut.market.model.Constant;
import com.cqut.market.model.FileUtil;
import com.cqut.market.model.LoginModel;
import com.cqut.market.model.NetWorkUtil;
import com.cqut.market.model.UpdateApp;
import com.cqut.market.model.Util;
import com.cqut.market.presenter.MineItemPresenter;
import com.cqut.market.view.CustomView.ApplyOrderListAdapter;
import com.cqut.market.view.CustomView.FragmentStateAdapter;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.CustomView.MyPop;
import com.cqut.market.view.CustomView.VPSwipeRefreshLayout;
import com.cqut.market.view.MineItemView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Environment.DIRECTORY_PICTURES;

public class ShowMineItemActivity extends BaseActivity<MineItemView, MineItemPresenter> implements MineItemView, SwipeRefreshLayout.OnRefreshListener {


    public static final int CALL_PHONE = 10;
    private static final int IMAGE_HEAD = 1;
    private static final int IMAGE_PROBLEM = 2;
    private static final int RESIZE_REQUEST_CODE = 3;
    private static final int PERMISION_READIMAGE = 4;
    public String userId, password;
    int transport_fee = 2;
    Timer timer = new Timer();
    private String problemMessage;
    private VPSwipeRefreshLayout vpSwipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String nickName;
    private AlertDialog changePasswordDialog;
    private ProgressDialog applyOrderDialog;
    private ProgressDialog uploadReceiveGoodDialog;
    private AlertDialog deleteAccountDialog;
    private AlertDialog setNickNameDialog;
    private AlertDialog updateUserDialog;
    private ImageView image_problem;
    private ProgressDialog uploadProblemDialog;
    private View view_dialog;
    private LinearLayout receive_good_info_parent;
    private MyPop popupWindow_recive_good;
    private ImageView image_show_order_pop;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(() -> {
                if (view_dialog != null)
                    showDialogReceiveInfo();
            });

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideStatueBar();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        sharedPreferences = getSharedPreferences(Constant.MY_MARKET_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getString(Constant.USER_ID, "-1");
        if (userId.equals("-1")) {
            Toast.makeText(this, "获取用户信息失败，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        Intent intent = getIntent();
        int item = intent.getIntExtra("item", -1);
        if (item == -1) {
            Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
            finish();
        }
        switch (item) {
            case Constant.MINE_ORDER:
                getWindow().setStatusBarColor(Color.WHITE);
                setContentView(R.layout.fragment_order_list_view_page);
                vpSwipeRefreshLayout = findViewById(R.id.fragment_mine_order_list_view_page_refresh);
                vpSwipeRefreshLayout.setOnRefreshListener(this);
                findViewById(R.id.fragment_mine_order_info_back).setOnClickListener(v -> finish());
                vpSwipeRefreshLayout.post(() -> {
                    vpSwipeRefreshLayout.setRefreshing(true);
                    getPresenter().getOrderList(userId, ShowMineItemActivity.this);
                });
                break;
            case Constant.MINE_RECIVIE_GOOD_INFO:
                setContentView(R.layout.fragment_mine_receive_goods_info);
                ArrayList<Order> orders = new ArrayList<>();
                orders.clear();
                orders.addAll(Constant.orders);
                if (orders.size() == 0)
                    finish();
                showOrdersInfo(orders);
                break;
            case Constant.MINE_RECIVIE_GOOD_EDIT_INFO:
                getWindow().setStatusBarColor(Color.WHITE);
                setContentView(R.layout.fragment_mine_receive_edit_info);
                uploadReceiveGoodInfo();
                break;
            case Constant.MINE_PERSONAL_INFO:
                setContentView(R.layout.fragment_mine_user_info);
                showPersonalInfo();
                break;
            case Constant.MINE_PROBLEM_CALLBACK:
                getWindow().setStatusBarColor(Color.WHITE);
                setContentView(R.layout.fragment_mine_problem_callback);
                handleProblemCallback();
                break;
            case Constant.MINE_ABOUT:
                getWindow().setStatusBarColor(Color.WHITE);
                String argumentUrl = Constant.HOST + "argument";
                setContentView(R.layout.fragment_mine_about);
                Button bt_update = findViewById(R.id.fragment_mine_about_check_update);
                TextView text_version = findViewById(R.id.fragment_mine_about_version);
                TextView text_user_argument = findViewById(R.id.fragment_mine_about_user_argument);
                ImageView image_back = findViewById(R.id.fragment_mine_about_back);
                image_back.setOnClickListener(v -> finish());
                text_version.setText(new UpdateApp(this).getVersionName());
                bt_update.setOnClickListener(v -> {
                    new UpdateApp(this).update();
                });
                text_user_argument.setOnClickListener(v -> {
                    View view = LayoutInflater.from(this).inflate(R.layout.web_view, null);
                    setContentView(view);
                    WebView webView = view.findViewById(R.id.web_view);
                    openWebTo(webView, argumentUrl);
                });
                break;
            case Constant.JOIN_US:
                setContentView(R.layout.web_view);
                String url = Constant.HOST + "join";
                WebView webView = findViewById(R.id.web_view);
                openWebTo(webView, url);
                break;

        }
        timer.schedule(task, 100);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    private void openWebTo(WebView webView, String url) {
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });
    }

    private void handleProblemCallback() {
        View back = findViewById(R.id.fragment_mine_problem_callback_back);
        back.setOnClickListener(v -> finish());
        View apply = findViewById(R.id.fragment_mine_problem_callback_apply);
        EditText text = findViewById(R.id.fragment_mine_problem_callback_text);
        EditText contact = findViewById(R.id.fragment_mine_problem_callback_contact);
        image_problem = findViewById(R.id.fragment_mine_problem_callback_image);
        image_problem.setOnClickListener(v -> {
            requestPermission();
        });
        apply.setOnClickListener(v -> {
            if (text.length() < 5) {
                MyDialog.showToast(this, "字数有点少额，多写点嘛！");
                return;
            }
            String path = null;
            problemMessage = "message:" + text.getText().toString() + "\n" + "联系方式:" + contact.getText().toString() + "\nuserId:" + userId;
            path = sharedPreferences.getString(Constant.PROBLEMIMAGE_PATH, null);
            if (path != null) {
                problemMessage = problemMessage + "\n图片名称:" + path.substring(path.lastIndexOf("/"));
                getPresenter().uploadProblemCallback(path, this);
            } else {
                getPresenter().uploadProblemMessage(problemMessage, this);
            }
            uploadProblemDialog = MyDialog.getProgressDialog(this, "正在上传中", "一会就好...");
            uploadProblemDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            uploadProblemDialog.show();
        });

    }

    public void requestPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISION_READIMAGE);
        } else {
            openPhoto(IMAGE_PROBLEM);
        }

    }

    private void showPopNickName() {
        View view = LayoutInflater.from(this).inflate(R.layout.fragment_mine_nickname, null);
        setNickNameDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();
        Button bt_nick_apply = view.findViewById(R.id.fragment_mine_set_nickame_apply);
        EditText ed_nickname = view.findViewById(R.id.fragment_mine_set_nickame);
        bt_nick_apply.setOnClickListener(v -> {
            String nick = ed_nickname.getText().toString();
            if (nick.equals("")) {
                MyDialog.showToast(this, "内容不能为空");
                return;
            }
            if (!nick.startsWith(" ")) {
                User user = new User();
                user.setId(userId);
                user.setNickName(nick);
                nickName = nick;
                Constant.NETWORK_INFO = NetWorkUtil.isNetworkAvailable(this);
                updateUserDialog = MyDialog.getProgressDialog(this, "提交数据", "请稍候");
                updateUserDialog.show();
                getPresenter().uploadUserInfo(user, this);
            } else {
                MyDialog.showToast(this, "昵称开头不能是空格额！");
            }
        });
        setNickNameDialog.show();
    }

    private void showPersonalInfo() {
        View re_change_password = findViewById(R.id.fragment_mine_user_info_change_password);
        View re_delete_account = findViewById(R.id.fragment_mine_user_info_deled_account);
        View re_set_nickname = findViewById(R.id.fragment_mine_user_info_set_nickname);
        View re_set_head_image = findViewById(R.id.fragment_mine_user_info_set_head_image);
        View bt_login_out = findViewById(R.id.fragment_mine_user_info_login_out);
        findViewById(R.id.fragment_mine_user_info_back).setOnClickListener(v -> finish());
        Switch switch1 = findViewById(R.id.fragment_mine_switch_vibrate);
        boolean vs = sharedPreferences.getBoolean(Constant.VIBRATORABLE, true);
        switch1.setChecked(vs);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean(Constant.VIBRATORABLE, isChecked);
            editor.apply();
        });
        bt_login_out.setOnClickListener(v -> {
            AlertDialog.Builder loginOutBulider = MyDialog.getDialog(this, "退出登录", "确定要退出登录吗?");
            loginOutBulider.setPositiveButton("确定", (dialog, which) -> {
                editor.putLong(Constant.LAST_LOGIN_TIME, 12345678);
                editor.apply();
                Intent intent = new Intent(ShowMineItemActivity.this, LunchActivity.class);
                startActivity(intent);
                FileUtil.clearCache();
                ActivityControl.finishAllActivities();
            });
            loginOutBulider.setNegativeButton("取消", null);
            AlertDialog dialog = loginOutBulider.create();
            dialog.show();

        });
        re_change_password.setOnClickListener(v -> {
            View view = LayoutInflater.from(this).inflate(R.layout.change_password, null);
            Button bt_apply = view.findViewById(R.id.change_password_apply);
            EditText ed_old = view.findViewById(R.id.change_password_old);
            EditText ed_new = view.findViewById(R.id.change_password_new);
            changePasswordDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            changePasswordDialog.show();
            bt_apply.setOnClickListener(v1 -> {
                String old = ed_old.getText().toString();
                String mNew = ed_new.getText().toString();
                if (old.length() < 6 || mNew.length() < 6) {
                    MyDialog.showToast(this, "密码长度大于6位");
                    return;
                }
                if (old.equals(mNew)) {
                    MyDialog.showToast(this, "两次密码一样");
                    return;
                }
                if (old.equals(sharedPreferences.getString(Constant.PASSWORD, "-1.0901"))) {
                    User user = new User();
                    user.setId(userId);
                    user.setPassword(mNew);
                    getPresenter().uploadUserInfo(user, this);
                    password = mNew;
                } else {
                    MyDialog.showToast(this, "密码错误");
                }
            });
        });
        re_delete_account.setOnClickListener(v -> {
            String userName = sharedPreferences.getString(Constant.USER_NAME, "-1");
            if (userName.equals("-1")) {
                MyDialog.showToast(this, "获取用户信息失败");
                return;
            }
            AlertDialog.Builder deleteAccountDialogBuilder = MyDialog.getDialog(this, "注销帐号", "帐号：" + userName + "\n" + "将会被注销,包括订单信息和评论等将会被完全清除");
            deleteAccountDialogBuilder.setNegativeButton("再考虑一下", (dialog, which) -> {

            });
            deleteAccountDialogBuilder.setPositiveButton("确定", (dialog, which) -> {
                changePassword();
            });
            deleteAccountDialog = deleteAccountDialogBuilder.create();
            deleteAccountDialog.show();
        });
        re_set_nickname.setOnClickListener(v -> {
            showPopNickName();
        });
        re_set_head_image.setOnClickListener(v -> {
            openPhoto(IMAGE_HEAD);
        });
    }

    private void openPhoto(int type) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, type);
    }

    public void changePassword() {
        View view = LayoutInflater.from(this).inflate(R.layout.change_password, null);
        Button bt_apply = view.findViewById(R.id.change_password_apply);
        TextView text_title = view.findViewById(R.id.change_password_title);
        text_title.setText("确认密码");
        EditText ed_old = view.findViewById(R.id.change_password_old);
        EditText ed_new = view.findViewById(R.id.change_password_new);
        ed_new.setVisibility(View.GONE);
        ed_old.setHint("输入密码");
        changePasswordDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        changePasswordDialog.show();
        bt_apply.setOnClickListener(v1 -> {
            String old = ed_old.getText().toString();
            String password = sharedPreferences.getString(Constant.PASSWORD, "-1");
            if (!password.equals("-1") && old != null && old.equals(password)) {
                getPresenter().deleteAccount(userId, this);
            } else {
                MyDialog.showToast(this, "请输入正确的密码");
            }
        });
    }

    private void uploadReceiveGoodInfo() {
        EditText ed_addr = findViewById(R.id.fragment_mine_receive_edit_info_addr);
        EditText ed_phone = findViewById(R.id.fragment_mine_receive_edit_info_phone);
        EditText ed_qq = findViewById(R.id.fragment_mine_receive_edit_info_qq);
        EditText ed_email = findViewById(R.id.fragment_mine_receive_edit_info_email);
        Button bt_apply = findViewById(R.id.fragment_mine_receive_edit_info_apply);
        View bt_back = findViewById(R.id.fragment_mine_receive_goods_edit_info_back);
        TextView text_remin = findViewById(R.id.fragment_mine_receive_goods_edit_info_remin);
        bt_back.setOnClickListener(v -> finish());
        String maddr = sharedPreferences.getString(Constant.ADDR, "");
        String mphone = sharedPreferences.getString(Constant.PHONE_NUMBER, "");
        String mqq = sharedPreferences.getString(Constant.QQ_NUMBER, "");
        String memail = sharedPreferences.getString(Constant.EMAIL, "");
        ed_addr.setText(maddr);
        ed_email.setText(memail);
        ed_qq.setText(mqq);
        ed_phone.setText(mphone);
        bt_apply.setOnClickListener(v -> {
            text_remin.setText("");
            String addr = ed_addr.getText().toString();
            String email = ed_email.getText().toString();
            String phone = ed_phone.getText().toString();
            String qq = ed_qq.getText().toString();
            if (addr.equals("")||addr.length()<4) {
                text_remin.setText("地址至少4个字");
                Util.clickAnimator(ed_addr);
                return;
            }
            if (!LoginModel.isMobileNumber(phone)) {
                text_remin.setText("手机号码可能不对喲！");
                Util.clickAnimator(ed_phone);
                return;
            }
            editor.putString(Constant.ADDR, addr);
            editor.putString(Constant.EMAIL, email);
            editor.putString(Constant.PHONE_NUMBER, phone);
            editor.putString(Constant.QQ_NUMBER, qq);
            editor.apply();
            User user = new User();
            user.setId(userId);
            user.setAddr(addr);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            user.setQqNumber(qq);
            editor.putString(Constant.PHONE_NUMBER, phone);
            editor.apply();
            uploadReceiveGoodDialog = MyDialog.getProgressDialog(this, "正在上传", "请稍候...");
            uploadReceiveGoodDialog.show();
            getPresenter().uploadUserInfo(user, this);
    });
}

    private void showPopAnimator(int type) {
        image_show_order_pop.setVisibility(View.VISIBLE);
        int start, end;
        if (type == 1) {//进入
            start = -image_show_order_pop.getWidth();
            end = 0;
        } else {
            start = 0;
            end = -image_show_order_pop.getWidth();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        valueAnimator.start();
        valueAnimator.addUpdateListener(animation -> {
            image_show_order_pop.setX((int) valueAnimator.getAnimatedValue());
            image_show_order_pop.invalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (type != 1)
                    image_show_order_pop.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showDialogReceiveInfo() {
        showPopAnimator(0);
        if (image_show_order_pop != null && image_show_order_pop.getVisibility() == View.VISIBLE)
            if (popupWindow_recive_good != null && popupWindow_recive_good.isShowing()) {
                return;
            }
        popupWindow_recive_good = new MyPop(view_dialog);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view_dialog.measure(w, h);
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        popupWindow_recive_good.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow_recive_good.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow_recive_good.setOutsideTouchable(false);
        popupWindow_recive_good.setFocusable(true);
        popupWindow_recive_good.setDismiss(true);
        popupWindow_recive_good.setTouchable(true);
        popupWindow_recive_good.setAnimationStyle(R.style.contextMenuAnim);
        popupWindow_recive_good.setOnDismissListener(() -> {
            showPopAnimator(1);//进入
        });
        int y = height / 2;
        int x = width / 2;
        popupWindow_recive_good.showAsDropDown(receive_good_info_parent, 0, px2dp(-height) - receive_good_info_parent.getHeight() - 80, Gravity.NO_GRAVITY);
    }

    public int px2dp(float pxValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    public int dp2px(float dpValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    private void showOrdersInfo(ArrayList<Order> orders) {
        view_dialog = LayoutInflater.from(this).inflate(R.layout.dialog_recieve_good_info, null);
        EditText ed_beizhu, ed_addr, ed_phone_number;
        CheckBox checkBox_1, checkBox_2, checkBox_3, isFetchAddr;
        TextView text_price;
        ImageView image_back;
        receive_good_info_parent = findViewById(R.id.fragment_mine_receive_goods_parent);
        image_back = findViewById(R.id.fragment_mine_receive_goods_back);
        image_show_order_pop = findViewById(R.id.fragment_mine_receive_goods_info_show_popwindow);//显示收货信xi
        image_show_order_pop.setVisibility(View.INVISIBLE);
        checkBox_1 = view_dialog.findViewById(R.id.fragment_mine_receive_goods_info_checkbox_1);
        checkBox_2 = view_dialog.findViewById(R.id.fragment_mine_receive_goods_info_checkbox_2);
        checkBox_3 = view_dialog.findViewById(R.id.fragment_mine_receive_goods_info_checkbox_3);
        isFetchAddr = view_dialog.findViewById(R.id.fragment_mine_receive_goods_is_fetch_addr);
        checkBox_1.setChecked(true);
        Button bt_order_apply = findViewById(R.id.fragment_mine_receive_goods_info_apply);
        bt_order_apply.setEnabled(true);
        text_price = findViewById(R.id.fragment_mine_receive_goods_info_price);
        ed_addr = view_dialog.findViewById(R.id.fragment_mine_receive_goods_info_address);
        ed_beizhu = view_dialog.findViewById(R.id.fragment_mine_receive_goods_info_beizu);
        ed_phone_number = view_dialog.findViewById(R.id.fragment_mine_receive_goods_info_phoneNumber);
        ed_addr.setText(sharedPreferences.getString(Constant.ADDR, ""));
        ed_phone_number.setText(sharedPreferences.getString(Constant.PHONE_NUMBER, ""));
        ed_addr.setOnClickListener(Util::clickAnimator);
        ed_phone_number.setOnClickListener(Util::clickAnimator);
        ed_beizhu.setOnClickListener(Util::clickAnimator);
        isFetchAddr.setOnClickListener(Util::clickAnimator);
        ApplyOrderListAdapter applyOrderListAdapter = new ApplyOrderListAdapter(this, orders);
        image_show_order_pop.setOnClickListener(v -> {
            showDialogReceiveInfo();
        });
        image_back.setOnClickListener(v -> finish());
        checkBox_1.setOnClickListener(v -> {
            Util.clickAnimator(v);
            checkBox_1.setChecked(true);
            checkBox_2.setChecked(false);
            checkBox_3.setChecked(false);
            transport_fee = 1;
            text_price.setText("配送费:" + transport_fee + "\nRMB:" + (calculateMoney(orders) + transport_fee));
        });
        checkBox_2.setOnClickListener(v -> {
            Util.clickAnimator(v);
            checkBox_2.setChecked(true);
            checkBox_1.setChecked(!checkBox_2.isChecked());
            checkBox_3.setChecked(false);
            transport_fee = 2;
            text_price.setText("配送费:" + transport_fee + "\nRMB:" + (calculateMoney(orders) + transport_fee));
        });
        checkBox_3.setOnClickListener(v -> {
            Util.clickAnimator(v);
            checkBox_2.setChecked(false);
            checkBox_3.setChecked(!checkBox_2.isChecked());
            checkBox_1.setChecked(!checkBox_3.isChecked());
            transport_fee = 3;
            text_price.setText("配送费:" + transport_fee + "\nRMB:" + (calculateMoney(orders) + transport_fee));
        });

        bt_order_apply.setOnClickListener(v -> {
            Util.clickAnimator(v);
            if (popupWindow_recive_good != null)
                popupWindow_recive_good.close();
            String addr = ed_addr.getText().toString();
            String beizhu = ed_beizhu.getText().toString();
            String phone = ed_phone_number.getText().toString();
            if (addr != null && !addr.equals("") && phone != null && LoginModel.isMobileNumber(phone)) {
                AlertDialog.Builder builder = MyDialog.getDialog(this, "提交订单", "请您确认以下信息：\n" +
                        "备注:" + beizhu + "\n" + "电话:" + phone + "\n" + "地址:" + addr + "\n" + "总费用:" + (calculateMoney(orders) + transport_fee));
                builder.setCancelable(false);
                builder.setIcon(R.drawable.order);
                builder.setPositiveButton("确认", (dialog, which) -> {

                    if (!addr.equals("") && !phone.equals("")) {
                        editor.putString(Constant.PHONE_NUMBER, phone);
                        editor.putString(Constant.ADDR, addr);
                        editor.apply();
                        long time = System.currentTimeMillis();
                        for (Order o : orders) {
                            for (ApplyOrderListAdapter.ViewHolder holder : applyOrderListAdapter.holders) {
                                if (holder.goodId.equals(o.getGood().getId())) {
                                    String bz = holder.edit_beizhu.getText().toString();
                                    if (bz != null && bz.length() > 0)
                                        o.setBeizhu("单独备注:" + bz + "\n" + "备注:" + beizhu + "\n" + "电话:" + phone + "\n" + "地址:" + addr + "\n" + "总费用:" + (calculateMoney(orders) + transport_fee));
                                    else
                                        o.setBeizhu("备注:" + beizhu + "\n" + "电话:" + phone + "\n" + "地址:" + addr + "\n" + "总费用:" + (calculateMoney(orders) + transport_fee));
                                    break;
                                }
                            }
                            o.setOrderTime(time);
                            o.setTransport_fee(transport_fee);
                        }
                        applyOrderDialog = MyDialog.getProgressDialog(ShowMineItemActivity.this, "正在提交订单", "请稍候");
                        applyOrderDialog.show();
                        bt_order_apply.setEnabled(false);
                        if (isFetchAddr.isChecked()) {
                            User user = new User();
                            user.setId(userId);
                            user.setAddr(addr);
                            ShowMineItemActivity.this.getPresenter().uploadUserInfo(user, this);
                        }
                        ShowMineItemActivity.this.getPresenter().applyOrders(orders, ShowMineItemActivity.this);
                    } else {
                        MyDialog.showToast(ShowMineItemActivity.this, "电话号码和地址是必需要填的喲！");
                    }
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                    showDialogReceiveInfo();
                    dialog.dismiss();
                    bt_order_apply.setEnabled(true);
                });
                AlertDialog mdialog = builder.create();
                mdialog.show();
            } else {
                MyDialog.showToast(ShowMineItemActivity.this, "电话号码和地址是必需要填正确的喲！");
            }
        });
        text_price.setText("配送费:" + transport_fee + "\nRMB:" + (calculateMoney(orders) + transport_fee));
        RecyclerView recyclerView = findViewById(R.id.fragment_mine_receive_goods_info_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(applyOrderListAdapter);
        applyOrderListAdapter.notifyDataSetChanged();
    }


    private float calculateMoney(ArrayList<Order> orders) {
        float money = 0;
        for (Order order : orders) {
            int count = order.getCount();
            money += count * order.getGood().getPrice();
        }
        BigDecimal bg = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
        return bg.floatValue();
    }

    @Override
    protected MineItemPresenter createPresenter() {
        return new MineItemPresenter();
    }

    @Override
    protected MineItemView createView() {
        return this;
    }


    @Override
    public void onCheckOrderSuccess(ArrayList<Order> orders) {
        ArrayList<Order> orders_apply = new ArrayList<>();
        ArrayList<Order> orders_processing = new ArrayList<>();
        ArrayList<Order> orders_cancel = new ArrayList<>();
        ArrayList<Order> orders_completed = new ArrayList<>();
        for (Order order : orders) {
            switch (order.getState()) {
                case STATE_APPLY:
                    orders_apply.add(order);
                    break;
                case STATE_PROCESSING:
                    orders_processing.add(order);
                    break;
                case STATE_CANCEL:
                    orders_cancel.add(order);
                    break;
                case STATE_COMPLETED:
                    orders_completed.add(order);
                    break;
            }
        }
        runOnUiThread(() -> {
            if (vpSwipeRefreshLayout != null)
                vpSwipeRefreshLayout.setRefreshing(false);
            if (orders == null) {
                MyDialog.showToast(this, "没有订单");
                return;
            }
            ArrayList<String> strings = new ArrayList<>();
            strings.add("已提交");
            strings.add("正在处理");
            strings.add("已完成");
            strings.add("已取消");
            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(new OrderFragment(orders_apply));
            fragments.add(new OrderFragment(orders_processing));
            fragments.add(new OrderFragment(orders_completed));
            fragments.add(new OrderFragment(orders_cancel));

            ViewPager2 viewPager2 = findViewById(R.id.fragment_order_list_viewpager);
            TabLayout tabLayout = findViewById(R.id.fragment_order_list_pager_tab);
            FragmentStateAdapter orderViewPagerAdapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewPager2.setAdapter(orderViewPagerAdapter);
            new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(strings.get(position))).attach();
        });
    }

    @Override
    public void onCheckOrderFailed(String message) {
        runOnUiThread(() -> {
            if (vpSwipeRefreshLayout != null) {
                vpSwipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onApplyOrders(String message) {
        runOnUiThread(() -> {
            if (!message.contains("提交成功")) {
                MyDialog.showToast(this, "提交订单失败：" + message);
            } else {
                AlertDialog.Builder builder = MyDialog.getDialog(this, "提交成功", message);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.main);
                builder.setPositiveButton("返回首页", (dialog, which) -> {
                    finish();
                });
                builder.setNeutralButton("立刻联系我们", (dialog, which) -> {
                    if (ContextCompat.checkSelfPermission(ShowMineItemActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ShowMineItemActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                    } else {
                        callPhone("15823626029");
                    }
                });
                builder.setNegativeButton("查看订单", (dialog, which) -> {
                    Intent intent = new Intent(this, ShowMineItemActivity.class);
                    intent.putExtra(Constant.USER_ID, userId);
                    intent.putExtra("item", Constant.MINE_ORDER);
                    startActivity(intent);
                    finish();
                });
                AlertDialog mdialog = builder.create();
                mdialog.show();
                NotificationManager notificationManger = Util.createNotificationManger(this, "order", "下单通知", NotificationManager.IMPORTANCE_MAX);
                Notification notification = Util.createNotification(this, "下单成功", message, "order");
                notificationManger.notify(1, notification);
            }
            if (applyOrderDialog != null && applyOrderDialog.isShowing())
                applyOrderDialog.dismiss();
        });
    }

    public void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onUploadReceiveGoodInfo(String message) {
        runOnUiThread(() -> {
            if (setNickNameDialog != null && updateUserDialog != null) {
                if (nickName != null && message.contains("成功")) {
                    editor.putString(Constant.NICK_NAME, nickName);
                    editor.apply();
                }
            }
            if (setNickNameDialog != null && setNickNameDialog.isShowing())
                setNickNameDialog.dismiss();
            if (updateUserDialog != null && updateUserDialog.isShowing())
                updateUserDialog.dismiss();

            if (changePasswordDialog != null && changePasswordDialog.isShowing()) {
                changePasswordDialog.dismiss();
                if (password != null && message.contains("成功")) {
                    editor.putString(Constant.PASSWORD, password);
                    editor.apply();
                }
            }
            if (uploadReceiveGoodDialog != null && uploadReceiveGoodDialog.isShowing())
                uploadReceiveGoodDialog.dismiss();
            MyDialog.showToast(this, message);
        });
    }

    @Override
    public void onDeleteAccount(String message) {
        runOnUiThread(() -> {
            MyDialog.showToast(this, message);
            if (message.contains("成功")) {
                editor.clear();
                editor.apply();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                ActivityControl.finishAllActivities();
            }
        });
    }

    @Override
    public void onUploadFile(String message) {
        runOnUiThread(() -> {
            if (message.contains("图片") && uploadProblemDialog != null && uploadProblemDialog.isShowing()) {
                uploadProblemDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                uploadProblemDialog.setMax(100);
                uploadProblemDialog.setProgress(70);
                editor.remove(Constant.PROBLEMIMAGE_PATH);
                editor.apply();
                if (problemMessage != null)
                    getPresenter().uploadProblemMessage(problemMessage, this);
                else uploadProblemDialog.dismiss();
            }
            if (message.contains("失败")) {
                if (uploadProblemDialog != null && uploadProblemDialog.isShowing())
                    uploadProblemDialog.dismiss();
            }
            if (message.contains("反馈") && uploadProblemDialog != null && uploadProblemDialog.isShowing()) {
                uploadProblemDialog.setProgress(100);
                uploadProblemDialog.dismiss();
            }
            MyDialog.showToast(this, message);
        });
    }


    protected void setHideStatueBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置修改状态栏
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISION_READIMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openPhoto(IMAGE_PROBLEM);
            } else {
                MyDialog.showToast(this, "没有权限");
            }
        } else if (requestCode == CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone("15823626029");
            } else {
                MyDialog.showToast(this, "没有权限，如果想联系我们请在 我的->About里面找到联系方式");
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case IMAGE_HEAD:
                    resizeImage(data.getData());
                    break;

                case RESIZE_REQUEST_CODE:
                    if (data != null) {
                        showResizeImage(data);
                    }
                    break;
                case IMAGE_PROBLEM:
                    Bitmap bitmap = null;
                    ContentResolver resolver = getContentResolver();
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);//获得图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (image_problem != null)
                        image_problem.setImageBitmap(bitmap);
                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                    String date = dateformat.format(new Date(System.currentTimeMillis()));
                    String path = getExternalFilesDir(DIRECTORY_PICTURES) + File.separator + Constant.PROBLEMIMAGE_NAME + "_" + date + ".jpg";
                    FileUtil.saveImage(bitmap, path);
                    editor.putString(Constant.PROBLEMIMAGE_PATH, path);
                    editor.apply();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪的大小
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        //设置返回码
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //裁剪之后设置保存图片的路径
            String path = getFilesDir().getPath() + File.separator + Constant.HEADIMAGE_NAME;
            editor.putString(Constant.HEADIMAGE_PATH, path);
            editor.apply();
            FileUtil.saveImage(photo, path);
        }

    }

    @Override
    public void onRefresh() {
        getPresenter().getOrderList(userId, this);
    }
}
