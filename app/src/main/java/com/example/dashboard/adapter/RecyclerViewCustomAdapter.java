package com.example.dashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.ProductActivity;
import com.example.dashboard.R;
import com.example.dashboard.data.CartTransactions;
import com.example.dashboard.data.ProductDatabase;
import com.example.dashboard.data.SharedProductData;
import com.example.dashboard.executer.AppExecutors;
import com.example.dashboard.model.Product;
import com.example.dashboard.utility.MathUtility;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewCustomAdapter extends RecyclerView.Adapter<RecyclerViewCustomAdapter.CustomViewHolder> {
    private Context context;
    private List<Product> productList;

    private CartTransactions cartTransactions;

    private SharedProductData sharedProduct;

    public RecyclerViewCustomAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        cartTransactions = new CartTransactions(this.context);
        sharedProduct = SharedProductData.getInstance();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        TextView productNameText;
        TextView productAvailableText;
        TextView productSellerText;
        TextView productPrice;
        ImageView productImage;
        ImageButton addToCartButton;

        CustomViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            productNameText = mView.findViewById(R.id.product_name_text);
            productAvailableText = mView.findViewById(R.id.product_available_text);
            productSellerText = mView.findViewById(R.id.product_seller_text);
            productPrice = mView.findViewById(R.id.product_price);
            productImage = mView.findViewById(R.id.product_image);
            addToCartButton = mView.findViewById(R.id.product_add_to_cart_button);
        }

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.product_card, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.productNameText.append(productList.get(position).name);
        holder.productAvailableText.append(Float.toString(productList.get(position).quantity));

        String truncatedPrice = MathUtility.truncateFloatToTwoDecimalPlaces(productList.get(position).price);
        holder.productPrice.append(truncatedPrice);

        if(productList.get(position).seller != null) {
            holder.productSellerText.append(productList.get(position).seller.name);
        }

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart(productList.get(position));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedProduct.saveProduct(productList.get(position));
                Toast.makeText(context, sharedProduct.getProduct().name + " was clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, ProductActivity.class);
                context.startActivity(intent);
            }
        });

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        if(productList.get(position).imageUrl != null && productList.get(position).imageUrl.length() != 0) {
            builder.build().load(productList.get(position).imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.productImage);
        }
        else {
            builder.build().load("https://homepages.cae.wisc.edu/~ece533/images/fruits.png")
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void addProductToCart(Product product) {
        //TODO Implement adding desired quantity of product to cart
        product.addedQuantity = 1.0f;

        cartTransactions.updateOrInsertInCart(product);
    }
}
