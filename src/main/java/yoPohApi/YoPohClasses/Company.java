package yoPohApi.YoPohClasses;

import java.io.Serializable;

/**
 * Created by ayushb on 28/9/15.
 */
public class Company implements Serializable{
    String companyName;
    String companyDivision;
    String companyId;

    public String getCompanyDivision() {
        return companyDivision;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyDivision(String companyDivision) {
        this.companyDivision = companyDivision;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
