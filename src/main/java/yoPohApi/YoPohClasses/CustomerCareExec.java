package yoPohApi.YoPohClasses;

import java.io.Serializable;

/**
 * Created by ayushb on 28/9/15.
 */
public class CustomerCareExec implements Serializable{
    String name;
    String designation;
    String emailId;
    String executiveId;
    float rating;

    public float getRating() {
        return rating;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getExecutiveId() {
        return executiveId;
    }

    public String getName() {
        return name;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setExecutiveId(String executiveId) {
        this.executiveId = executiveId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
