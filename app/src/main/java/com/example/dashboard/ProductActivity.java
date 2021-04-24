package com.example.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.adapter.RecyclerViewFeedbackAdapter;
import com.example.dashboard.data.CartTransactions;
import com.example.dashboard.data.Preferences;
import com.example.dashboard.data.SharedProductData;
import com.example.dashboard.model.Product;
import com.example.dashboard.model.User;
import com.example.dashboard.model.feedback.Feedback;
import com.example.dashboard.model.feedback.FeedbackGetResponse;
import com.example.dashboard.network.DataService;
import com.example.dashboard.network.RetrofitClientInstance;
import com.example.dashboard.utility.MathUtility;
import com.google.android.material.button.MaterialButton;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity{
    private TextView productNameText, productCostText, productTotalText, productDescriptionText, sellerInfoText, productAvailableQuantity, feedbackButton;
    private EditText productAddedQuantity;
    private ImageView productImage;
    private MaterialButton addToCartButton;
    private RecyclerView recyclerView;
    private RecyclerViewFeedbackAdapter recyclerViewFeedbackAdapter;


    private CartTransactions cartTransactions;

    private SharedProductData sharedProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productNameText = findViewById(R.id.activity_product_name_text);
        productCostText = findViewById(R.id.activity_product_cost);
        productTotalText = findViewById(R.id.activity_product_total);
        productDescriptionText = findViewById(R.id.activity_product_description_text);
        sellerInfoText = findViewById(R.id.activity_product_seller_info);
        productAddedQuantity = findViewById(R.id.activity_product_add_quantity_edit_text);
        productImage = findViewById(R.id.activity_product_image);
        addToCartButton = findViewById(R.id.activity_product_add_to_cart_button);
        productAvailableQuantity = findViewById(R.id.activity_product_quantity_available);
        feedbackButton = findViewById(R.id.activity_product_add_feedback_button);

        cartTransactions = new CartTransactions(ProductActivity.this);

        sharedProduct = SharedProductData.getInstance();
        showProductDetailsAndAddListeners(sharedProduct.getProduct());


    }

    private void showProductDetailsAndAddListeners(Product product) {
        productNameText.setText(product.name);
        productCostText.append(MathUtility.truncateFloatToTwoDecimalPlaces(product.price));

        productAvailableQuantity.setText("Available Quantity: " + product.quantity.toString());

        productAddedQuantity.setText("1");
        productAddedQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(productAddedQuantity.getText().toString().isEmpty()) {
                    productTotalText.setText("Total: Rs 0");
                }
                else {
                    Float currentTotalCost = product.price * Float.parseFloat(productAddedQuantity.getText().toString());
                    productTotalText.setText("Total: Rs " + MathUtility.truncateFloatToTwoDecimalPlaces(currentTotalCost));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Float totalCost = product.price * Float.parseFloat(productAddedQuantity.getText().toString());
        productTotalText.append(MathUtility.truncateFloatToTwoDecimalPlaces(totalCost));

        if(product.description != null && product.description.text != null)
            productDescriptionText.setText(product.description.text);

        if(product.seller != null) {
            sellerInfoText.setText("Sold by " + product.seller.storeName + "\nat\n" + product.seller.address.addressText + "\nPhone: " + product.seller.phone);
        }

        Picasso.Builder builder = new Picasso.Builder(ProductActivity.this);
        builder.downloader(new OkHttp3Downloader(ProductActivity.this));
        if(product.imageUrl != null && product.imageUrl.length() != 0) {
            builder.build().load(product.imageUrl)
                    .placeholder(R.drawable.ic_categories_nav)
                    .error(R.drawable.ic_categories_nav)
                    .into(productImage);
        }
        else {
            builder.build().load("https://homepages.cae.wisc.edu/~ece533/images/fruits.png")
                    .placeholder(R.drawable.ic_categories_nav)
                    .error(R.drawable.ic_categories_nav)
                    .into(productImage);
        }


        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.addedQuantity = Float.parseFloat(productAddedQuantity.getText().toString());
                addProductToCart(product);
            }
        });

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, FeedbackActivity.class);
                intent.putExtra("productId", product._id);
                intent.putExtra("receiverId", product.seller._id);
                startActivity(intent);
            }
        });

        showFeedbacks(product);
    }

    private void addProductToCart(Product product) {
        cartTransactions.updateOrInsertInCart(product);
    }

    private void showFeedbacks(Product product) {
        DataService service = RetrofitClientInstance.getRetrofitInstance().create(DataService.class);
        Call<FeedbackGetResponse> call = service.getFeedbacksByProductId(product._id);

        call.enqueue(new Callback<FeedbackGetResponse>() {
            @Override
            public void onResponse(Call<FeedbackGetResponse> call, Response<FeedbackGetResponse> response) {
                if(response.body().error) {
                    Toast.makeText(ProductActivity.this, "Can not get feedbacks", Toast.LENGTH_SHORT).show();
                    Log.e("ProductActivity.java", "onResponse: " + response.body().message);
                }
                else {
                    if(response.body().feedbacks != null) {
                        generateFeedbackList(response.body().feedbacks);
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedbackGetResponse> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Can not get reviews", Toast.LENGTH_SHORT).show();
                Log.e("ProductActivity.java", "onFailure: " + t.getMessage());
            }
        });
    }

    private void generateFeedbackList(List<Feedback> feedbackList) {
        recyclerView = findViewById(R.id.activity_product_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewFeedbackAdapter = new RecyclerViewFeedbackAdapter(ProductActivity.this, feedbackList);
        recyclerView.setAdapter(recyclerViewFeedbackAdapter);
    }
}
