package com.cqut.market.view.CustomView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Order> orders;
    private View.OnClickListener listener;

    public OrderListAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        Good good = order.getGood();
        if (good == null) return;
        holder.text_name.setText(good.getName());
        holder.image.setTag(good.getId());
        holder.image_add.setTag(good.getId());
        holder.image_sub.setTag(good.getId());
        holder.image_cancel.setTag(good.getId());

        if (listener != null) {
            holder.image.setOnClickListener(listener);
            holder.image_add.setOnClickListener(listener);
            holder.image_sub.setOnClickListener(listener);
            holder.text_Order_count.setOnClickListener(listener);
            holder.image_cancel.setOnClickListener(listener);
        }
        holder.image_add.setTag(good.getId());
        holder.image_sub.setTag(good.getId());
        holder.image_cancel.setTag(good.getId());
        float price = good.getPrice();
        int count1 = order.getCount();
        float result = price * count1;
        BigDecimal bg = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        double result1 = bg.doubleValue();
        holder.text_price.setText(price + "x" + count1 + "=" + result1);
        int count = order.getCount();
        holder.text_Order_count.setText(count + "");
        Glide.with(context).load(Constant.HOST + "image?imageName=" + good.getImageName()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, image_add, image_sub, image_cancel;
        TextView text_name, text_price, text_Order_count;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            image = itemView.findViewById(R.id.order_info_item_image);
            text_price = itemView.findViewById(R.id.order_info_total_price);
            text_name = itemView.findViewById(R.id.order_info_item_name);
            image_add = itemView.findViewById(R.id.order_info_item_add);
            image_sub = itemView.findViewById(R.id.order_info_item_sub);
            text_Order_count = itemView.findViewById(R.id.order_info_item_counts);
            image_cancel = itemView.findViewById(R.id.order_info_item_cancle);
        }
    }
}
