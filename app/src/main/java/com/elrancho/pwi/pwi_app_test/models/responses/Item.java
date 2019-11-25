package com.elrancho.pwi.pwi_app_test.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("itemUPC")
    @Expose
    private long itemUPC;
    @SerializedName("vendorItem")
    @Expose
    private long vendorItem;
    @SerializedName("storeId")
    @Expose
    private long storeId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("cost")
    @Expose
    private Double cost;
    @SerializedName("unitOfMeasure")
    @Expose
    private String unitOfMeasure;
    @SerializedName("itemMaster")
    @Expose
    private Boolean itemMaster;
    @SerializedName("dateUploaded")
    @Expose
    private String dateUploaded;

    public Item(long itemUPC, long vendorItem, long storeId, String description, String category, Double cost, String unitOfMeasure, Boolean itemMaster, String dateUploaded) {
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

    public long getItemUPC() {
        return itemUPC;
    }

    public void setItemUPC(long itemUPC) {
        this.itemUPC = itemUPC;
    }

    public long getVendorItem() {
        return vendorItem;
    }

    public void setVendorItem(Integer vendorItem) {
        this.vendorItem = vendorItem;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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