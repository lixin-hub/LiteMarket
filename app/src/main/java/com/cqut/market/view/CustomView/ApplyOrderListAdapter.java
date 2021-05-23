package com.cqut.market.view.CustomView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.beans.Good;
import com.cqut.market.beans.Order;
import com.cqut.market.model.Constant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ApplyOrderListAdapter extends RecyclerView.Adapter<ApplyOrderListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Order> orders = new ArrayList<>();
    public List<ViewHolder> holders = new ArrayList<>();

    public ApplyOrderListAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders.addAll(orders);
        Collections.sort(orders, (o1, o2) -> (int) (o1.getOrderTime() - o2.getOrderTime()));
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
        holder.goodId=order.getGood().getId();
        holders.add(holder);
        Good good = order.getGood();
        Date date = new Date(order.getOrderTime());
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.text_time.setText(dateformat.format(date));
        holder.text_ordercode.setText(order.getOrderCode());
        holder.text_Order_count.setText("x" + order.getCount());
        holder.text_name.setText(good.getName());
        float price = good.getPrice();
        int count = order.getCount();
        float result = price * count;
        BigDecimal bg = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        double result1 = bg.doubleValue();
        holder.text_price.setText("￥" + result1);
        Glide.with(context).load(Constant.HOST + "image?imageName=" + good.getImageName()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String goodId;
        public EditText edit_beizhu;
        TextView text_name//货物名字
                , text_price//及格
                , text_transport_price//配送fee
                , text_Order_count//数量
                , text_time//时间
                , text_ordercode;//订单号
        ImageView image;//图片

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.fragment_mine_order_list_item_image);
            text_price = itemView.findViewById(R.id.fragment_mine_order_list_item_price);
            text_name = itemView.findViewById(R.id.fragment_mine_order_list_item_name);
            text_transport_price = itemView.findViewById(R.id.fragment_mine_order_list_item_transport_price);
            text_ordercode = itemView.findViewById(R.id.fragment_mine_order_list_item_ordercode);
            text_time = itemView.findViewById(R.id.fragment_mine_order_list_item_time);
            text_Order_count = itemView.findViewById(R.id.fragment_mine_order_list_item_count);
            edit_beizhu = itemView.findViewById(R.id.fragment_mine_order_list_item_beizhu_input);
            edit_beizhu.setVisibility(View.VISIBLE);
        }
    }
}
