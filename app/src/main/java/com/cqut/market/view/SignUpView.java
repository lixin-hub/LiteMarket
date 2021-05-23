package com.cqut.market.view;

import com.cqut.market.beans.User;

public interface SignUpView extends BaseView {
    void onSignResult(User user, String code);
}
