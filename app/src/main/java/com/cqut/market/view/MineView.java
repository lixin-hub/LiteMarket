package com.cqut.market.view;

import com.cqut.market.beans.Order;
import com.cqut.market.beans.User;

import java.util.List;

public interface MineView {
   void onImageUploadSuccess();
   void onImageUploadFailed(String message);
   void onGetUserInfoSuccess(User user);
   void OnGetUserInfoFailed(String message);
   void onPostUserInfoSuccess();
   void onPostUserInfoFailed(String message);


}
