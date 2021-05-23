package com.cqut.market.presenter;

import android.content.Intent;

import androidx.activity.OnBackPressedCallback;

import com.cqut.market.model.SignUpModel;
import com.cqut.market.beans.User;
import com.cqut.market.view.SignUpView;
import com.cqut.market.view.activity.LoginActivity;

public class SignUpPresenter extends BasePresenter<SignUpView> {

    private final SignUpModel signUpModel;

    public SignUpPresenter() {
        this.signUpModel = new SignUpModel();
    }

    public void signup(String userName, String password) {
        signUpModel.signup(userName, password, new SignUpModel.SignupListener() {
            @Override
            public void onFailed() {
                getView().onSignResult(null,"100");
            }

            @Override
            public void onSuccess(User user,String code) {
                getView().onSignResult(user,code);
            }
        });
    }
    public void sendCheckCode(String userName,String code){
        signUpModel.sendCheckCode(userName,code);
    }

}
