package com.example.dashboard.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.AddProductActivity;
import com.example.dashboard.MainActivity;
import com.example.dashboard.R;
import com.example.dashboard.adapter.RecyclerViewHomeAdapter;
import com.example.dashboard.data.Preferences;
import com.example.dashboard.model.Product;
import com.example.dashboard.model.order.OrderDetails;
import com.example.dashboard.model.order.UserOrdersResponse;
import com.example.dashboard.network.DataService;
import com.example.dashboard.network.RetrofitClientInstance;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    MaterialButton addProductButton;
    Button contactUsButton, storesNearYou, yourOrders;
    TextView homeOrdersText;

    RecyclerView recyclerView;
    RecyclerViewHomeAdapter recyclerViewHomeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addProductButton = getView().findViewById(R.id.fragment_home_add_product_button);
        contactUsButton = getView().findViewById(R.id.contact_us);
        storesNearYou = getView().findViewById(R.id.stores_near_you);
        yourOrders = getView().findViewById(R.id.your_orders);
        homeOrdersText = getView().findViewById(R.id.home_orders_text);
        recyclerView = getView().findViewById(R.id.fragment_home_recycler_view);

        Preferences preferences = Preferences.getPreferences(getActivity().getApplicationContext());
        if(preferences.getCurrentUser().userType.equals("customer")) {
            addProductButton.setVisibility(View.GONE);
            homeOrdersText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
        else if(preferences.getCurrentUser().userType.equals("wholesaler")) {
            storesNearYou.setVisibility(View.INVISIBLE);
            yourOrders.setVisibility(View.INVISIBLE);
            getOrdersPlacedToUser(preferences.getCurrentUser()._id);
        }
        else {
            getOrdersPlacedToUser(preferences.getCurrentUser()._id);
        }

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddProductActivity.class);
                startActivity(intent);
            }
        });


        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Opening default Email client", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("f20180605@hyderabad.bits-pilani.ac.in") +
                        "?subject=" + Uri.encode("") +
                        "&body=" + Uri.encode("");
                Uri uri = Uri.parse(uriText);

                intent.setData(uri);
                try {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }catch(android.content.ActivityNotFoundException e) {
                    Toast.makeText(getContext(),
                            "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        yourOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YourOrderFragment yourOrderFragment = new YourOrderFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, yourOrderFragment, "HomeFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    private void getOrdersPlacedToUser(String sellerId) {
        DataService service = RetrofitClientInstance.getRetrofitInstance().create(DataService.class);

        Call<UserOrdersResponse> call = service.getOrdersPlacedToYou(sellerId);

        call.enqueue(new Callback<UserOrdersResponse>() {
            @Override
            public void onResponse(Call<UserOrdersResponse> call, Response<UserOrdersResponse> response) {
                if(response.body().error) {
                    Log.e("HomeFragment", "onResponse: " + response.body().message);
                    Toast.makeText(getActivity(), "Error getting orders placed to you.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(response.body().orders != null && response.body().products != null)
                        generateOrderList(response.body().orders, response.body().products);
                    else
                        Toast.makeText(getActivity(), "You have no orders placed to you.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserOrdersResponse> call, Throwable t) {
                Log.e("HomeFragment", "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), "Error getting orders placed to you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateOrderList(List<OrderDetails> orderList, List<Product> productList) {


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewHomeAdapter = new RecyclerViewHomeAdapter(getContext(), orderList, productList);
        recyclerView.setAdapter(recyclerViewHomeAdapter);
    }
}
