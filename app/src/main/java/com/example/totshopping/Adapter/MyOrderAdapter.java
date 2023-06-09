package com.example.totshopping.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.OrderDetailsActivity;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.MyOrderModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.myViewHolder> {

    List<MyOrderModel> myOrderModelList;
    Dialog loadingDialog;

    public MyOrderAdapter(List<MyOrderModel> myOrderModelList, Dialog loadingDialog) {
        this.myOrderModelList = myOrderModelList;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String productImage = myOrderModelList.get(position).getProductImage();
        String productId = myOrderModelList.get(position).getProductId();
        String productTitle = myOrderModelList.get(position).getProductTitle();
        int rate = myOrderModelList.get(position).getRating();

        String orderStatus = myOrderModelList.get(position).getOrderStatus();
        Date date;

        switch (orderStatus) {

            case "Ordered":
                date = myOrderModelList.get(position).getOrderedDate();
                break;
            case "Packed":
                date = myOrderModelList.get(position).getPackedDate();
                break;
            case "Shipped":
                date = myOrderModelList.get(position).getShippedDate();
                break;
            case "Delivered":
                date = myOrderModelList.get(position).getDeliveredDate();
                break;
            case "Cancelled":
                date = myOrderModelList.get(position).getCancelledDate();
                break;
            default:
                date = myOrderModelList.get(position).getCancelledDate();
        }


        holder.setData(productImage, productTitle, orderStatus, date, rate, productId, position);


    }

    @Override
    public int getItemCount() {
        return myOrderModelList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage, deliveryIndicator;
        TextView productTitle, deliveryDate;
        LinearLayout rateNowContainer;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            deliveryIndicator = itemView.findViewById(R.id.order_indicator);
            productTitle = itemView.findViewById(R.id.product_title);
            deliveryDate = itemView.findViewById(R.id.order_delivery_date);
            rateNowContainer = itemView.findViewById(R.id.rate_now_container);
        }

        private void setData(String resource, String title, String orderStatus, Date date, int rate, String productId, int position) {
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("position", position);
                    itemView.getContext().startActivity(intent);
                }
            });

            if (orderStatus.equals("Canceled")) {
                deliveryIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
            } else {
                deliveryIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.successGreen)));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyy hh:mm aa");
            deliveryDate.setText(orderStatus + String.valueOf(simpleDateFormat.format(date)));
            ///////////ratings layout
            setPosition(rate);
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                final int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        setPosition(starPosition);
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId);

                        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                            @Nullable
                            @Override
                            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                if (rate != 0) {
                                    Long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                    Long decrease = documentSnapshot.getLong(rate + 1 + "_star") - 1;
                                    transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                    transaction.update(documentReference, rate + 1 + "_star", decrease);
                                } else {
                                    Long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                    transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                }

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Object>() {
                            @Override
                            public void onSuccess(Object o) {

                                Map<String, Object> myRating = new HashMap<>();
                                if (DbQueries.myRatedIds.contains(productId)) {
                                    myRating.put("rating_" + DbQueries.myRatedIds.indexOf(productId), (long) starPosition + 1);
                                } else {
                                    myRating.put("list_size", (long) DbQueries.myRatedIds.size() + 1);
                                    myRating.put("product_ID_" + DbQueries.myRatedIds.size(), productId);
                                    myRating.put("rating_" + DbQueries.myRatedIds.size(), (long) starPosition + 1);
                                }

                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                        .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DbQueries.orderModelList.get(position).setRating(starPosition);
                                            if (DbQueries.myRatedIds.contains(productId)) {
                                                DbQueries.myRating.set(DbQueries.myRatedIds.indexOf(productId), Long.parseLong(String.valueOf(starPosition + 1)));
                                            } else {
                                                DbQueries.myRatedIds.add(productId);
                                                DbQueries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismiss();
                            }
                        });
                    }
                });

            }
            ///////////ratings layout
        }

        private void setPosition(int starPosition) {
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (x <= starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
        }
    }
}
