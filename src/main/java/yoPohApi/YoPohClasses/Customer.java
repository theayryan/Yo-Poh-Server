package yoPohApi.YoPohClasses;

import java.io.Serializable;

/**
 * Created by ayushb on 28/9/15.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 7526472295622776147L;
    String name;
    String address;
    String phoneNum;
    String customerId;
    String emailId;
    String[] products = new String[10];

    public Customer() {
    }

    public Customer(String name, String address, String phoneNum, String customerId, String emailId) {
        this.name = name;
        this.address = address;
        this.customerId = customerId;
        this.phoneNum = phoneNum;
        this.emailId = emailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String[] getProducts() {
        return products;
    }

    public void setProducts(String[] products) {
        this.products = products;
    }
}
