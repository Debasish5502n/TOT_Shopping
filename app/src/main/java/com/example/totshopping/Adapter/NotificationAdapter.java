package com.example.totshopping.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Model.NotificationModel;
import com.example.totshopping.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.myViewHolder> {

    private List<NotificationModel> notificationModels=new ArrayList<>();

    public NotificationAdapter(List<NotificationModel> notificationModels) {
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String image=notificationModels.get(position).getImage();
        String title=notificationModels.get(position).getBody();
        boolean readed=notificationModels.get(position).isReaded();

        holder.setData(image,title,readed);
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textView=itemView.findViewById(R.id.textView);

        }
        private void setData(String image,String title,Boolean readed){
            Glide.with(itemView.getContext()).load(image).into(imageView);
            if (readed){
                textView.setAlpha(0.5f);
            }else {
                textView.setAlpha(1f);
            }
            textView.setText(title);
        }
    }
}
