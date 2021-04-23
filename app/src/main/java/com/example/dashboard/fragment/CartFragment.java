package com.example.dashboard.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.ProductsActivity;
import com.example.dashboard.R;
import com.example.dashboard.adapter.RecyclerViewCartProductsAdapter;
import com.example.dashboard.adapter.RecyclerViewCustomAdapter;
import com.example.dashboard.cart.CartViewModel;
import com.example.dashboard.data.CartTransactions;
import com.example.dashboard.data.Preferences;
import com.example.dashboard.data.ProductDatabase;
import com.example.dashboard.executer.AppExecutors;
import com.example.dashboard.model.Product;
import com.example.dashboard.model.User;
import com.example.dashboard.model.order.OrderRequest;
import com.example.dashboard.model.order.OrderResponse;
import com.example.dashboard.network.DataService;
import com.example.dashboard.network.RetrofitClientInstance;
import com.example.dashboard.utility.MathUtility;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private ProductDatabase productDatabase;

    private RecyclerView recyclerView;
    private RecyclerViewCartProductsAdapter recyclerViewCartProductsAdapter;

    private LinearLayout cartTotalCostLL;
    private TextView totalCost;
    private MaterialButton placeOrderButton;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartTotalCostLL = view.findViewById(R.id.fragment_card_linear_layout);
        totalCost = view.findViewById(R.id.fragment_cart_total_text_view);
        placeOrderButton = view.findViewById(R.id.fragment_cart_place_order_button);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Placing Order...");

        productDatabase = ProductDatabase.getInstance(getActivity());
        productDatabase.productDao().getProductList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                if(productList != null) {
                    if (productList.size() == 0) {
                        cartTotalCostLL.setVisibility(View.GONE);
                    } else {
                        cartTotalCostLL.setVisibility(View.VISIBLE);
                    }
                    generateProductsList(view, productList);
                }
            }
        });
        productDatabase.productDao().getTotalCost().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                totalCost.setText("Total: " + MathUtility.truncateFloatToTwoDecimalPlaces(aFloat));
            }
        });

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductsFromDatabaseAndPlaceOrders();
            }
        });
    }

    private void generateProductsList(@NonNull View view, List<Product> productList) {
        recyclerView = view.findViewById(R.id.fragment_cart_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewCartProductsAdapter = new RecyclerViewCartProductsAdapter(getContext(), productList);
        recyclerView.setAdapter(recyclerViewCartProductsAdapter);
    }

    private void getProductsFromDatabaseAndPlaceOrders() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Product> productList = productDatabase.productDao().getProductListAsList();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        placeCartOrder(productList);
                    }
                });

            }
        });
    }

    private void placeCartOrder(List<Product> productList) {
//        if(productList == null) {
//            Log.e("CartFragment.java", "placeCartOrder: " + "productList was null.");
//            return;
//        }
        DataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(DataService.class);

        Preferences preferences = Preferences.getPreferences(getActivity().getApplicationContext());
        User currentUser = preferences.getCurrentUser();

        CartTransactions cartTransactions = new CartTransactions(getContext());

        for(Product product: productList) {
            progressDialog.show();
            Log.e("CartFragment.java", "placeCartOrder: " + currentUser._id);
            OrderRequest orderRequest = new OrderRequest(currentUser._id, product._id, product.seller._id, product.addedQuantity);
            Call<OrderResponse> call = service.placeOrder(orderRequest);

            call.enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    progressDialog.dismiss();
                    if(response.body().error) {
                        Log.e("CartFragment.java", "onResponse: " + response.body().message);
                        Toast.makeText(getContext(), "Error placing orders.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        cartTransactions.deleteProductFromCart(product);
                        Toast.makeText(getContext(), "Order placed successfully.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("CartFragment.java", "onFailure: " + t.getMessage());
                    Toast.makeText(getContext(), "Error placing orders.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
