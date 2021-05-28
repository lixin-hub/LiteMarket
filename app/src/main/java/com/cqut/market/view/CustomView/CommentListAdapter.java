package com.cqut.market.view.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cqut.market.R;
import com.cqut.market.beans.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private final int resourceId;
    private View.OnClickListener listener;

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

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
            holder.image_likes = view.findViewById(R.id.comments_likes);
            holder.likes_text = view.findViewById(R.id.comment_likes_text);
            holder.image_likes.setOnClickListener(listener);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.content.setStartColor(Color.parseColor("#FF0052"));
        holder.content.setEndColor(Color.parseColor("#802F7B"));
        holder.content.setSpeed(400);
        holder.content.setFlash(false);
        holder.likes_text.setStartColor(Color.parseColor("#FF0052"));
        holder.likes_text.setEndColor(Color.parseColor("#802F7B"));
        holder.likes_text.setSpeed(400);
        holder.likes_text.setFlash(false);
        if (comment == null) {
            holder.content.setText("献上你的评论吧!");
            holder.image_likes.setVisibility(View.GONE);
            return view;
        }
        holder.image_likes.setVisibility(View.VISIBLE);

        holder.image_likes.setTag(comment.getCommentId());
        holder.likes_text.setText(String.valueOf(comment.getLikes()));
        holder.content.setText(comment.getContent());
        holder.userName.setText(comment.getUserName());
        Date date = new Date(Long.parseLong(comment.getTime()));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //date.getYear()+1970+"年"+date.getMonth()+"月"+date.getDay()+"日"+date.getHours()+"时"+date.getMinutes()+"分"
        holder.time.setText(dateformat.format(date));
        return view;
    }

    static class ViewHolder {
        ColorfulTextView content;
        TextView userName;
        TextView time;
        ImageView image_likes;
        ColorfulTextView likes_text;
    }
}
