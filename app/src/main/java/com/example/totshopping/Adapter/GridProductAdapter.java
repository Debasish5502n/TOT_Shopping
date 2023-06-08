package com.example.totshopping.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.ProductDetailsActivity;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.R;

import java.util.List;

public class GridProductAdapter extends BaseAdapter {

    List<HorizontalScrollModel> horizontalScrollModelList;

    public GridProductAdapter(List<HorizontalScrollModel> horizontalScrollModelList) {
        this.horizontalScrollModelList = horizontalScrollModelList;
    }

    @Override
    public int getCount() {
        return horizontalScrollModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(parent.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID",horizontalScrollModelList.get(position).getProductID());
                    parent.getContext().startActivity(intent);
                }
            });
            ImageView imageView=view.findViewById(R.id.h_s_item);
            TextView title=view.findViewById(R.id.h_s_title);
            TextView desc=view.findViewById(R.id.h_s_decs);
            TextView price=view.findViewById(R.id.h_s_price);

            Glide.with(price.getContext()).load(horizontalScrollModelList.get(position).getProductImage()).into(imageView);
            title.setText(horizontalScrollModelList.get(position).getProductTitle());
            desc.setText(horizontalScrollModelList.get(position).getProductDescription());
            price.setText("Rs."+horizontalScrollModelList.get(position).getProductPrice()+"/-");
        }else {
            view=convertView;
        }
        return view;
    }
}
