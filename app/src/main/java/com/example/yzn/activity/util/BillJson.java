package com.example.yzn.activity.util;

public class BillJson {
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
    private String base64Str;
    private int type;
    private String middleName;
    private int currency;
    private String detail;
    private boolean isTaken;

    public  BillJson(){}

    public BillJson(String billID,String issuerID, String productName, String price, int currency, int type, String middleName,String base64Str, String detail, boolean isTaken){
        this.billID=billID;
        this.issuerID = issuerID;
        this.productName = productName;
        this.price=price;
        this.type=type;
        this.middleName = middleName;
        this.base64Str=base64Str;
        this.currency=currency;
        this.detail=detail;
        this.isTaken=isTaken;
    }


    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getIssuerID() {
        return issuerID;
    }

    public void setIssuerID(String issuerID) {
        this.issuerID = issuerID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBase64Str() {
        return base64Str;
    }

    public void setBase64Str(String base64Str) {
        this.base64Str = base64Str;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}