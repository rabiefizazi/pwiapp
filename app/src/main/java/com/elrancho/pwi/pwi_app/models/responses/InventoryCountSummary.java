package com.elrancho.pwi.pwi_app.models.responses;

public class InventoryCountSummary {


    private Integer storeId;
    private Integer departmentId;
    private String weekEndDate;
    private Double totalInventory;

    public InventoryCountSummary(Integer storeId, Integer departmentId, String weekEndDate, Double totalInventory) {
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public Double getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(Double totalInventory) {
        this.totalInventory = totalInventory;
    }

}