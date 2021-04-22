package com.example.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.data.Preferences;
import com.example.dashboard.model.Product;
import com.example.dashboard.model.ProductDescription;
import com.example.dashboard.network.DataService;
import com.example.dashboard.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    EditText productName, productPrice, productQuantity, productUnit, productType, productSubtype, productDescriptionText, productImageUrl;
    TextView doneText, addDescriptionText;
    LinearLayout typeLinearLayout, subTypeLinearLayout, descriptionTextLinearLayout, imageUrlLinearLayout;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productName = findViewById(R.id.fragment_addproduct_name_edit_text);
        productPrice = findViewById(R.id.fragment_addproduct_price_edit_text);
        productQuantity = findViewById(R.id.fragment_addproduct_quantity_edit_text);
        productUnit = findViewById(R.id.fragment_addproduct_unit_edit_text);
        productType = findViewById(R.id.fragment_addproduct_type_edit_text);
        productSubtype = findViewById(R.id.fragment_addproduct_subtype_edit_text);
        productDescriptionText = findViewById(R.id.fragment_addproduct_description_text_edit_text);
        productImageUrl = findViewById(R.id.fragment_addproduct_image_edit_text);

        doneText = findViewById(R.id.done_text_fragment_addproduct);
        addDescriptionText = findViewById(R.id.fragment_addproduct_description_button);

        typeLinearLayout = findViewById(R.id.fragment_addproduct_type);
        subTypeLinearLayout = findViewById(R.id.fragment_addproduct_subtype);
        descriptionTextLinearLayout = findViewById(R.id.fragment_addproduct_description_text);
        imageUrlLinearLayout = findViewById(R.id.fragment_addproduct_image);

        progressDialog = new ProgressDialog(AddProductActivity.this);
        progressDialog.setMessage("Adding product");

        addDescriptionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeLinearLayout.setVisibility(View.VISIBLE);
                subTypeLinearLayout.setVisibility(View.VISIBLE);
                descriptionTextLinearLayout.setVisibility(View.VISIBLE);
                imageUrlLinearLayout.setVisibility(View.VISIBLE);

                addDescriptionText.setVisibility(View.GONE);
            }
        });


        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPostProduct();
            }
        });
    }

    private void createAndPostProduct() {
        Preferences preferences = Preferences.getPreferences(getApplicationContext());

        Product product = new Product(
                productName.getText().toString(),
                Float.parseFloat(productPrice.getText().toString()),
                Float.parseFloat(productQuantity.getText().toString()),
                productUnit.getText().toString(),
                preferences.getCurrentUser().userType.toLowerCase(),
                productImageUrl.getText().toString(),
                new ProductDescription(productType.getText().toString(), productSubtype.getText().toString(), productDescriptionText.getText().toString()),
                preferences.getCurrentUser()
        );

        postOneProduct(product);
    }

    private void postOneProduct(Product product) {
        progressDialog.show();

        DataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(DataService.class);

        Call<Product> call = service.postOneProduct(product);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                progressDialog.dismiss();
                if(response.body().error){
                    Log.e("AddProductsFragment", "onResponse: "  + response.body().message);
                    Toast.makeText(AddProductActivity.this, "Error adding product", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddProductActivity.this, "Product added successfully.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("AddProductsFragment", "onFailure: " + t.getMessage());
                Toast.makeText(AddProductActivity.this, "Error adding product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
