package com.example.dashboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.R;
import com.example.dashboard.model.Product;
import com.example.dashboard.model.order.OrderDetails;
import com.example.dashboard.model.order.OrderUpdateResponse;
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

public class RecyclerViewHomeAdapter extends RecyclerView.Adapter<RecyclerViewHomeAdapter.RecyclerViewHomeAdapterViewHolder> {
    private Context context;
    private List<OrderDetails> orderList;
    private List<Product> productList;

    public RecyclerViewHomeAdapter(Context context, List<OrderDetails> orderList, List<Product> productList) {
        this.context = context;
        this.orderList = orderList;
        this.productList = productList;
    }

    class RecyclerViewHomeAdapterViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView nameText, quantityText, buyerIdText;
        MaterialButton dispatchButton, deliverButton;
        ImageView productImage;
        public RecyclerViewHomeAdapterViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameText = mView.findViewById(R.id.order_card_home_name_text);
            quantityText = mView.findViewById(R.id.order_card_home_quantity_text);
            buyerIdText = mView.findViewById(R.id.order_card_buyer_id);

            dispatchButton = mView.findViewById(R.id.dispatch_button);
            deliverButton = mView.findViewById(R.id.deliver_button);

            productImage = mView.findViewById(R.id.order_card_home_image);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHomeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.orders_card_home, parent, false);

        return new RecyclerViewHomeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHomeAdapterViewHolder holder, int position) {
        if(position < productList.size()) {
            holder.nameText.setText(productList.get(position).name);

            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new OkHttp3Downloader(context));
            if(productList.get(position).imageUrl != null) {
                builder.build().load(productList.get(position).imageUrl)
                        .placeholder(R.drawable.ic_categories_nav)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.productImage);
            }
            else {
                builder.build().load("https://homepages.cae.wisc.edu/~ece533/images/fruits.png")
                        .placeholder(R.drawable.ic_categories_nav)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.productImage);
            }
        }

        holder.quantityText.append(MathUtility.truncateFloatToTwoDecimalPlaces(orderList.get(position).orderedQuantity));

        holder.buyerIdText.append(orderList.get(position).buyerId);

        holder.dispatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder(true, false, orderList.get(position)._id, holder.dispatchButton, holder.deliverButton);
            }
        });

        holder.deliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder(false, true, orderList.get(position)._id, holder.dispatchButton, holder.deliverButton);
            }
        });

        if(orderList.get(position).dispatch.isDispatched) {
            holder.dispatchButton.setEnabled(false);
            holder.deliverButton.setEnabled(true);
            holder.deliverButton.setStrokeColorResource(R.color.colorPrimary);
        }
        if(orderList.get(position).delivery.isDelivered) {
            holder.dispatchButton.setEnabled(false);
            holder.deliverButton.setEnabled(false);
            holder.deliverButton.setStrokeColorResource(R.color.browser_actions_bg_grey);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    private void updateOrder(Boolean markDispatched, Boolean markDelivered, String orderId, MaterialButton dispatchButton, MaterialButton deliveryButton) {
        DataService service = RetrofitClientInstance.getRetrofitInstance().create(DataService.class);

        Call<OrderUpdateResponse> call = service.updateOrder(markDispatched, markDelivered, orderId);

        call.enqueue(new Callback<OrderUpdateResponse>() {
            @Override
            public void onResponse(Call<OrderUpdateResponse> call, Response<OrderUpdateResponse> response) {
                if(response.body().error) {
                    Log.e("HomeAdapter", "onResponse: " + response.body().message);
                    Toast.makeText(context, "Error updating orders", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(markDelivered) {
                        deliveryButton.setEnabled(false);
                        deliveryButton.setStrokeColorResource(R.color.browser_actions_bg_grey);
                        Toast.makeText(context, "Order delivered", Toast.LENGTH_SHORT).show();
                    }
                    if(markDispatched) {
                        dispatchButton.setEnabled(false);
                        deliveryButton.setEnabled(true);
                        deliveryButton.setStrokeColorResource(R.color.colorPrimary);
                        Toast.makeText(context, "Order dispatched", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderUpdateResponse> call, Throwable t) {
                Log.e("HomeAdapter", "onFailure: " + t.getMessage());
                Toast.makeText(context, "Error getting orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
