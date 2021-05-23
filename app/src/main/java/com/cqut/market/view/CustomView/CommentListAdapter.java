package com.cqut.market.view.CustomView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cqut.market.R;
import com.cqut.market.beans.Comment;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private final int resourceId;
    public CommentListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Comment> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Comment comment = getItem(position);
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.content = view.findViewById(R.id.comment_item_content);
            holder.userName = view.findViewById(R.id.comment_item_user);
            holder.time = view.findViewById(R.id.comment_time);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        if (comment == null) {
            holder.content.setStartColor(0xFF00B8D4);
            holder.content.setEndColor(0xFFFF00D5);
            holder.content.setText("献上你的评论吧!");
            return view;
        }
        holder.content.setSpeed(400);
        holder.content.setFlash(true);
        holder.content.setStartColor(0xFF00B8D4);
        holder.content.setEndColor(0xFFFF00D5);
        holder.content.setText(comment.getContent());
        holder.userName.setText(comment.getUserName());
        Date date = new Date(Long.parseLong(comment.getTime()));
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //date.getYear()+1970+"年"+date.getMonth()+"月"+date.getDay()+"日"+date.getHours()+"时"+date.getMinutes()+"分"
        holder.time.setText(dateformat.format(date));
        return view;
    }

    static class ViewHolder {
        ColorfulTextView content;
        TextView userName;
        TextView time;
    }
}
