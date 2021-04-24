package com.example.dashboard.model.order;

import com.google.gson.annotations.SerializedName;

public class OrderDetails {
    @SerializedName("_id")
    public String _id;

    @SerializedName("buyerId")
    public String buyerId;

    @SerializedName("productId")
    public String productId;

    @SerializedName("sellerId")
    public String sellerId;

    @SerializedName("orderedQuantity")
    public Float orderedQuantity;

    @SerializedName("time")
    public Long orderPlacedTimeInMillis;

    @SerializedName("cost")
    public Float cost;

    @SerializedName("dispatch")
    public OrderDispatchDetails dispatch;

    @SerializedName("delivery")
    public OrderDeliveryDetails delivery;

    public OrderDetails(String buyerId,
                        String productId,
                        String sellerId,
                        Float orderedQuantity,
                        Long orderPlacedTimeInMillis,
                        Float cost,
                        OrderDispatchDetails dispatch,
                        OrderDeliveryDetails delivery) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.orderedQuantity = orderedQuantity;
        this.orderPlacedTimeInMillis = orderPlacedTimeInMillis;
        this.cost = cost;
        this.dispatch = dispatch;
        this.delivery = delivery;
    }
}
