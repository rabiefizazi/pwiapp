package com.elrancho.pwi.pwi_app.models.responses;

public class InventoryCountDetails {

    private Integer storeId;
    private Integer departmentId;
    private String userId;
    private Integer vendorItem;
    private String itemDescription;
    private Double cost;
    private Integer quantity;
    private String weekEndDate;
    private String  dateUpdated;
    private Boolean itemMaster;

    public InventoryCountDetails(Integer storeId, Integer departmentId, String userId, Integer vendorItem, Double cost, Integer quantity, String weekEndDate, String  dateUpdated, Boolean itemMaster) {
        this.storeId = storeId;
        this.departmentId = departmentId;
        this.userId = userId;
        this.vendorItem = vendorItem;
        this.cost = cost;
        this.quantity = quantity;
        this.weekEndDate = weekEndDate;
        this.dateUpdated = dateUpdated;
        this.itemMaster = itemMaster;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getVendorItem() {
        return vendorItem;
    }

    public void setVendorItem(Integer vendorItem) {
        this.vendorItem = vendorItem;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String  getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String  dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Boolean getItemMaster() {
        return itemMaster;
    }

    public void setItemMaster(Boolean itemMaster) {
        this.itemMaster = itemMaster;
    }
}
