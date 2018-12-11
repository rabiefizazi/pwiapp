package com.elrancho.pwi.pwi_app.models.responses;

import java.util.List;

public class InventoryCountDetailsResponse {

    List<InventoryCountDetails> inventoryCounts;

    public InventoryCountDetailsResponse(List<InventoryCountDetails> inventoryCounts) {
        this.inventoryCounts = inventoryCounts;
    }

    public List<InventoryCountDetails> getInventoryCounts() {
        return inventoryCounts;
    }

    public void setInventoryCounts(List<InventoryCountDetails> inventoryCounts) {
        this.inventoryCounts = inventoryCounts;
    }
}
