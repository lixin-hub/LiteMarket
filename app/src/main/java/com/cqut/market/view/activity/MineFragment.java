package com.cqut.market.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.beans.User;
import com.cqut.market.model.Constant;
import com.cqut.market.model.FileUtil;
import com.cqut.market.model.MineModel;
import com.cqut.market.model.NetWorkUtil;
import com.cqut.market.view.CustomView.MyBadge;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.MineView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineFragment extends Fragment implements MineView, View.OnClickListener {
    private String user_id;
    private MineModel mineModel;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog userDialog, updateUserDialog;
    private User user;
    private TextView text_nickName, text_account;
    private ImageView imageView;
    private AlertDialog setNickNameDialog;
    private TextView cache_size;
    private MyBadge myBadge;
    private ImageView image_background;

    public MineFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        image_background = view.findViewById(R.id.fragment_mine_head_background_image);
        RelativeLayout order, addr, person, problem, clear, about;
        order = view.findViewById(R.id.mine_order);
        addr = view.findViewById(R.id.mine_recive_addr);
        person = view.findViewById(R.id.mine_personal_info);
        problem = view.findViewById(R.id.mine_problem_callback);
        clear = view.findViewById(R.id.mine_clear_menmory);
        about = view.findViewById(R.id.mine_about);
        cache_size = view.findViewById(R.id.fragment_mine_cache_size);
        imageView = view.findViewById(R.id.fragment_mine_head_image);
        text_nickName = view.findViewById(R.id.fragment_mine_nickame);
        text_account = view.findViewById(R.id.fragment_mine_username);
        View message = view.findViewById(R.id.mine_message);
        myBadge = view.findViewById(R.id.my_badge);
        text_nickName.setOnClickListener(this);
        message.setOnClickListener(this);
        addr.setOnClickListener(this);
        order.setOnClickListener(this);
        person.setOnClickListener(this);
        problem.setOnClickListener(this);
        clear.setOnClickListener(this);
        about.setOnClickListener(this);
        Bitmap bitmap = FileUtil.getHeadImage(this.getContext());
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        isNewMessage();
        return view;
    }

    private void isNewMessage() {
        if (preferences != null) {
            int count = preferences.getInt(Constant.NEW_MESSAGE_COUNT, 0);
            if (count > 0) {
                myBadge.setVisibility(View.VISIBLE);
                myBadge.setNumber(count + "");
            } else
                myBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isNewMessage();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        preferences = activity.sharedPreferences;
        editor = preferences.edit();
        isNewMessage();
        this.user_id = preferences.getString(Constant.USER_ID, "");
        boolean isLoded = preferences.getBoolean(Constant.IS_USER_LOADED, false);
        mineModel = new MineModel();
        Constant.NETWORK_INFO = NetWorkUtil.isNetworkAvailable(this.getContext());
        if (user_id != null && !user_id.equals("")) {
            if (!isLoded) {
                userDialog = MyDialog.getProgressDialog(getContext(), "请求用户数据", "请稍候");
                userDialog.show();
                mineModel.getUserInfo(user_id, this);
                editor.putBoolean(Constant.IS_USER_LOADED, true);
                editor.apply();
            } else {
                user = new User();
                user.setId(user_id);
            }

        } else {
            if (activity != null) {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
        text_account.setText(preferences.getString(Constant.USER_NAME, "-2"));
        String nickName = preferences.getString(Constant.NICK_NAME, "-1");
        if (nickName.equals("-1") || nickName.equals(""))
            text_nickName.setText("怎么称呼您?");
        else text_nickName.setText(nickName);
        String path_pic = preferences.getString(Constant.BING_PIC, null);
        if (path_pic!=null){
            Glide.with(getContext()).load(path_pic).into(image_background);
        }else {
            NetWorkUtil.sendRequest("http://guolin.tech/api/bing_pic", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String path = response.body().string();
                    if (path != null) {
                        editor.putString(Constant.BING_PIC, path);
                        editor.apply();
                        getActivity().runOnUiThread(() -> {
                            Glide.with(getContext()).load(path).into(image_background);
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_order:
                intentTo(Constant.MINE_ORDER);
                break;
            case R.id.fragment_mine_nickame:
                showPopNickName();
                break;
            case R.id.mine_message:
                Intent intent = new Intent(this.getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_recive_addr:
                intentTo(Constant.MINE_RECIVIE_GOOD_EDIT_INFO);
                break;
            case R.id.mine_personal_info:
                intentTo(Constant.MINE_PERSONAL_INFO);
                break;
            case R.id.mine_problem_callback:
                intentTo(Constant.MINE_PROBLEM_CALLBACK);
                break;
            case R.id.mine_clear_menmory:
                View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_mine_clear_menmory, null);
                View bt_cancle = view.findViewById(R.id.fragment_mine_clear_menmory_cancel);
                View bt_yes = view.findViewById(R.id.fragment_mine_clear_menmory_yes);
                AlertDialog dialog = new AlertDialog.Builder(this.getActivity())
                        .setView(view)
                        .create();
                bt_cancle.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
                bt_yes.setOnClickListener(v1 -> {
                    new Thread(() -> Glide.get(this.getActivity()).clearDiskCache()).start();
                    Glide.get(this.getActivity()).clearMemory();
                    FileUtil.clearCache();
                    cache_size.setText("");
                    dialog.dismiss();
                    MyDialog.showToast(this.getContext(), "清除成功");
                });
                dialog.show();
                break;
            case R.id.mine_about:
                intentTo(Constant.MINE_ABOUT);
                break;
        }
    }

    public void intentTo(int item) {
        Intent intent = new Intent(this.getActivity(), ShowMineItemActivity.class);
        intent.putExtra("item", item);
        getActivity().startActivity(intent);
    }

    @Override
    public void onImageUploadSuccess() {

    }

    @Override
    public void onImageUploadFailed(String message) {
        getActivity().runOnUiThread(() -> {
            MyDialog.showToast(getContext(), message);
        });
    }

    @Override
    public void onGetUserInfoSuccess(User user) {
        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(() -> {
                if (userDialog.isShowing()) userDialog.dismiss();
                this.user = user;
                if (user != null) {
                    String nickName = user.getNickName();
                    if (nickName != null && !nickName.equals("")) {
                        text_nickName.setText(user.getNickName());
                        editor.putString(Constant.NICK_NAME, nickName);
                    }
                    text_account.setText(user.getUserName());
                    editor.putString(Constant.PASSWORD, user.getPassword());
                    editor.putString(Constant.USER_NAME, user.getUserName());
                    editor.putString(Constant.USER_NAME, user.getUserName());
                    editor.putString(Constant.QQ_NUMBER, user.getQqNumber());
                    editor.putString(Constant.PHONE_NUMBER, user.getPhoneNumber());
                    editor.putString(Constant.EMAIL, user.getEmail());
                    editor.putString(Constant.ADDR, user.getAddr());
                    editor.apply();
                }
            });
    }


    @Override
    public void OnGetUserInfoFailed(String message) {
        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(() -> {
                if (setNickNameDialog != null && setNickNameDialog.isShowing())
                    setNickNameDialog.dismiss();
                if (userDialog.isShowing()) userDialog.dismiss();
                MyDialog.showToast(getContext(), message);
                text_nickName.setText(preferences.getString(Constant.NICK_NAME, "怎么称呼您？"));
            });

    }

    @Override
    public void onPostUserInfoSuccess() {
        getActivity().runOnUiThread(() -> {
            if (setNickNameDialog != null && setNickNameDialog.isShowing())
                setNickNameDialog.dismiss();
            if (updateUserDialog != null && updateUserDialog.isShowing())
                updateUserDialog.dismiss();
            MyDialog.showToast(getContext(), "修改成功");
            text_nickName.setText(user.getNickName());
            editor.putString(Constant.NICK_NAME, user.getNickName());
            editor.apply();
        });
    }

    @Override
    public void onPostUserInfoFailed(String message) {
        getActivity().runOnUiThread(() -> {
            if (updateUserDialog != null && updateUserDialog.isShowing())
                updateUserDialog.dismiss();
            MyDialog.showToast(getContext(), message);
        });
    }

    private void showPopNickName() {
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_mine_nickname, null);
        setNickNameDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(true)
                .create();
        Button bt_nick_apply = view.findViewById(R.id.fragment_mine_set_nickame_apply);
        EditText ed_nickname = view.findViewById(R.id.fragment_mine_set_nickame);
        bt_nick_apply.setOnClickListener(v -> {
            String nick = ed_nickname.getText().toString();
            if (nick == null || nick.equals("")) {
                MyDialog.showToast(getActivity(), "内容不能为空");
                return;
            }
            if (!nick.startsWith(" ")) {
                user.setId(user_id);
                user.setNickName(nick);
                Constant.NETWORK_INFO = NetWorkUtil.isNetworkAvailable(MineFragment.this.getContext());
                updateUserDialog = MyDialog.getProgressDialog(getContext(), "提交数据", "请稍候");
                updateUserDialog.show();
                mineModel.updateUserInfo(user, MineFragment.this);
            } else {
                MyDialog.showToast(getActivity(), "昵称开头不能是空格额！");
            }
        });
        setNickNameDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        String nick = preferences.getString(Constant.NICK_NAME, "-1");
        if (!nick.equals("-1"))
            text_nickName.setText(nick);
        Bitmap bitmap = FileUtil.getHeadImage(this.getContext());
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        String cacheSize = FileUtil.getCacheSize(this.getContext());
        if (cacheSize != null)
            cache_size.setText(cacheSize + "");
    }

}
