package com.example.dashboard.model.order;

import com.google.gson.annotations.SerializedName;

public class OrderUpdateResponse {
    @SerializedName("error")
    public Boolean error;

    @SerializedName("message")
    public String message;

    @SerializedName("updatedOrder")
    public OrderDetails updatedOrder;

    public OrderUpdateResponse(Boolean error, String message, OrderDetails updatedOrder) {
        this.error = error;
        this.message = message;
        this.updatedOrder = updatedOrder;
    }
}
