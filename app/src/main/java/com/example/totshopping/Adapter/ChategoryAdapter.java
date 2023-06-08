package com.example.totshopping.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.CategoryActivity;
import com.example.totshopping.Model.CategoryModel;
import com.example.totshopping.R;

import java.util.ArrayList;
import java.util.List;

public class ChategoryAdapter extends RecyclerView.Adapter<ChategoryAdapter.viewHolder> {

    private List<CategoryModel> categoryModelList=new ArrayList<>();
    int lastPosition=-1;

    public ChategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.catagory_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String icon=categoryModelList.get(position).getCategoryIcon();
        String name=categoryModelList.get(position).getCategoryName();
        holder.categoryName.setText(name);
        holder.setcategoryIcon(icon);

        if (!categoryModelList.get(position).getCategoryName().equals("")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != 0) {
                        Intent intent = new Intent(holder.itemView.getContext(), CategoryActivity.class);
                        intent.putExtra("name", name);
                        holder.itemView.getContext().startActivity(intent);
                    }
                }
            });

            if (lastPosition < position){
                Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fade_in);
                holder.itemView.setAnimation(animation);
                lastPosition = position;
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView categoryIcon;
        TextView categoryName;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon=itemView.findViewById(R.id.category_icon);
            categoryName=itemView.findViewById(R.id.category_name);
        }
        private void setcategoryIcon(String iconUrl){
            if (iconUrl !=null){
                Glide.with(itemView.getContext()).load(iconUrl).placeholder(R.drawable.cotagory_home).into(categoryIcon);
            }else {
                categoryIcon.setImageResource(R.drawable.ny_home);
            }
    }
    }
}
