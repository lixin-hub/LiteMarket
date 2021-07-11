package com.cqut.market.view.CustomView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.beans.Good;
import com.cqut.market.model.Constant;

import java.util.ArrayList;

public class GoodListAdapter extends RecyclerView.Adapter<GoodListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Good> goods;
    public ArrayList<ViewHolder> allHolders=new ArrayList<>();
    private View.OnClickListener listener;
    public ViewHolder holder;
    public GoodListAdapter(Context context, ArrayList<Good> goods) {
        this.context = context;
        this.goods = goods;
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Good good = goods.get(position);
        this.holder=holder;
        holder.id=good.getId();
        allHolders.add(holder);
        if (good == null) return;
        holder.text_name.setText(good.getName());
        holder.text_stock.setText("库存:" + good.getStock());
        holder.text_sales.setText("销量:" + good.getSales());
        holder.text_price.setText("价格:" + good.getPrice());
        holder.text_description.setText(good.getDescription());
        holder.text_category.setText("分类:" + good.getCategory());
        holder.text_description.setStartColor(Color.BLACK);
        holder.text_description.setEndColor(Color.BLACK);
        holder.text_description.setFlash(false);
        // holder.text_description.setSpeed(200);
        if (listener != null) {
            holder.image.setOnClickListener(listener);
            holder.image_sub.setOnClickListener(listener);
            holder.image_add.setOnClickListener(listener);
        }
        holder.image.setTag(good.getId());
        holder.image_add.setTag(holder);
        holder.image_sub.setTag(holder);
        Glide.with(context).load( Constant.HOST +"image?imageName=" + good.getImageName()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return (goods==null)?0:goods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, image_add, image_sub;
        TextView text_name, text_price, text_category, text_sales, text_stock;
        ColorfulTextView text_description;
        public CardView cardView;
        public String id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.goods_item_image);
            text_category = itemView.findViewById(R.id.goods_item_category);
            text_description = itemView.findViewById(R.id.goods_item_description);
            text_price = itemView.findViewById(R.id.goods_item_price);
            text_sales = itemView.findViewById(R.id.goods_item_sales);
            text_stock = itemView.findViewById(R.id.goods_item_stock);
            text_name = itemView.findViewById(R.id.goods_item_name);
            image_add = itemView.findViewById(R.id.goods_item_add);
            image_sub = itemView.findViewById(R.id.goods_item_sub);
            cardView = itemView.findViewById(R.id.good_item_slected_flag);
        }
    }
}
