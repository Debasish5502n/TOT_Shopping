package com.example.totshopping.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.ProductDetailsActivity;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.R;

import java.util.List;


public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.myViewholder> {

    List<HorizontalScrollModel> horizontalScrollModelList;

    public HorizontalScrollAdapter(List<HorizontalScrollModel> horizontalScrollModelList) {
        this.horizontalScrollModelList = horizontalScrollModelList;
    }

    @NonNull
    @Override
    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);
        return new myViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewholder holder, int position) {
        Glide.with(holder.imageView.getContext()).load(horizontalScrollModelList.get(position).getProductImage()).into(holder.imageView);
        holder.title.setText(horizontalScrollModelList.get(position).getProductTitle());
        holder.desc.setText(horizontalScrollModelList.get(position).getProductDescription());
        holder.price.setText("Rs."+horizontalScrollModelList.get(position).getProductPrice()+"/-");

        if (!horizontalScrollModelList.get(position).getProductTitle().equals("")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.desc.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID",horizontalScrollModelList.get(position).getProductID());
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (horizontalScrollModelList.size() >8){
            return 8;
        }else {
            return horizontalScrollModelList.size();
        }
    }

    public class myViewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,desc,price;
        public myViewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.h_s_item);
            title=itemView.findViewById(R.id.h_s_title);
            desc=itemView.findViewById(R.id.h_s_decs);
            price=itemView.findViewById(R.id.h_s_price);
        }
    }
}
