package com.daveace.productcatalog.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daveace.productcatalog.R;
import com.daveace.productcatalog.interfaces.CatalogItemLongClickListener;
import com.daveace.productcatalog.interfaces.ScanListener;
import com.daveace.productcatalog.model.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {


    private List<Product> products;

    private CatalogItemLongClickListener longClickListener;

    private ScanListener scanListener;

    public CatalogAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        final Product product = products.get(position);

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .into(holder.productImage);

        Glide.with(holder.itemView.getContext())
                .load(product.getCodeUrl())
                .into(holder.codeImageView);
        holder.productNameView.setText(product.getName());
        holder.scanButton.setOnClickListener(view -> {
            //scan the content of the coded image
            scanListener.onScanClick(holder.codeImageView, holder.productCodeView);
        });
        holder.itemView.setOnLongClickListener(view -> {
            longClickListener.setOnItemLongClick(product, holder.itemView);
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setLongClickListener(CatalogItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setScanListener(ScanListener scanListener) {
        this.scanListener = scanListener;
    }

    public class CatalogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_image)
        ImageView productImage;

        @BindView(R.id.code_image)
        ImageView codeImageView;

        @BindView(R.id.product_name)
        TextView productNameView;

        @BindView(R.id.product_code)
        TextView productCodeView;

        @BindView(R.id.scan_button)
        Button scanButton;

        public CatalogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

