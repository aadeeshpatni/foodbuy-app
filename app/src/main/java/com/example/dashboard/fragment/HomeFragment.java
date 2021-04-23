package com.example.dashboard.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dashboard.AddProductActivity;
import com.example.dashboard.MainActivity;
import com.example.dashboard.R;
import com.example.dashboard.data.Preferences;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    MaterialButton addProductButton;
    Button contactUsButton, storesNearYou, yourOrders;

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

        Preferences preferences = Preferences.getPreferences(getActivity().getApplicationContext());
        if(preferences.getCurrentUser().userType.equals("customer")) {
            addProductButton.setVisibility(View.GONE);
        }
        else if(preferences.getCurrentUser().userType.equals("wholesaler")) {
            storesNearYou.setVisibility(View.INVISIBLE);
            yourOrders.setVisibility(View.INVISIBLE);
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
}
