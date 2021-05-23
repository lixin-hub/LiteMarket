package com.cqut.market.beans;


import com.sun.mail.imap.protocol.ID;

public class Good {



    private String id;
    private String name;//名称
    private float price;//价格
    private String description;//描述
    private long addTime;//上架时间
    private String category;//分类
    private String imageName;//图片名称
    private int sales;//销量
    private int stock;//库存


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Good{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", addTime=" + addTime +
                ", category='" + category + '\'' +
                ", imageName='" + imageName + '\'' +
                ", id='"+id+'\'' +
                '}';
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
