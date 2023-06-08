package com.example.totshopping.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.totshopping.Model.ProductSpecifiactionModel;
import com.example.totshopping.R;

import java.util.List;

public class ProductSpecificationAdapter extends RecyclerView.Adapter<ProductSpecificationAdapter.myViewHolder> {

    private List<ProductSpecifiactionModel> productSpecifiactionModels;

    public ProductSpecificationAdapter(List<ProductSpecifiactionModel> productSpecifiactionModels) {
        this.productSpecifiactionModels = productSpecifiactionModels;
    }

    @Override
    public int getItemViewType(int position) {
        switch (productSpecifiactionModels.get(position).getType()) {
            case 0:
                return ProductSpecifiactionModel.SPECIFICATION_TITLE;
            case 1:
                return ProductSpecifiactionModel.SPECIFICATION_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ProductSpecifiactionModel.SPECIFICATION_TITLE:
                TextView title = new TextView(parent.getContext());
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(setDp(16, parent.getContext()),
                        setDp(16, parent.getContext())
                        , setDp(16, parent.getContext())
                        , setDp(8, parent.getContext()));
                title.setLayoutParams(layoutParams);
                return new myViewHolder(title);
            case ProductSpecifiactionModel.SPECIFICATION_BODY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_single_layout, parent, false);
                return new myViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        switch (productSpecifiactionModels.get(position).getType()) {
            case ProductSpecifiactionModel.SPECIFICATION_TITLE:
                holder.setTitle(productSpecifiactionModels.get(position).getTitle());
                break;
            case ProductSpecifiactionModel.SPECIFICATION_BODY:
                holder.setFeature(productSpecifiactionModels.get(position).getFeatureName(), productSpecifiactionModels.get(position).getFeatureValue());
                break;
            default:
                return;
        }

    }

    @Override
    public int getItemCount() {
        return productSpecifiactionModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tv_specification_feature, tv_specification_value;
        TextView title;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        private void setTitle(String featureTitle) {
            title = (TextView) itemView;
            title.setText(featureTitle);
        }

        private void setFeature(String featureTitle, String featureDetails) {
            tv_specification_feature = itemView.findViewById(R.id.tv_specification_feature);
            tv_specification_value = itemView.findViewById(R.id.tv_specification_value);
            tv_specification_feature.setText(featureTitle);
            tv_specification_value.setText(featureDetails);
        }
    }

    private int setDp(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
