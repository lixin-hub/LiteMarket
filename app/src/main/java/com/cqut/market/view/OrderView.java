package com.cqut.market.view;


import android.view.View;

import com.cqut.market.beans.Comment;
import com.cqut.market.beans.Good;

import java.util.ArrayList;

public interface OrderView extends BaseView {
   void onCommentApply();
   void onCommentApplyFailed(String message);
   void onGetCommentsSuccess(ArrayList<Comment> comments);
   void onGetCommentsFailed(String message);
   void onGetGoodSuccess(Good good);
   void onGetGoodFailed(String message);
   void onLikesSuccess(String message, View view);
}
