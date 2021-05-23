package com.cqut.market.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cqut.market.R;
import com.cqut.market.beans.Message;
import com.cqut.market.model.Constant;
import com.cqut.market.presenter.MessagePresenter;
import com.cqut.market.view.CustomView.MessageAdapter;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.MessageView;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends BaseActivity<MessageView, MessagePresenter> implements MessageView {
    private static final int CALL_PHONE = 10;
    private final ArrayList<Message> messageQueue = new ArrayList<>();
    private String userId;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LinearLayout keybord_visibility, more_visibility;
    private RecyclerView messageRecycler;
    private MessageAdapter messageAdapter;
    private ImageView keybord_image, more_image;
    private Button bt_send;
    private String headImagePath;
    private EditText ed_content;
    private ProgressDialog progressDialog;
    private TextView contact_us, about,new_activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            Window window = getWindow();
            //设置修改状态栏
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色F
            getWindow().setStatusBarColor(Color.parseColor("#C4D1D5"));
            setContentView(R.layout.activity_message);
            Toolbar toolbar = findViewById(R.id.activity_message_toolbar);
            setSupportActionBar(toolbar);
            preferences = getSharedPreferences(Constant.MY_MARKET_NAME, MODE_PRIVATE);
            editor = preferences.edit();
            headImagePath = preferences.getString(Constant.HEADIMAGE_PATH, null);
            userId = preferences.getString(Constant.USER_ID, "-1");
            keybord_visibility = findViewById(R.id.activity_message_navigation_keybord_visibility);
            more_image = findViewById(R.id.activity_message_go_to_more);
            keybord_image = findViewById(R.id.activity_message_go_to_keybord);
            more_visibility = findViewById(R.id.activity_message_navigation_more_visibility);
            messageRecycler = findViewById(R.id.message_recycler);
            bt_send = findViewById(R.id.message_bt_send);
            ed_content = findViewById(R.id.message_input);
            contact_us = findViewById(R.id.activity_message_more_contact_us);
            about = findViewById(R.id.activity_message_navigation_about);
            new_activity=findViewById(R.id.activity_message_navigation_new_activity);
            findViewById(R.id.activity_message_back).setOnClickListener((v) -> finish());
            messageAdapter = new MessageAdapter(this, messageQueue);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            linearLayoutManager.setInitialPrefetchItemCount(1);
            BottomOffsetDecoration bottomOffsetDecoration = new BottomOffsetDecoration((int) 100);
            messageRecycler.addItemDecoration(bottomOffsetDecoration);
            messageRecycler.setLayoutManager(linearLayoutManager);
            messageRecycler.setAdapter(messageAdapter);
            progressDialog = MyDialog.getProgressDialog(this, "Loading", "我在玩命加载啦~");
            progressDialog.show();
            more_image.setOnClickListener((v) -> {
                if (ed_content.hasFocus()) {
                    showKeyboard(false);
                }
                navigationAnimator(Constant.ANIMATOR_OUT, keybord_visibility);
                navigationAnimator(Constant.ANIMATOR_IN, more_visibility);
            });
            keybord_image.setOnClickListener((v) -> {
                navigationAnimator(Constant.ANIMATOR_OUT, more_visibility);
                navigationAnimator(Constant.ANIMATOR_IN, keybord_visibility);
            });
            bt_send.setOnClickListener(v -> {
                String content = ed_content.getText().toString();
                String userName = preferences.getString(Constant.USER_NAME, "");
                if (content.length() == 0) return;
                Message message = new Message.Builder()
                        .setContent(content)
                        .setDate(System.currentTimeMillis())
                        .setFrom(userName)
                        .setUserId(userId)
                        .setTitle("normaInput")
                        .setImagePath(headImagePath)
                        .setMessageType(Message.TYPE_SEND)
                        .setSendTo("LiteMarket")
                        .build();
                getPresenter().sendMessage(message, this);
                ed_content.setText("");
                messageRecycler.smoothScrollToPosition(messageQueue.size() - 1);
            });
            contact_us.setOnClickListener((v) -> {
                showContactPopMenu(v);
            });
            about.setOnClickListener(v -> {
                showAboutPopMenu(v);
            });
            new_activity.setOnClickListener((v -> {
                getPresenter().requestMessage(Constant.GET_MESSAGE_NEW_ACTIVITY,userId,this);
            }));
        }//init
        getPresenter().requestMessage(Constant.GET_MESSAGE_NEW, userId, this);
    }

    public void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //当前获取到焦点的view
        View currentFocus = getCurrentFocus();
        if (null == imm)
            return;
        if (isShow) {
            if (currentFocus != null) {
                //有焦点打开
                imm.showSoftInput(currentFocus, 0);
            } else {
                //无焦点打开
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } else {
            if (currentFocus != null) {
                //有焦点关闭
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                //无焦点关闭
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }

    private void showAboutPopMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.about_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.join_us:
                    Intent intent1 = new Intent(this, ShowMineItemActivity.class);
                    intent1.putExtra("item", Constant.JOIN_US);
                    startActivity(intent1);
                    break;
                case R.id.advice:
                    Intent intent = new Intent(this, ShowMineItemActivity.class);
                    intent.putExtra("item", Constant.MINE_PROBLEM_CALLBACK);
                    startActivity(intent);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showContactPopMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.contact_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.phone:
                    if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                    } else {
                        callPhone("15823626029");
                    }
                    break;
                case R.id.qq:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.QQ_URL)));
                    break;
                case R.id.qq_group:
                    joinQQGroup(Constant.QQ_KEY);
                    break;
                case R.id.weixin:
                    getWechatApi();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    private void getWechatApi() {
        ClipboardManager tvCopy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setText("liqwertyuiopxin");
        MyDialog.showToastLong(this, "微信号码已经复制到剪切板请在搜索框进行粘贴");
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            MyDialog.showToast(this, "检查到您手机没有安装微信，请安装后使用该功能");
        }
    }

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            MyDialog.showToast(this, "未安装手Q或安装的版本不支持");
            return false;
        }
    }

    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void navigationAnimator(int type, View view) {
        view.setVisibility(View.VISIBLE);
        float start = 0, end = 0;
        int screenHeight;
        float var;
        screenHeight = getWindow().getDecorView().getHeight();
        if (type == Constant.ANIMATOR_IN) {
            start = screenHeight;
            end = screenHeight - view.getHeight();

        } else {
            start = screenHeight - view.getHeight();
            end = screenHeight;
        }
        var = Math.abs(start - end);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(animation -> {
            float alpha;
            float value = (float) valueAnimator.getAnimatedValue() - screenHeight;
            view.setY((float) valueAnimator.getAnimatedValue());
            alpha = (value / var) * 100;
            view.setAlpha(Math.abs(alpha));
            view.invalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (type == Constant.ANIMATOR_OUT)
                    view.setVisibility(View.INVISIBLE);
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected MessagePresenter createPresenter() {
        return new MessagePresenter();
    }

    @Override
    protected MessageView createView() {
        return this;
    }


    @Override
    public void onMessageResult(List<Message> messages) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (messages == null || messages.size() < 0) {
            return;
        }
        messageQueue.clear();
        messageQueue.addAll(messages);
        runOnUiThread(() -> {
            messageAdapter.myNotifyDataSetChanged(messageQueue);
            messageRecycler.scrollToPosition(messageQueue.size() - 1);
        });
    }

    @Override
    public void onSendMessageResult(String message) {
        runOnUiThread(() -> {
            if (!message.equals(Constant.SEND_MESSAGE_SUCCESS))
                MyDialog.showToast(this, message);
            if (message.equals(Constant.SEND_MESSAGE_SUCCESS))
                getPresenter().requestMessage(Constant.GET_MESSAGE_NEW, userId, this);//获取前几条消息
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone("15823626029");
            } else {
                MyDialog.showToast(this, "没有权限，如果想联系我们请在 我的->About里面找到联系方式");
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    static class BottomOffsetDecoration extends RecyclerView.ItemDecoration {
        private final int mBottomOffset;

        public BottomOffsetDecoration(int bottomOffset) {
            mBottomOffset = bottomOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int dataSize = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if (dataSize > 0 && position == dataSize - 1) {
                outRect.set(0, 0, 0, mBottomOffset);
            } else {
                outRect.set(0, 0, 0, 0);
            }

        }
    }
}
