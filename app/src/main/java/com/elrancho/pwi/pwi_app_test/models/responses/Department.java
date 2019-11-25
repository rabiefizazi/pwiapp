package com.elrancho.pwi.pwi_app_test.models.responses;

public class Department {

    private String storeId;
    private String departmentId;

    public Department(String storeId, String departmentId) {
        this.storeId = storeId;
        this.departmentId = departmentId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
}
