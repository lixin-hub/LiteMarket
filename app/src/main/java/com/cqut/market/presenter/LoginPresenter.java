package com.cqut.market.presenter;

import com.cqut.market.model.LoginModel;
import com.cqut.market.beans.User;
import com.cqut.market.view.LoginView;

public class LoginPresenter extends BasePresenter<LoginView> {

    private final LoginModel loginModel;

    public LoginPresenter() {
        this.loginModel = new LoginModel();
    }

    public void login(String userName, String password) {
        loginModel.login(userName, password, new LoginModel.LoginListener() {
            @Override
            public void onFailed() {
                getView().onLoginResult(null,100+"");
            }

            @Override
            public void onSuccess(User user,String code) {
                getView().onLoginResult(user,code);
            }
        });
    }
}
