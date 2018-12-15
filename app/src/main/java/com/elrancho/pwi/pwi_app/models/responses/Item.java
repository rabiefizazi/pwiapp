package com.elrancho.pwi.pwi_app.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    private Integer itemUPC;
    private Integer vendorItem;
    private Integer storeId;
    private String description;
    private Object category;
    private Double cost;
    private String unitOfMeasure;
    private Boolean itemMaster;
    private String dateUploaded;

    public Item(Integer itemUPC, Integer vendorItem, Integer storeId, String description, Object category, Double cost, String unitOfMeasure, Boolean itemMaster, String dateUploaded) {
        this.itemUPC = itemUPC;
        this.vendorItem = vendorItem;
        this.storeId = storeId;
        this.description = description;
        this.category = category;
        this.cost = cost;
        this.unitOfMeasure = unitOfMeasure;
        this.itemMaster = itemMaster;
        this.dateUploaded = dateUploaded;
    }

    public Integer getItemUPC() {
        return itemUPC;
    }

    public void setItemUPC(Integer itemUPC) {
        this.itemUPC = itemUPC;
    }

    public Integer getVendorItem() {
        return vendorItem;
    }

    public void setVendorItem(Integer vendorItem) {
        this.vendorItem = vendorItem;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getCategory() {
        return category;
    }

    public void setCategory(Object category) {
        this.category = category;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Boolean getItemMaster() {
        return itemMaster;
    }

    public void setItemMaster(Boolean itemMaster) {
        this.itemMaster = itemMaster;
    }

    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }
}
