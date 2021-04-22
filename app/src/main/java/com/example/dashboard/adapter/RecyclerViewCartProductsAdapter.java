package com.example.dashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.ProductActivity;
import com.example.dashboard.R;
import com.example.dashboard.data.CartTransactions;
import com.example.dashboard.data.SharedProductData;
import com.example.dashboard.model.Product;
import com.example.dashboard.utility.MathUtility;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewCartProductsAdapter extends RecyclerView.Adapter<RecyclerViewCartProductsAdapter.CartProductsCustomViewHolder>{
    private Context context;
    private List<Product> productList;

    private CartTransactions cartTransactions;
    private SharedProductData sharedProduct;

    public RecyclerViewCartProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        cartTransactions = new CartTransactions(this.context);
        sharedProduct = SharedProductData.getInstance();
    }

    class CartProductsCustomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        TextView productNameText;
        TextView productAddedText;
        TextView productSellerText;
        TextView productCost;
        ImageView productImage;
        ImageButton deleteFromCartButton;

        CartProductsCustomViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            productNameText = mView.findViewById(R.id.cart_product_name_text);
            productAddedText = mView.findViewById(R.id.cart_product_ordered_text);
            productSellerText = mView.findViewById(R.id.cart_product_seller_text);
            productCost = mView.findViewById(R.id.cart_product_cost);
            productImage = mView.findViewById(R.id.cart_product_image);
            deleteFromCartButton = mView.findViewById(R.id.cart_product_delete_from_cart_button);
        }
    }

    @NonNull
    @Override
    public CartProductsCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.cart_product_card, parent, false);

        return new CartProductsCustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductsCustomViewHolder holder, int position) {
        holder.productNameText.append(productList.get(position).name);
        holder.productAddedText.append(MathUtility.truncateFloatToTwoDecimalPlaces(productList.get(position).addedQuantity));

        String cost = MathUtility.truncateFloatToTwoDecimalPlaces(productList.get(position).addedQuantity * productList.get(position).price);
        holder.productCost.append(cost);

        if(productList.get(position).seller != null) {
            if(productList.get(position).seller.storeName != null) {
                holder.productSellerText.append(productList.get(position).seller.storeName);
            }
        }

        holder.deleteFromCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductFromCart(productList.get(position));
            }
        });

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        if(productList.get(position).imageUrl != null) {
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedProduct.saveProduct(productList.get(position));

                Intent intent = new Intent(context, ProductActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void deleteProductFromCart(Product product) {
        cartTransactions.deleteProductFromCart(product);
    }
}
