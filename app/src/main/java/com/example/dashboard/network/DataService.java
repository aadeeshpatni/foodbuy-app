package com.example.dashboard.network;

import com.example.dashboard.model.Product;
import com.example.dashboard.model.ProductDescription;
import com.example.dashboard.model.ProductsResponse;
import com.example.dashboard.model.User;
import com.example.dashboard.model.UserResponseObject;
import com.example.dashboard.model.feedback.Feedback;
import com.example.dashboard.model.feedback.FeedbackGetResponse;
import com.example.dashboard.model.feedback.FeedbackPostResponse;
import com.example.dashboard.model.order.OrderRequest;
import com.example.dashboard.model.order.OrderResponse;
import com.example.dashboard.model.order.OrderUpdateResponse;
import com.example.dashboard.model.order.UserOrdersResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DataService {
    @GET("/users")
    Call<UserResponseObject> getUserWithEmail(@Query("email") String email);

    @Headers("Content-Type: application/json")
    @POST("/users")
    Call<UserResponseObject> postNewUser(
            @Query("userType") String userType,
            @Body User user
    );

    @Headers("Content-Type: application/json")
    @POST("/products")
    Call<Product> postOneProduct(
            @Body Product product
    );

    @Headers("Content-Type: application/json")
    @GET("/products")
    Call<ProductsResponse> getAllProducts();

    @Headers("Content-Type: application/json")
    @GET("/products")
    Call<ProductsResponse> getProductByName(
            @Query("productName") String productName,
            @Query("userType") String userType
    );

    @Headers("Content-Type: application/json")
    @POST("/placeOrder")
    Call<OrderResponse> placeOrder(
            @Body OrderRequest orderRequest
    );

    @GET("/orders")
    Call<UserOrdersResponse> getOrdersByUserId(@Query("buyerId") String userId);

    @GET("/feedback")
    Call<FeedbackGetResponse> getFeedbacksByProductId(@Query("productId") String productId);

    @Headers("Content-Type: application/json")
    @POST("/feedback")
    Call<FeedbackPostResponse> postFeedback(@Body Feedback feedback);

    @GET("/orders/seller")
    Call<UserOrdersResponse> getOrdersPlacedToYou(@Query("sellerId") String sellerId);

    @POST("/orders")
    Call<OrderUpdateResponse> updateOrder(@Query("markDispatched") Boolean markDispatched,
                                          @Query("markDelivered") Boolean markDelivered,
                                          @Query("orderId") String orderId);
}
