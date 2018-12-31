package com.elrancho.pwi.pwi_app.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordResetRequest {

    @SerializedName("operationResult")
    @Expose
    private String operationResult;

    @SerializedName("operationName")
    @Expose
    private String operationName;

    public PasswordResetRequest(String operationResult, String operationName) {
        this.operationResult = operationResult;
        this.operationName = operationName;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
