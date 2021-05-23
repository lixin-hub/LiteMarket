package com.cqut.market.model;

import com.cqut.market.beans.Comment;
import com.cqut.market.beans.Good;
import com.cqut.market.beans.Message;
import com.cqut.market.beans.Order;
import com.cqut.market.beans.User;

import net.sf.json.JSONObject;

public class JsonUtil {

    public static User parseJsonToUser(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return null;
        User user = null;
        JSONObject userD = new JSONObject(jsonstr);
        if (userD.has("_id")) {
            JSONObject _id = userD.getJSONObject("_id");
            String oid = _id.getString("$oid");
            String userName = userD.getString("userName");
            String password = userD.getString("password");
            user = new User(userName, password);
            user.setId(oid);
        } else {
            return null;
        }
        if (userD.has("qqNumber")) {
            String qqNumber = userD.getString("qqNumber");
            user.setQqNumber(qqNumber);
        }
        if (userD.has("email")) {
            String email = userD.getString("email");
            user.setEmail(email);
        }
        if (userD.has("nickName")) {
            String nickName = userD.getString("nickName");
            user.setNickName(nickName);
        }
        if (userD.has("phoneNumber")) {
            String phoneNumber = userD.getString("phoneNumber");
            user.setPhoneNumber(phoneNumber);
        }
        if (userD.has("addr")) {
            String addr = userD.getString("addr");
            user.setAddr(addr);
        }
        return user;
    }

    public static Comment parseComment(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return null;
        JSONObject object = new JSONObject(jsonstr);
        String userName = object.getString("userName");
        String time = object.getString("time");
        String content = object.getString("content");
        String id = object.getString("id");
        String userId = object.getString("userId");
        Comment comment = new Comment(content, userName, id, userId);
        comment.setTime(time);
        return comment;
    }

    public static Good parseJsonGoods(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return null;
        JSONObject object = new JSONObject(jsonstr);
        String name = object.getString("name");
        String description = object.getString("description");
        String category = object.getString("category");
        JSONObject oid = object.getJSONObject("_id");
        String id = oid.getString("$oid");
        long addTime = object.getJSONObject("addTime").getLong("$numberLong");
        double price = object.getDouble("price");
        String imageName = object.getString("imageName");
        int stock = object.getInt("stock");
        int sales = object.getInt("sales");
        Good good = new Good();
        good.setSales(sales);
        good.setStock(stock);
        good.setAddTime(addTime);
        good.setName(name);
        good.setCategory(category);
        good.setImageName(imageName);
        good.setPrice((float) price);
        good.setDescription(description);
        good.setId(id);
        return good;
    }

    public static Good parseJsonGoods(JSONObject object) {
        String name = object.getString("name");
        String description = object.getString("description");
        String category = object.getString("category");
        JSONObject oid = object.getJSONObject("_id");
        String id = oid.getString("$oid");
        long addTime = object.getJSONObject("addTime").getLong("$numberLong");
        double price = object.getDouble("price");
        String imageName = object.getString("imageName");
        int stock = object.getInt("stock");
        int sales = object.getInt("sales");
        Good good = new Good();
        good.setSales(sales);
        good.setStock(stock);
        good.setAddTime(addTime);
        good.setName(name);
        good.setCategory(category);
        good.setImageName(imageName);
        good.setPrice((float) price);
        good.setDescription(description);
        good.setId(id);
        return good;
    }

    public static Order parseJsonOrder(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return null;
        JSONObject object = new JSONObject(jsonstr);
        Order order = new Order();
        String orderCode = object.getString("orderCode");
        int count = object.getInt("count");
        int transportFee = object.getInt("transportFee");
        String state = object.getString("state");
        String userId = object.getString("userId");
        if (object.has("beizhu")) {
            String beizhu = object.getString("beizhu");
            order.setBeizhu(beizhu);
        } else order.setBeizhu("");
        JSONObject _id = object.getJSONObject("_id");
        String oid = _id.getString("$oid");
        JSONObject timeObj = object.getJSONObject("orderTime");
        long time = timeObj.getLong("$numberLong");
        order.setCount(count);
        order.setId(oid);
        order.setOrderCode(orderCode);
        order.setOrderTime(time);
        order.setState(OrderState.valueOf(state));
        order.setTransport_fee(transportFee);
        order.setUserId(userId);
        JSONObject goodObj = object.getJSONObject("good");
        Good good = parseJsonGoods(goodObj);
        order.setGood(good);
        return order;
    }

    public static int parseOrderCounts(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return -1;
        JSONObject object = new JSONObject(jsonstr);
        if (object.has("counts")) {
            return object.getInt("counts");
        } else {
            return -1;
        }
    }

    public static String getResponseCode(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return null;
        JSONObject u = new JSONObject(jsonstr);
        if (u.has("responseCode"))
            return u.getString("responseCode");
        else return null;
    }

    public static Message parseMessage(String jsonstr) {
        if (!jsonstr.startsWith("{") && !jsonstr.endsWith("}")) return null;
        JSONObject object = new JSONObject(jsonstr);
        int messageType = object.getInt("messageType");
        String content = object.getString("content");
        String title = object.getString("title");
        String imagePath = object.getString("imagePath");
        String sendTo = object.getString("sendTo");
        String from = object.getString("from");
        String userId = "";
        if (object.has("userId")) {
            userId = object.getString("userId");
        }
        JSONObject mdate = object.getJSONObject("date");
        long date = mdate.getLong("$numberLong");
        JSONObject oid = object.getJSONObject("_id");
        String id = oid.getString("$oid");
        return new Message.Builder()
                .setContent(content)
                .setDate(date)
                .setFrom(from)
                .setTitle(title)
                .setUserId(userId)
                .setImagePath(imagePath)
                .setMessageType(messageType)
                .setSendTo(sendTo)
                .setId(id)
                .build();
    }
}
