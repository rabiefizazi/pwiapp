package com.elrancho.pwi.pwi_app.models.responses;

public class InventoryCountDetails {

    private Integer storeId;
    private Integer departmentId;
    private Integer areaId;
    private String userId;
    private Integer vendorItem;
    private String itemDescription;
    private Double cost;
    private Double quantity;
    private String weekEndDate;
    private String  dateUpdated;
    private Boolean itemMaster;
    private String unitOfMeasure;
    private Double totalAmount;

    public InventoryCountDetails(Integer storeId, Integer departmentId, Integer areaId, String userId, Integer vendorItem, String itemDescription, Double cost, Double quantity, String weekEndDate, String dateUpdated, Boolean itemMaster, String unitOfMeasure, Double totalAmount) {
        this.storeId = storeId;
        this.departmentId = departmentId;
        this.areaId=areaId;
        this.userId = userId;
        this.vendorItem = vendorItem;
        this.itemDescription = itemDescription;
        this.cost = cost;
        this.quantity = quantity;
        this.weekEndDate = weekEndDate;
        this.dateUpdated = dateUpdated;
        this.itemMaster = itemMaster;
        this.unitOfMeasure = unitOfMeasure;
        this.totalAmount = totalAmount;
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

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getAreaId() {
        return areaId;
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Boolean getItemMaster() {
        return itemMaster;
    }

    public void setItemMaster(Boolean itemMaster) {
        this.itemMaster = itemMaster;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Double getTotalAmount() {
        return getCost()*getQuantity();
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
