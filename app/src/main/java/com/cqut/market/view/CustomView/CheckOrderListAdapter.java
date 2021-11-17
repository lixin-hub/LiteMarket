package com.cqut.market.view.CustomView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.beans.Good;
import com.cqut.market.beans.Order;
import com.cqut.market.model.Constant;
import com.cqut.market.model.MineItemModel;
import com.cqut.market.view.CancelOrderListener;
import com.cqut.market.view.activity.ShowMineItemActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CheckOrderListAdapter extends RecyclerView.Adapter<CheckOrderListAdapter.ViewHolder> implements CancelOrderListener {

    private final Context context;
    private final ArrayList<Order> orders = new ArrayList<>();
    private String lastOrderCode = "";
    private int lastColor = Color.parseColor("#EF6C00");

    public CheckOrderListAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders.clear();
        this.orders.addAll(orders);
        Collections.sort(this.orders, (o1, o2) -> (int) (o2.getOrderTime() - o1.getOrderTime()));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mine_order_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        if (order == null) return;
        switch (order.getState()) {
            case STATE_APPLY:
                holder.bt_cancel_order.setVisibility(View.VISIBLE);
                break;
            case STATE_PROCESSING:
            case STATE_CANCEL:
            case STATE_COMPLETED:
                holder.bt_cancel_order.setVisibility(View.GONE);
                break;
        }
        if (!lastOrderCode.equals(order.getOrderCode())) {
            lastOrderCode = order.getOrderCode();
            if (lastColor == Color.parseColor("#6800B0FF"))
                lastColor = Color.parseColor("#68827717");
            else lastColor = Color.parseColor("#6800B0FF");
        }
        holder.background.setBackgroundColor(lastColor);
        Good good = order.getGood();
        Date date = new Date(order.getOrderTime());
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.text_time.setText(dateformat.format(date));
        holder.text_order_state.setText(order.getState().toString());
        holder.text_ordercode.setText(order.getOrderCode());
        holder.text_transport_price.setText("配送费:" + " ￥" + order.getTransport_fee());
        holder.text_Order_count.setText("x" + order.getCount());
        holder.text_name.setText(good.getName());
        holder.text_beizhu.setText(order.getBeizhu());
        float price = good.getPrice();
        int count = order.getCount();
        float result = price * count;
        BigDecimal bg = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        double result1 = bg.doubleValue();
        holder.text_price.setText("￥" + result1);
        Glide.with(context).load(Constant.HOST + "image?imageName=" + good.getImageName()).into(holder.image);

        holder.bt_cancel_order.setOnClickListener(v -> {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_ordert, null);
            View bt_contact = view.findViewById(R.id.dialog_cancel_order_contact);
            RadioGroup group = view.findViewById(R.id.dialog_cancel_order_radio_group);
            View bt_think = view.findViewById(R.id.dialog_cancel_order_think);
         /*   RadioButton radio1 = view.findViewById(R.id.dialog_radio1);
            RadioButton radio2 = view.findViewById(R.id.dialog_radio2);
            RadioButton radio3 = view.findViewById(R.id.dialog_radio3);
            RadioButton radio4 = view.findViewById(R.id.dialog_radio4);*/
            EditText edit = view.findViewById(R.id.dialog_cancel_order_edit);
            View bt_ok = view.findViewById(R.id.dialog_cancel_order_ok);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            bt_contact.setOnClickListener(v1 -> {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(((ShowMineItemActivity) context), new String[]{Manifest.permission.CALL_PHONE}, ShowMineItemActivity.CALL_PHONE);
                } else {
                    ((ShowMineItemActivity) context).callPhone(Constant.MY_PHONE_NUMBER);
                    dialog.dismiss();
                }
            });
            bt_ok.setOnClickListener(v12 -> {
                String info="";
                if (group.getCheckedRadioButtonId() != -1) {
                    info = ((RadioButton) view.findViewById(group.getCheckedRadioButtonId())).getText().toString();
                }
                String info1 = edit.getText().toString();
                String message = "取消原因:\n"+info + "\n" + info1;
                MineItemModel.cancelOrder(order, message, holder, CheckOrderListAdapter.this);
                dialog.dismiss();
                holder.bt_cancel_order.setEnabled(false);
            });
            bt_think.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onCancelOrder(Order s, ViewHolder holder) {
        ((ShowMineItemActivity) context).runOnUiThread(() -> {
            if (s != null) {
                MyDialog.showToast(context, "取消订单成功！");
                ((ShowMineItemActivity) context).getPresenter().getOrderList(((ShowMineItemActivity) context).userId, ((ShowMineItemActivity) context));
            } else {
                holder.bt_cancel_order.setEnabled(true);
                AlertDialog.Builder builder = MyDialog.getDialog(context, "取消订单", "取消失败,请联系客服");
                builder.setCancelable(true);
                builder.create().show();
            }
        });

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;//图片
        Button bt_cancel_order;
        TextView text_name//货物名字
                , text_price//及格
                , text_transport_price//配送fee
                , text_Order_count//数量
                , text_order_state//订单状态
                , text_time//时间
                , text_beizhu//备注
                , text_ordercode;//订单号
        RelativeLayout background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.fragment_mine_order_list_item_background);
            image = itemView.findViewById(R.id.fragment_mine_order_list_item_image);
            text_price = itemView.findViewById(R.id.fragment_mine_order_list_item_price);
            text_name = itemView.findViewById(R.id.fragment_mine_order_list_item_name);
            text_transport_price = itemView.findViewById(R.id.fragment_mine_order_list_item_transport_price);
            text_ordercode = itemView.findViewById(R.id.fragment_mine_order_list_item_ordercode);
            text_order_state = itemView.findViewById(R.id.fragment_mine_order_list_item_state);
            text_time = itemView.findViewById(R.id.fragment_mine_order_list_item_time);
            text_Order_count = itemView.findViewById(R.id.fragment_mine_order_list_item_count);
            text_beizhu = itemView.findViewById(R.id.fragment_mine_order_list_item_beizhu);
            bt_cancel_order = itemView.findViewById(R.id.fragment_mine_order_list_item_cancel);
        }
    }
}
