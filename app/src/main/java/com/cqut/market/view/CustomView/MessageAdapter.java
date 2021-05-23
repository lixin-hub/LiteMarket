package com.cqut.market.view.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.beans.Message;
import com.cqut.market.model.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Message> messages = new ArrayList<>();

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages.addAll(messages);
        Collections.sort(messages, (o1, o2) -> (int) (o1.getDate() - o2.getDate()));
    }

    public void myNotifyDataSetChanged(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == Message.TYPE_ACTIVITY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_activity, parent, false);
        } else if (viewType == Message.TYPE_RECEIVE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_left, parent, false);
        } else if (viewType == Message.TYPE_SEND) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_right, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message == null) return;
        Date date = new Date(message.getDate());
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (getItemViewType(position) == Message.TYPE_ACTIVITY) {
            holder.activity_text_title.setText(message.getTitle());
            holder.activity_text_content.setText(message.getContent());
            holder.activity_text_time.setText(dateformat.format(date));
            Glide.with(context).load(Constant.HOST + "image?imageName=" + message.getImagePath()).into(holder.activity_image);
        } else if (getItemViewType(position) == Message.TYPE_RECEIVE) {
            holder.left_content.setText(message.getContent());
            holder.left_time.setText(dateformat.format(message.getDate()) + " 回复:");
            Glide.with(context).load(Constant.HOST + "image?imageName=" + message.getImagePath()).into(holder.left_head_image);
        } else if (getItemViewType(position) == Message.TYPE_SEND) {
            holder.right_content.setText(message.getContent());
            holder.right_time.setText(dateformat.format(message.getDate()) + " 留言:");
            Bitmap bitmap = BitmapFactory.decodeFile(message.getImagePath());
            if (bitmap != null)
                holder.right_head_image.setImageBitmap(bitmap);
            else holder.right_head_image.setImageResource(R.drawable.ic_launcher);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message == null)
            return Message.TYPE_RECEIVE;
        return message.getMessageType();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView activity_image;
        TextView activity_text_title, activity_text_content, activity_text_time;
        ImageView left_head_image;
        TextView left_content, left_time;
        ImageView right_head_image;
        TextView right_content, right_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activity_image = itemView.findViewById(R.id.message_activity_image);
            activity_text_title = itemView.findViewById(R.id.message_activity_title);
            activity_text_content = itemView.findViewById(R.id.message_activity_content);
            activity_text_time = itemView.findViewById(R.id.message_time);
            right_content = itemView.findViewById(R.id.message_right_content);
            right_head_image = itemView.findViewById(R.id.message_right_head_image);
            right_time = itemView.findViewById(R.id.message_right_time);
            left_content = itemView.findViewById(R.id.message_left_content);
            left_time = itemView.findViewById(R.id.message_left_time);
            left_head_image = itemView.findViewById(R.id.message_left_head_image);

        }
    }
}
