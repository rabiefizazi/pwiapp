package com.elrancho.pwi.pwi_app.models.responses;

public class DepartmentResponse {

    private String storeId;
    private String departmentId;
    private String weekEndDate;
    private String totalInventory;

    public DepartmentResponse(String storeId, String departmentId, String weekEndDate, String totalInventory) {
        this.storeId = storeId;
        this.departmentId = departmentId;
        this.weekEndDate = weekEndDate;
        this.totalInventory = totalInventory;
    }

    public String getStoreId() { return storeId; }

    public void setStoreId(String storeId) {  this.storeId = storeId; }

    public String getDepartmentId() { return departmentId; }

    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getWeekEndDate() { return weekEndDate; }

    public void setWeekEndDate(String weekEndDate) { this.weekEndDate = weekEndDate; }

    public String getTotalInventory() { return totalInventory; }

    public void setTotalInventory(String totalInventory) {  this.totalInventory = totalInventory; }
}
