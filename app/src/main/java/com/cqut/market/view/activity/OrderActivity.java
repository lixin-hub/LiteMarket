package com.cqut.market.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.androidadvance.topsnackbar.TSnackbar;
import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.beans.Comment;
import com.cqut.market.beans.Good;
import com.cqut.market.model.Constant;
import com.cqut.market.model.Util;
import com.cqut.market.presenter.OrderPresenter;
import com.cqut.market.view.CustomView.CommentListAdapter;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.OrderView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class OrderActivity extends BaseActivity<OrderView, OrderPresenter> implements OrderView, View.OnClickListener {

    private ArrayList<Comment> comments;
    private ListView commentList;
    private CommentListAdapter commentArrayAdapter;
    private FloatingActionButton bt_comment;
    private String goodId;
    private TextView order_description;
    private TextView order_description_time;
    private ImageView image_good;
    private EditText ed_comment_input;
    private Button bt_apply_comment;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private PopupWindow popupWindow;
    private ProgressDialog dialog;
    private ImageView bt_want;
    private boolean isSelected = false;
    private boolean isSelectedBefore = false;
    private Comment newComment;//新提交的评论

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideStatueBar();
        setContentView(R.layout.activity_order);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        dialog = MyDialog.getProgressDialog(this, "正在加载", "请稍候");
        dialog.show();
        Toolbar toolbar = findViewById(R.id.order_toolbar);
        commentList = findViewById(R.id.order_comment_list);
        order_description = findViewById(R.id.order_description);
        order_description_time = findViewById(R.id.order_description_time);
        bt_want = findViewById(R.id.order_want);
        bt_want.setOnClickListener(this);
        image_good = findViewById(R.id.order_image);
        bt_comment = findViewById(R.id.order_comment_button);
        bt_comment.setOnClickListener(this);
        Intent intent = getIntent();
        goodId = intent.getStringExtra("id");
        isSelectedBefore = intent.getBooleanExtra("isSlected", false);
        if (isSelectedBefore) {
            bt_want.callOnClick();
        }
        collapsingToolbarLayout = findViewById(R.id.order_collapsing_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getPresenter().getComments(goodId);
        getPresenter().getGood(goodId);
        comments = new ArrayList<>();
        commentArrayAdapter = new CommentListAdapter(this, R.layout.comments_item, comments);
        commentList.setAdapter(commentArrayAdapter);
        commentArrayAdapter.setListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            if (isSelected)
                intent.putExtra("id", goodId);
            else intent.putExtra("id", "");
            intent.putExtra("isSlected", isSelected);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected OrderPresenter createPresenter() {
        return new OrderPresenter();
    }

    @Override
    protected OrderView createView() {
        return this;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_comment_button:
                if (popupWindow == null || !popupWindow.isShowing()) {
                    popupToComment(v);
                    ed_comment_input.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.SHOW_FORCED);
                }
                break;
            case R.id.acivity_order_comment_bt_apply:
                String comment = ed_comment_input.getText().toString();
                if (goodId != null) {
                    SharedPreferences sharedPreferences = this.getSharedPreferences(Constant.MY_MARKET_NAME, Context.MODE_PRIVATE);
                    String nickName = sharedPreferences.getString(Constant.NICK_NAME, null);
                    String userName;
                    if (nickName == null)
                        userName = sharedPreferences.getString(Constant.USER_NAME, null);
                    else
                        userName = nickName;
                    if (userName == null) {
                        Intent intent = new Intent(this, SignUpActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    if (!comment.equals("")) {
                        String userId = sharedPreferences.getString(Constant.USER_ID, "null");
                        newComment = new Comment(comment, userName, goodId, userId);
                        getPresenter().applyComment(newComment);
                        ed_comment_input.setText("");
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.SHOW_FORCED);
                        bt_comment.callOnClick();
                    }
                }
                break;
            case R.id.order_want:
                Util.clickAnimator(v);
                if (isSelectedBefore) {
//                    bt_want.setBackgroundColor(Color.GRAY);
                    floatButton(getWindowManager().getDefaultDisplay().getWidth(), 0, v, 1);//向左
                    isSelectedBefore = false;
                    isSelected = true;
                    return;
                }
                isSelected = !isSelected;
                if (isSelected) {
//                    bt_want.setBackgroundColor(Color.GRAY);
                    floatButton(getWindowManager().getDefaultDisplay().getWidth(), 0, v, 1);
                    TSnackbar snackbar = TSnackbar.make(collapsingToolbarLayout, "已经添加到购物车了", TSnackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#0277BD"));
                    snackbar.getView().setRight(Gravity.RIGHT);
                    snackbar.show();
                } else {
//                    bt_want.setBackgroundColor(Color.parseColor("#0277BD"));
                    Snackbar.make(v, "移除成功", Snackbar.LENGTH_SHORT).show();
                    floatButton(getWindowManager().getDefaultDisplay().getWidth(), 0, v, -1);
                }
                break;
            case R.id.comments_likes:
                for (Comment c : comments) {
                    if (c == null) {
                        comments.remove(c);
                    }
                }
                Util.clickAnimator(v);
                String id = (String) v.getTag();
                Comment comm = findCommentById(id);
                if (comm != null) comm.setLikes(comm.getLikes() + 1);
                getPresenter().applyLikes(id, this, v);
        }
    }

    private void sortComments() {
        Collections.sort(comments, (o1, o2) -> (int) (Long.parseLong(o1.getTime()) - Long.parseLong(o2.getTime())));
        Collections.sort(comments, (o1, o2) -> o2.getLikes() - o1.getLikes());
    }

    private Comment findCommentById(String id) {
        for (Comment comment : comments) {
            if (comment.getCommentId().equals(id))
                return comment;
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", goodId);
        intent.putExtra("isSlected", isSelected);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onCommentApply() {
        runOnUiThread(() -> {
            if (newComment != null)
                this.comments.add(0, newComment);
            Toast.makeText(OrderActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
            commentArrayAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(commentList);
        });
    }

    @Override
    public void onCommentApplyFailed(String message) {
        runOnUiThread(() -> {
            Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onGetCommentsSuccess(ArrayList<Comment> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        sortComments();
        runOnUiThread(() -> {
            if (dialog != null) dialog.dismiss();
            commentArrayAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(commentList);
        });
    }

    @Override
    public void onGetCommentsFailed(String message) {
        runOnUiThread(() -> {
            Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
            if (dialog != null) dialog.dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetGoodSuccess(Good good) {
        runOnUiThread(() -> {
            if (dialog != null) dialog.dismiss();
            if (good==null)
                return;
            order_description.setText(good.getDescription());
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            order_description_time.setText("上架时间:" + dateformat.format(new Date(good.getAddTime())));
            Glide.with(this).load(Constant.HOST + "image?imageName=" + good.getImageName()).into(image_good);
            collapsingToolbarLayout.setTitle(good.getName());
            collapsingToolbarLayout.setContentScrimColor(Color.parseColor("#0277BD"));
        });


    }

    @Override
    public void onGetGoodFailed(String message) {
        runOnUiThread(() -> Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onLikesSuccess(String message, View view) {
        runOnUiThread(() -> {
            if (!message.equals(Constant.LIKES_SUCCESS)) {
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
            } else {
                sortComments();
                commentArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void popupToComment(View parent) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_order_comment, null);
        bt_apply_comment = view.findViewById(R.id.acivity_order_comment_bt_apply);
        ed_comment_input = view.findViewById(R.id.activity_order_comment_input);
        bt_apply_comment.setOnClickListener(this);
        popupWindow = new PopupWindow(view);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth((int) (width * 0.9));
        darkenBackground(0.5f);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setOnDismissListener(() -> darkenBackground(1f));
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.contextMenuAnim);
        int y = height / 2;
        int x = width / 2 - ((int) (width * 0.9) / 2);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y - 100);
    }

    private void floatButton(int start, int end, View view, int dir) {
        //100   0
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            if (dir > 0)//100  0
                view.setX(value);
            else //0  100
                view.setX(start - value - view.getWidth());
            view.invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        animator.start();
    }

    private void darkenBackground(float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

}