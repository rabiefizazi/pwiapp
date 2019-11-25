package com.elrancho.pwi.pwi_app_test.models.responses;


import java.util.List;

public class InventoryCountSummaryResponse {

    List<InventoryCountSummary> inventoryCountSummaries;

    public InventoryCountSummaryResponse(List<InventoryCountSummary> inventoryCountSummaries) {
        this.inventoryCountSummaries = inventoryCountSummaries;
    }

    public List<InventoryCountSummary> getInventoryCountSummaries() {
        return inventoryCountSummaries;
    }

    public void setInventoryCountSummaries(List<InventoryCountSummary> inventoryCountSummaries) {
        this.inventoryCountSummaries = inventoryCountSummaries;
    }
}
