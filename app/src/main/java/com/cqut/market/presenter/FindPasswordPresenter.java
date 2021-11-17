package com.cqut.market.presenter;

import com.cqut.market.model.FindPasswordModel;
import com.cqut.market.view.FindPasswordView;

public class FindPasswordPresenter extends BasePresenter<FindPasswordView> {
    public void verificationPhone(String phone) {
        new FindPasswordModel().verification(phone, getView());
    }

    public void verificationEmail(String email,String phone) {
        new FindPasswordModel().verificationEmail(email,phone,getView());
    }
}
