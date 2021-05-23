package com.cqut.market.view;

import com.cqut.market.beans.User;

public interface LoginView extends BaseView{
   void onLoginResult( User user,String responseCode);

}
