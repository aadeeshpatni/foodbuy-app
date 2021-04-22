package com.example.dashboard.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import retrofit2.http.Headers;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int localId;

    @SerializedName("error")
    public Boolean error;

    @SerializedName("message")
    public String message;

    @ColumnInfo(name = "_id")
    @SerializedName("_id")
    public String _id;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    public String name;

    @ColumnInfo(name = "price")
    @SerializedName("price")
    public Float price;

    @SerializedName("quantity")
    public Float quantity;

    @ColumnInfo(name = "addedQuantity")
    public Float addedQuantity;

    @ColumnInfo(name = "unit")
    @SerializedName("unit")
    public String unit;

    @ColumnInfo(name = "sellerType")
    @SerializedName("sellerType")
    public String sellerType;

    @ColumnInfo(name = "imageUrl")
    @SerializedName("imageUrl")
    public String imageUrl;

    @Embedded
    @SerializedName("description")
    public ProductDescription description;

    @Embedded
    @SerializedName("seller")
    public User seller;

    public Product(int localId, String _id, String name, Float price, Float addedQuantity, String unit, String sellerType, String imageUrl, ProductDescription description, User seller){
        this.localId = localId;
        this._id = _id;
        this.name = name;
        this.price = price;
        this.addedQuantity = addedQuantity;
        this.unit = unit;
        this.sellerType = sellerType;
        this.description = description;
        this.seller = seller;
    }

    @Ignore
    public Product(String name, Float price, Float quantity, String unit, String sellerType, String imageUrl, ProductDescription description, User seller) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.sellerType = sellerType;
        this.description = description;
        this.seller = seller;
    }

    public String toString() {
        String str = "error: " + error + ", mess: " + message + ", name: " + name;
        if(description != null) {
            str += ", type: " + description.text;
        }
        if(seller != null) {
            str += ", sellerName: " + seller.name;
        }
        return str;
    }
}


