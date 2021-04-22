package com.example.dashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.R;
import com.example.dashboard.data.CartTransactions;
import com.example.dashboard.data.SharedOrderData;
import com.example.dashboard.data.SharedProductData;
import com.example.dashboard.model.Product;
import com.example.dashboard.model.order.OrderDetails;
import com.example.dashboard.model.order.OrderResponse;
import com.google.android.material.card.MaterialCardView;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewOrdersAdapter extends RecyclerView.Adapter<RecyclerViewOrdersAdapter.RecyclerViewOrdersViewHolder> {
    private Context context;
    private List<OrderDetails> orderList;
    private List<Product> productList;

    private SharedProductData sharedProduct;
    private SharedOrderData sharedOrder;

    public RecyclerViewOrdersAdapter(Context context, List<OrderDetails> orderList, List<Product> productList) {
        this.context = context;
        this.orderList = orderList;
        this.productList = productList;
        sharedProduct = SharedProductData.getInstance();
        sharedOrder = SharedOrderData.getInstance();
    }

    class RecyclerViewOrdersViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        MaterialCardView orderCardView;
        ImageView orderCardImage;
        TextView orderCardName, orderCardTracker;

        RecyclerViewOrdersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            orderCardView = mView.findViewById(R.id.order_card_view);
            orderCardImage = mView.findViewById(R.id.order_card_image);
            orderCardTracker = mView.findViewById(R.id.order_card_tracker_text);
            orderCardName = mView.findViewById(R.id.order_card_name_text);
        }
    }

    @NonNull
    @Override
    public RecyclerViewOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.order_card, parent, false);

        return new RecyclerViewOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewOrdersViewHolder holder, int position) {
        if(productList.size() > position)
            holder.orderCardName.setText(productList.get(position).name);

        if(orderList.get(position) != null && orderList.get(position).dispatch != null) {
            if(orderList.get(position).dispatch.isDispatched) {
                holder.orderCardTracker.setText("Your order is on the way.");
            }
            else {
                holder.orderCardTracker.setText("Order placed. Will dispatch soon.");
            }
        }

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        if(productList.get(position).imageUrl != null) {
            builder.build().load(productList.get(position).imageUrl)
                    .placeholder(R.drawable.ic_categories_nav)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.orderCardImage);
        }
        else {
            builder.build().load("https://homepages.cae.wisc.edu/~ece533/images/fruits.png")
                    .placeholder(R.drawable.ic_categories_nav)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.orderCardImage);
        }


        holder.orderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedProduct.saveProduct(productList.get(position));
                sharedOrder.saveOrder(orderList.get(position));

                Toast.makeText(context, sharedProduct.getProduct().name + "  " + sharedOrder.getOrder().buyerId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
