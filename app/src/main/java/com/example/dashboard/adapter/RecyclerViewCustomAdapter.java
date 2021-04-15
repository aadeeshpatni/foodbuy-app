package com.example.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.R;
import com.example.dashboard.model.Product;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewCustomAdapter extends RecyclerView.Adapter<RecyclerViewCustomAdapter.CustomViewHolder> {
    private Context context;
    private List<Product> productList;

    public RecyclerViewCustomAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        TextView productNameText;
        TextView productAvailableText;
        TextView productSellerText;
        ImageView productImage;

        CustomViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            productNameText = mView.findViewById(R.id.product_name_text);
            productAvailableText = mView.findViewById(R.id.product_available_text);
            productSellerText = mView.findViewById(R.id.product_seller_text);
            productImage = mView.findViewById(R.id.product_image);
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
        if(productList.get(position).seller != null) {
            holder.productSellerText.append(productList.get(position).seller.name);
        }

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load("https://homepages.cae.wisc.edu/~ece533/images/fruits.png")
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
