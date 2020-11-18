package com.example.yzn.activity.util;

import android.graphics.Bitmap;

public class Bill {
    public final static int Individual=1;
    public final static int Enterprise=2;
    public final static int Organization=3;
    public final static int Middle=4;

    public final static int RMB=0;//人名币
    public final static int USD=1;//美元
    public final static int EUR=2;//欧元
    public final static int GBP=3;//英镑
    public final static int JPY=4;//日元
    public final static int HKD=5;//港元

    private String billID;
    private String issuerID;
    private String productName;
    private String price;
    private Bitmap bitmap;
    private int type;
    private String middleName;
    private int currency;
    private String detail;
    private boolean isTaken;

    public Bill(String billID,String issuerID, String productName, String price,Bitmap bitmap, int currency, int type, String middleName, String detail, boolean isTaken){
        this.billID=billID;
        this.issuerID = issuerID;
        this.productName = productName;
        this.price=price;
        this.type=type;
        this.middleName = middleName;
        this.bitmap=bitmap;
        this.currency=currency;
        this.detail=detail;
        this.isTaken=isTaken;
    }

    public String getIssuerID(){
        return this.issuerID;
    }

    public String getProductName(){
        return this.productName;
    }

    public String getMiddleName(){
        return this.middleName;
    }

    public int getType(){
        return this.type;
    }

    public int getCurrency(){
        return this.currency;
    }

    public String getPrice(){
        return this.price;
    }

    public String getDetail(){
        return this.detail;
    }


    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }
}
