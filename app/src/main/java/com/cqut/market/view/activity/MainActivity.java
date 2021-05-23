package com.cqut.market.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidadvance.topsnackbar.TSnackbar;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cqut.market.R;
import com.cqut.market.beans.Good;
import com.cqut.market.beans.Order;
import com.cqut.market.model.Constant;
import com.cqut.market.model.MessageModel;
import com.cqut.market.model.NetWorkUtil;
import com.cqut.market.model.OrderState;
import com.cqut.market.presenter.MainPresenter;
import com.cqut.market.view.CustomView.GoodListAdapter;
import com.cqut.market.view.CustomView.Image3DSwitchView;
import com.cqut.market.view.CustomView.Image3DView;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.CustomView.OrderListAdapter;
import com.cqut.market.view.MainView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ashokvarma.bottomnavigation.BottomNavigationBar.BACKGROUND_STYLE_RIPPLE;
import static com.ashokvarma.bottomnavigation.BottomNavigationBar.MODE_FIXED;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView, View.OnFocusChangeListener, BottomNavigationBar.OnTabSelectedListener, TextWatcher, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private final ArrayList<Image3DView> images = new ArrayList<>();
    private final ArrayList<Good> allGoods_back = new ArrayList<>();//备份
    private final ArrayList<Order> orders = new ArrayList<>();
    private final List<GoodListAdapter.ViewHolder> viewHolders = new ArrayList<>();
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private ArrayList<String> orderedList;//加入购物车的商品
    private ArrayList<Good> allGoods;
    private ArrayList<Good> searchGoods;
    private RecyclerView recyclerView;
    private GoodListAdapter adapter;
    private Button bt_oder_apply;
    private int previewUI;//上一个界面
    private TextView text_total_money;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton bt_order_info;
    private Image3DSwitchView image3DSwitchView;
    private final TimerTask headerImageTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(() -> image3DSwitchView.scrollToNext());
        }
    };
    private OrderListAdapter orderListAdapter;
    private RecyclerView order_Info_list;
    private PopupWindow popupWindow;
    private StaggeredGridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout dialog_info_image_background;
    private Button bt_oderByPrice, bt_orderBySales;
    private EditText ed_search_box;
    private int backPressedTimes = 1;
    private FrameLayout frameLayout_visibility;
    private FrameLayout frameLayout_takePlace;
    private long fistTimeMillis;
    private int currentPosition = 0;
    private String userId;
    private MineFragment mineFragment;
    private ExpressFragment expressFragment;
    private BottomNavigationBar bottomNavigationBar;
    private ProgressDialog progressDialog_check_order_counts;
    private TextBadgeItem numberBadgeItem;
    private Button bt_clear_all_order;
    private Button bt_order_by_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideStatueBar();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(Color.parseColor("#FFA110"));
        sharedPreferences = getSharedPreferences(Constant.MY_MARKET_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.USER_ID, "");
        if (userId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        editor = sharedPreferences.edit();
        orderedList = new ArrayList<>();
        allGoods = new ArrayList<>();
        searchGoods = new ArrayList<>();
        recyclerView = findViewById(R.id.main_recycler);
        refreshLayout = findViewById(R.id.refresh);
        bt_order_info = findViewById(R.id.activity_main_bt_order_info);
        bt_order_info.setOnClickListener(this);
        refreshLayout.setColorSchemeColors(Color.parseColor("#FFA110"));
        refreshLayout.setOnRefreshListener(this);
        frameLayout_visibility = findViewById(R.id.main_layout_visibility);
        frameLayout_takePlace = findViewById(R.id.main_layout_take_place);
        coordinatorLayout = findViewById(R.id.coordinator);
        bt_order_by_type=findViewById(R.id.order_by_type);
        bt_order_by_type.setOnClickListener(v -> {

        });
        image3DSwitchView = findViewById(R.id.image_switch_view);
        images.add(findViewById(R.id.activity_main_head_3dswitch_1));
        images.add(findViewById(R.id.activity_main_head_3dswitch_2));
        images.add(findViewById(R.id.activity_main_head_3dswitch_3));
        images.add(findViewById(R.id.activity_main_head_3dswitch_4));
        images.add(findViewById(R.id.activity_main_head_3dswitch_5));
        ed_search_box = findViewById(R.id.main_search);
        ed_search_box.addTextChangedListener(this);
        ed_search_box.setOnFocusChangeListener(this);
        listenKeyboardVisible();
        bt_oderByPrice = findViewById(R.id.order_by_price);
        bt_orderBySales = findViewById(R.id.order_by_sales);
        bt_orderBySales.setOnClickListener(this);
        bt_oderByPrice.setOnClickListener(this);
        editor.putBoolean(Constant.IS_USER_LOADED, false);
        editor.apply();
        for (Image3DView v : images) {
            v.setOnClickListener(v1 -> {
                String id = (String) v.getTag();
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putExtra("id", "" + id);
                intent.putExtra("isSlected", (orderedList.contains(id)));
                startActivityForResult(intent, Constant.REQUESTCODE_ORDER);
            });
        }
        bottomNavigationBar = findViewById(R.id.bottomNavigationBar);
        refreshLayout.post(() -> {
            refreshLayout.setRefreshing(true);
            getPresenter().requestGoodsData();
        });
        Timer timer = new Timer();
        timer.schedule(headerImageTask, 1000, 2000);
        initFragment();
        numberBadgeItem = new TextBadgeItem();
        bottomNavigationBar.setMode(MODE_FIXED) // 设置mode
                .setBackgroundStyle(BACKGROUND_STYLE_RIPPLE)  // 背景样式
                .setBarBackgroundColor("#2FA8E1") // 背景颜色
                .setInActiveColor("#929292") // 未选中状态颜色
                .setActiveColor("#FFFFFF") // 选中状态颜色
                .addItem(new BottomNavigationItem(R.drawable.main, "商品").setInactiveIconResource(R.drawable.main).setActiveColor("#FFA110"))// 添加Item
                .addItem(new BottomNavigationItem(R.drawable.main, "活动").setInactiveIconResource(R.drawable.main))
                .addItem(new BottomNavigationItem(R.drawable.main, "我的").setInactiveIconResource(R.drawable.main).setActiveColor("#1E88E5").setBadgeItem(numberBadgeItem))
                .setFirstSelectedPosition(0) //设置默认选中位置
                .initialise();  // 提交初始化（完成配置
        //点击item
        numberBadgeItem.hide();
        bottomNavigationBar.setTabSelectedListener(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    private void addFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_right_out
                );
        if (fragment.isAdded()) {
            return;
        }
        transaction.replace(R.id.main_layout_take_place, fragment);
        transaction.commit();
    }

    private void removeFragment(@NonNull Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment.isAdded()) {
            transaction.remove(fragment);
            transaction.commit();
        }
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected MainView createView() {
        return this;
    }


    @Override
    public void onLoadPicture(Bitmap bitmap) {
        runOnUiThread(() -> {

        });
    }

    @Override
    public void onGoodsResponse(ArrayList<Good> goods) {
        String url = Constant.HOST + "image?imageName=";
        allGoods = goods;
        allGoods_back.clear();
        allGoods_back.addAll(goods);
        long time1 = sharedPreferences.getLong(Constant.LAST_GOOD_IMAGE_UPDATE_TIME, System.currentTimeMillis());
        if (System.currentTimeMillis() - time1 > Constant.GOOD_IMAGE_UPDATE_TIME) {
            new Thread(() -> Glide.get(MainActivity.this).clearDiskCache()).start();
            editor = editor.putLong(Constant.LAST_GOOD_IMAGE_UPDATE_TIME, System.currentTimeMillis());
            editor.apply();
        }
        runOnUiThread(() -> {
            Glide.get(MainActivity.this).clearMemory();
            if (refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            adapter = new GoodListAdapter(this, goods);
            gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                viewHolders.clear();
                for (String id : orderedList)
                    addCardViewGood(id);
            });
            adapter.notifyDataSetChanged();
            float offset = getWindowManager().getDefaultDisplay().getHeight() - bt_order_info.getY();
            BottomOffsetDecoration bottomOffsetDecoration = new BottomOffsetDecoration((int) offset);
            recyclerView.addItemDecoration(bottomOffsetDecoration);
            adapter.setOnClickListener(MainActivity.this);
            int p = 0;
            for (Good g : goods) {
                if (g != null && g.getCategory().contains("head") && p < 5) {
                    images.get(p).setTag(g.getId());
                    Glide.with(this).load(url + g.getImageName()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(images.get(p));
                    p++;
                }
            }
        });
    }

    @Override
    public void onGoodLoadFailed(Exception e) {
        runOnUiThread(() -> {
            if (refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            MyDialog.showToast(MainActivity.this, "商品加载失败");
        });

    }

    @Override
    public void onLoadPictureFailed(Exception e) {
        MyDialog.showToast(this, "图片加载失败");
    }

    @Override
    public void getGetOrderCounts(int counts) {
        runOnUiThread(() -> {
            if (progressDialog_check_order_counts != null && progressDialog_check_order_counts.isShowing())
                progressDialog_check_order_counts.dismiss();
        });
        if (counts >= 0) {
            String orderCode = getPresenter().generateOrderCode(counts);
            if (checkOrderShouldApplyAble()) {
                Intent intent1 = new Intent(this, ShowMineItemActivity.class);
                intent1.putExtra("item", Constant.MINE_RECIVIE_GOOD_INFO);
                Constant.orders.clear();
                Constant.orders.addAll(orders);
                startActivity(intent1);
                for (Order order : orders)
                    order.setOrderCode(orderCode);
            }
        } else {
            if (NetWorkUtil.isNetworkAvailable(this)) {
                MyDialog.showToast(MainActivity.this, "无法提交订单,服务器可能关闭了，稍候再下单吧！");
            } else MyDialog.showToast(MainActivity.this, "检查网络");
        }
    }


    public void initFragment() {
        mineFragment = new MineFragment();
        expressFragment = new ExpressFragment();
    }

    @Override
    public void onTabSelected(int position) {

        if (position != 0) {
            frameLayout_visibility.setVisibility(View.GONE);
            frameLayout_takePlace.setVisibility(View.VISIBLE);
        } else {
            frameLayout_takePlace.setVisibility(View.GONE);
            frameLayout_visibility.setVisibility(View.VISIBLE);
        }
        switch (position) {
            case 0:
                getWindow().setStatusBarColor(Color.parseColor("#FFA110"));
                if (currentPosition == 2) {
                    removeFragment(mineFragment);
                } else if (currentPosition == 1)
                    // removeFragment(expressFragment);
                    currentPosition = position;
                break;
            case 1:
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                if (currentPosition == 2) {
                    //removeFragment(mineFragment);
                }
                addFragment(expressFragment);
                currentPosition = 1;
                //new UpdateApp(this).update();
                break;
            case 2:
                getWindow().setStatusBarColor(Color.parseColor("#1E88E5"));
                if (currentPosition == 1)
                    removeFragment(expressFragment);
                addFragment(mineFragment);
                hidePopupView();
                currentPosition = position;
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    private void hidePopupView() {
        if (popupWindow != null && popupWindow.isShowing())
            bt_order_info.callOnClick();
    }

    @Override
    public void onRefresh() {
        getPresenter().requestGoodsData();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        GoodListAdapter.ViewHolder goodHolder;
        switch (v.getId()) {
            case R.id.goods_item_image:
            case R.id.goods_item_description:
            case R.id.goods_item_name:
                String mid = (String) v.getTag();
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putExtra("id", "" + mid);
                intent.putExtra("isSlected", orderedList.indexOf(mid) >= 0);
                startActivityForResult(intent, Constant.REQUESTCODE_ORDER);
                break;
            case R.id.goods_item_add:
                goodHolder = (GoodListAdapter.ViewHolder) v.getTag();
                String id = goodHolder.id;
                addOrder(id);
                goodHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFAB00"));
                viewHolders.add(goodHolder);
                break;
            case R.id.goods_item_sub:
                goodHolder = (GoodListAdapter.ViewHolder) v.getTag();
                String goodid = goodHolder.id;
                if (!orderedList.contains(goodid)) {
                    Snackbar.make(v, "还没有添加这个商品喲！", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                adapter.holder.cardView.setCardBackgroundColor(Color.WHITE);
                viewHolders.remove(goodHolder);
                orderedList.remove(goodid);
                orders.remove(findOrderByGoodId(goodid));
                Snackbar.make(v, "移除成功", Snackbar.LENGTH_SHORT).show();
                goodHolder.cardView.setCardBackgroundColor(Color.WHITE);
                break;
            case R.id.activity_main_bt_order_info:
                if (popupWindow == null || !popupWindow.isShowing()) {
                    btAnimotor(0, 100);
                    showPopupWindow(v, findGoodByIds(orderedList, allGoods));
                    popupAnimotor(-getWindowManager().getDefaultDisplay().getWidth(), 0, false);

                } else {
                    btAnimotor(100, 0);
                    popupAnimotor(0, -getWindowManager().getDefaultDisplay().getWidth(), true);

                }
                break;
            case R.id.order_info_item_add:
                String id1 = ((String) v.getTag());
                if (id1 == null) return;
                Order order_add = findOrderByGoodId(id1);
                int count1 = order_add.getCount();
                if (orderListAdapter != null) {
                    order_add.setCount(++count1);
                    orderListAdapter.notifyDataSetChanged();
                }
                text_total_money.setText(calculateMoney(orders) + "");
                break;
            case R.id.order_info_item_sub:
                String id2 = ((String) v.getTag());
                if (id2 == null) return;
                Order order_sub = findOrderByGoodId(id2);
                int count2 = order_sub.getCount();
                count2--;
                if (count2 > 0)
                    order_sub.setCount(count2);
                else {
                    orderListAdapter.notifyItemRemoved(orders.indexOf(order_sub));
                    orders.remove(order_sub);
                    orderedList.remove(id2);
                    cancelCardViewGood(id2);
                }
                orderListAdapter.notifyDataSetChanged();
                text_total_money.setText(calculateMoney(orders) + "");
                int count = orderListAdapter.getItemCount();
                if (count >= 2) {
                    bt_clear_all_order.setVisibility(View.VISIBLE);
                } else {
                    bt_clear_all_order.setVisibility(View.GONE);
                }
                break;
            case R.id.order_info_item_cancle:
                String id3 = ((String) v.getTag());
                cancelCardViewGood(id3);
                dialog_info_image_background.setVisibility(View.GONE);
                if (id3 == null) return;
                Order order = findOrderByGoodId(id3);
                orderListAdapter.notifyItemRemoved(orders.indexOf(order));
                orders.remove(order);
                orderedList.remove(id3);
                text_total_money.setText(calculateMoney(orders) + "");
                if (orderedList.size() == 0)
                    dialog_info_image_background.setVisibility(View.VISIBLE);
                else dialog_info_image_background.setVisibility(View.GONE);
                int c = orderListAdapter.getItemCount();
                if (c >= 2) {
                    bt_clear_all_order.setVisibility(View.VISIBLE);
                } else {
                    bt_clear_all_order.setVisibility(View.GONE);
                }

                break;
            case R.id.dialog_info_bt_apply:
                if (checkOrderShouldApplyAble()) {
                    progressDialog_check_order_counts = MyDialog.getProgressDialog(this, "生成订单", "请稍候");
                    progressDialog_check_order_counts.show();
                    getPresenter().getOrderCounts(this);
                }
                break;
            case R.id.order_by_price:
                popupWindowToPrice(v);
                break;
            case R.id.order_by_sales:
                popupWindowToSales(v);
                break;
        }
    }

    private void cancelCardViewGood(String id) {
        for (int i = 0; i < viewHolders.size(); i++) {
            if (id.equals(viewHolders.get(i).id)) {
                viewHolders.get(i).cardView.setCardBackgroundColor(Color.WHITE);
                viewHolders.remove(i);
            }
        }
    }

    private void addCardViewGood(String id) {
        for (int i = 0; i < adapter.allHolders.size(); i++) {
            if (id.equals(adapter.allHolders.get(i).id)) {
                viewHolders.add(adapter.allHolders.get(i));
                adapter.allHolders.get(i).cardView.setCardBackgroundColor(Color.parseColor("#FFAB00"));
            }
        }
    }

    private boolean checkOrderShouldApplyAble() {
        return orders.size() > 0;
    }

    public Order findOrderByGoodId(String id) {
        for (Order order : orders) {
            if (order.getGood().getId().equals(id))
                return order;
        }
        return null;
    }

    @Override
    protected void onRestart() {
        if (popupWindow != null && popupWindow.isShowing()) {
            bt_order_info.callOnClick();
        }
        super.onRestart();
    }

    @Override
    protected void onStart() {
        hasNewMessage();
        super.onStart();
    }

    @Override
    protected void onResume() {
        hasNewMessage();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putBoolean(Constant.IS_USER_LOADED, false);
        editor.apply();
    }

    private void hasNewMessage() {
        MessageModel.hasNewMessage(userId, count -> {
            runOnUiThread(() -> {
                if (count > 0) {
                    int local = Constant.getLocalMessages().size();
                    if (local < count) {
                        numberBadgeItem.show();
                        numberBadgeItem.setText(count - local + "");
                        editor.putInt(Constant.NEW_MESSAGE_COUNT, count - local);
                    } else {
                        numberBadgeItem.hide();
                        editor.putInt(Constant.NEW_MESSAGE_COUNT, 0);
                    }
                    editor.apply();
                }
            });
        });

    }

    //设置动画效果
    private void initAnim() {
        //通过加载XML动画设置文件来创建一个Animation对象；
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.recycler_anim);
        //得到一个LayoutAnimationController对象；
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        //设置控件显示的顺序；
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        //设置控件显示间隔时间；
        lac.setDelay(0.3f);
        //为ListView设置LayoutAnimationController属性；
        order_Info_list.setLayoutAnimation(lac);
    }

    public Good findGoodById(String id) {
        for (Good good : allGoods)
            if (good.getId().equals(id))
                return good;
        return null;
    }

    private void addOrder(String id) {
        for (String s : orderedList)
            if (id.equals(s)) {
                Snackbar.make(findViewById(R.id.goods_item_sub), "商品已经添加了!", Snackbar.LENGTH_SHORT).show();
                return;
            }
        addCardViewGood(id);
        orderedList.add(id);
        Order order = new Order();
        order.setGood(findGoodById(id));
        order.setTransport_fee(0);
        order.setState(OrderState.STATE_APPLY);
        order.setCount(1);
        order.setOrderTime(System.currentTimeMillis());
        order.setUserId(userId);
        orders.add(order);
        TSnackbar snackbar = TSnackbar.make(findViewById(R.id.main_layout_visibility), "添加成功了！", TSnackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.parseColor("#FFA810"));
        snackbar.show();
    }

    private void btAnimotor(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            bt_order_info.setRotation(value);
            bt_order_info.invalidate();
            bt_order_info.setEnabled(false);
            bt_order_info.setBackgroundColor(Color.RED);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bt_order_info.setEnabled(true);
            }
        });
        animator.start();
    }

    private void popupAnimotor(int start, int end, boolean dismiss) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(500);
        View v = popupWindow.getContentView();
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            v.setX(value);
            v.invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (dismiss) popupWindow.dismiss();
            }
        });
        animator.start();
    }

    private PopupWindow showPopupWindow(View parent, ArrayList<Good> goods) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_order_info, null);
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        bt_clear_all_order = view.findViewById(R.id.dialog_order_info_clear_all);
        bt_clear_all_order.setOnClickListener((v) -> {
            for (String id : orderedList) {
                cancelCardViewGood(id);
            }
            orders.clear();
            orderedList.clear();
            viewHolders.clear();
            bt_clear_all_order.setVisibility(View.GONE);
            orderListAdapter.notifyDataSetChanged();
            text_total_money.setText(calculateMoney(orders) + "");
            dialog_info_image_background.setVisibility(View.VISIBLE);
        });
        TextView text_look_look = view.findViewById(R.id.dialog_order_info_look_look);
        text_look_look.setOnClickListener(v -> bt_order_info.callOnClick());
        text_look_look.getPaint().setAntiAlias(true);
        text_look_look.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bt_oder_apply = view.findViewById(R.id.dialog_info_bt_apply);
        bt_oder_apply.setOnClickListener(this);
        text_total_money = view.findViewById(R.id.dialog_info_text_total_money);
        dialog_info_image_background = view.findViewById(R.id.dialog_info_background);
        if (goods.size() == 0) dialog_info_image_background.setVisibility(View.VISIBLE);
        else dialog_info_image_background.setVisibility(View.GONE);
        popupWindow = new PopupWindow(view);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        darkenBackground(0.5f);
        popupWindow.setOnDismissListener(() -> darkenBackground(1f));
        popupWindow.setHeight(getWindow().getDecorView().getHeight() / 2);
        popupWindow.showAtLocation(rootview, Gravity.NO_GRAVITY, 0, (int) (parent.getY() - getWindow().getDecorView().getHeight() / 2));
        orderListAdapter = new OrderListAdapter(this, orders);
        orderListAdapter.setOnClickListener(this);
        order_Info_list = view.findViewById(R.id.dialog_info_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        order_Info_list.setLayoutManager(linearLayoutManager);
        order_Info_list.setItemAnimator(new DefaultItemAnimator());
        order_Info_list.setAdapter(orderListAdapter);
        initAnim();
        int count = orderListAdapter.getItemCount();
        if (count >= 2) {
            bt_clear_all_order.setVisibility(View.VISIBLE);
        } else {
            bt_clear_all_order.setVisibility(View.GONE);
        }
        text_total_money.setText(calculateMoney(orders) + "");
        return popupWindow;
    }


    private void popupWindowToPrice(View parent) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_paixu, null);
        TextView text_up = view.findViewById(R.id.order_up);
        TextView text_down = view.findViewById(R.id.order_down);
        text_down.setOnClickListener(v -> {
            Collections.sort(allGoods, (o1, o2) -> (int) (o2.getPrice() * 100 - o1.getPrice() * 100));
            adapter.notifyItemRangeChanged(0, allGoods.size());
        });
        text_up.setOnClickListener(v -> {
            Collections.sort(allGoods, (o1, o2) -> (int) (o1.getPrice() * 100 - o2.getPrice() * 100));
            adapter.notifyItemRangeChanged(0, allGoods.size());
        });
        PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setWidth(150);
        popupWindow.setHeight(250);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(parent, -50, 0);

    }

    private void popupWindowToSales(View parent) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_paixu, null);
        TextView text_up = view.findViewById(R.id.order_up);
        TextView text_down = view.findViewById(R.id.order_down);
        text_down.setOnClickListener(v -> {
            if (allGoods.size() > 0) {
                Collections.sort(allGoods, (o1, o2) -> o2.getSales() - o1.getSales());
                adapter.notifyItemRangeChanged(0, allGoods.size());
            }
        });
        text_up.setOnClickListener(v -> {
            if (allGoods.size() > 0) {
                Collections.sort(allGoods, (o1, o2) -> o1.getSales() - o2.getSales());
                adapter.notifyItemRangeChanged(0, allGoods.size());
            }
        });
        PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setWidth(150);
        popupWindow.setHeight(250);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(parent, -50, 0);

    }

    private ArrayList<Good> findGoodByIds(ArrayList<String> ids, ArrayList<Good> goodArrayList) {
        ArrayList<Good> goods = new ArrayList<>();
        for (String id : ids) {
            for (Good good : goodArrayList) {
                if (good.getId().equals(id))
                    goods.add(good);
            }
        }
        return goods;
    }

    public int px2dp(float pxValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    public int dp2px(float dpValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUESTCODE_ORDER && resultCode == RESULT_OK) {
            String id = data.getStringExtra("id");
            boolean isSlected = data.getBooleanExtra("isSlected", false);
            if (isSlected)
                addOrder(id);
            else {
                orders.remove(findOrderByGoodId(id));
                orderedList.remove(id);
                cancelCardViewGood(id);
            }
        }

    }

    private void darkenBackground(float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTimes == 2) {
            if (System.currentTimeMillis() - fistTimeMillis <= 1500) {
                finish();
            } else {
                backPressedTimes = 1;
            }
        } else {
            fistTimeMillis = System.currentTimeMillis();
            Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
            backPressedTimes++;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        searchGoods.clear();
        for (Good good : allGoods_back) {
            if (good.getDescription().contains(s) || good.getName().contains(s)) {
                //searchGoods.add(good);
                recyclerView.smoothScrollToPosition(allGoods.indexOf(good));
            }
        }
//        refreshLayout.setEnabled(s.length() <= 0);

    }

    @Override
    public void afterTextChanged(Editable s) {
//        allGoods.clear();
//        allGoods.addAll(searchGoods);
//        if (adapter != null)
//            adapter.notifyDataSetChanged();
    }

    private void refreshAllViewHolder() {
        for (int i = 0; i < viewHolders.size(); i++) {
            GoodListAdapter.ViewHolder holder = viewHolders.get(i);
            cancelCardViewGood(holder.id);
        }
        for (String id : orderedList)
            addCardViewGood(id);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        refreshAllViewHolder();
        if (hasFocus) {
            gridLayoutManager.setSpanCount(1);
        } else {
            gridLayoutManager.setSpanCount(2);
            ed_search_box.setText("");
        }
//        //searchBox
//        allGoods.clear();
//        allGoods.addAll(allGoods_back);
    }

    private void listenKeyboardVisible() {

        final View contentView = findViewById(android.R.id.content);
        final View activityRoot = getWindow().getDecorView();
        if (activityRoot == null) {
            return;
        }

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();
            private final int visibleThreshold = Math.round(dp2px(100));

            @Override
            public void onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRoot.getRootView().getHeight() - r.height();
                //键盘是否弹出
                boolean isOpen = heightDiff > visibleThreshold;
                if (!isOpen) {
                    ed_search_box.clearFocus();
                } else {
                    ed_search_box.requestFocus();
                }
            }

        });

    }


    static class BottomOffsetDecoration extends RecyclerView.ItemDecoration {
        private final int mBottomOffset;
        private final boolean isFirstRefresh = true;

        public BottomOffsetDecoration(int bottomOffset) {
            mBottomOffset = bottomOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int dataSize = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            StaggeredGridLayoutManager gard = (StaggeredGridLayoutManager) parent.getLayoutManager();
            if ((dataSize - position) <= gard.getSpanCount()) {
                int height = gard.getBottomDecorationHeight(view);
                if (mBottomOffset > height)
                    outRect.set(0, 0, 0, mBottomOffset);
            } else {
                outRect.set(0, 0, 0, 0);
            }

        }
    }
}