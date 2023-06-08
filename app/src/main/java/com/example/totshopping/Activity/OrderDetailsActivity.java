package com.example.totshopping.Activity;

import static com.example.totshopping.DatabaseQueries.DbQueries.firebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView title, price, quantity;

    private ImageView productImage, orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;

    private ProgressBar O_P_progress, P_S_progress, S_D_progress;

    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;

    private TextView orderedDate, packedDate, shippedDate, deliveredDate;

    private TextView orderedBody, packedBody, shippedBody, deliveredBody;

    LinearLayout rateNowContainer;
    int rate;
    private TextView fullName, address, pinCode;

    TextView totalItems, totalItemPrice, deliveryPrice, totalAmount, savedAmount;

    Toolbar toolbar;
    int position;

    Dialog loadingDialog, canceledOrderDialog;
    SimpleDateFormat simpleDateFormat;

    Button canceledButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////////////////loading dialog

        ////////////////canceled order dialog
        canceledOrderDialog = new Dialog(this);
        canceledOrderDialog.setContentView(R.layout.order_canceled_layout);
        canceledOrderDialog.setCancelable(true);
        canceledOrderDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        //     canceledOrderDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////////////////canceled order dialog

        position = getIntent().getIntExtra("position", -1);
        MyOrderModel model = DbQueries.orderModelList.get(position);

        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        quantity = findViewById(R.id.product_quantity);
        productImage = findViewById(R.id.product_image);

        orderedIndicator = findViewById(R.id.order_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipped_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        O_P_progress = findViewById(R.id.order_packed_progress);
        P_S_progress = findViewById(R.id.packed_shipped_progress);
        S_D_progress = findViewById(R.id.shipped_delivered_progress);

        orderedTitle = findViewById(R.id.order_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipped_title);
        deliveredTitle = findViewById(R.id.delivery_title);

        orderedDate = findViewById(R.id.order_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.sipped_date);
        deliveredDate = findViewById(R.id.delivery_date);

        orderedBody = findViewById(R.id.order_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipped_body);
        deliveredBody = findViewById(R.id.delivery_body);

        rateNowContainer = findViewById(R.id.rate_now_container);
        fullName = findViewById(R.id.fullName);
        address = findViewById(R.id.address);
        pinCode = findViewById(R.id.pincode);

        totalItems = findViewById(R.id.total_Items);
        totalItemPrice = findViewById(R.id.total_item_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        totalAmount = findViewById(R.id.total_price);
        savedAmount = findViewById(R.id.saved_amount);
        canceledButton = findViewById(R.id.cancel_button);

        title.setText(model.getProductTitle());
        if (!model.getDiscountedPrice().equals("")) {
            price.setText("Rs." + model.getDiscountedPrice() + "/-");
        } else {
            price.setText("Rs." + model.getProductPrice() + "/-");
        }
        quantity.setText(String.valueOf("QTY :" + model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);

        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyy hh:mm aa");
        switch (model.getOrderStatus()) {

            case "Ordered":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredBody.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredIndicator.setVisibility(View.GONE);
                break;
            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.VISIBLE);
                O_P_progress.setProgress(100);

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredBody.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredIndicator.setVisibility(View.GONE);
                break;
            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));


                P_S_progress.setVisibility(View.VISIBLE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.VISIBLE);
                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                shippedBody.setVisibility(View.VISIBLE);
                shippedDate.setVisibility(View.VISIBLE);
                shippedIndicator.setVisibility(View.VISIBLE);
                shippedTitle.setVisibility(View.VISIBLE);

                deliveredBody.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredIndicator.setVisibility(View.GONE);
                break;
            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));


                P_S_progress.setVisibility(View.VISIBLE);
                S_D_progress.setVisibility(View.VISIBLE);
                O_P_progress.setVisibility(View.VISIBLE);
                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                shippedBody.setVisibility(View.VISIBLE);
                shippedDate.setVisibility(View.VISIBLE);
                shippedIndicator.setVisibility(View.VISIBLE);
                shippedTitle.setVisibility(View.VISIBLE);

                deliveredBody.setVisibility(View.VISIBLE);
                deliveredTitle.setVisibility(View.VISIBLE);
                deliveredDate.setVisibility(View.VISIBLE);
                deliveredIndicator.setVisibility(View.VISIBLE);

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Your order is out for delivery.");
                break;
            case "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));


                P_S_progress.setVisibility(View.VISIBLE);
                S_D_progress.setVisibility(View.VISIBLE);
                O_P_progress.setVisibility(View.VISIBLE);
                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                shippedBody.setVisibility(View.VISIBLE);
                shippedDate.setVisibility(View.VISIBLE);
                shippedIndicator.setVisibility(View.VISIBLE);
                shippedTitle.setVisibility(View.VISIBLE);

                deliveredBody.setVisibility(View.VISIBLE);
                deliveredTitle.setVisibility(View.VISIBLE);
                deliveredDate.setVisibility(View.VISIBLE);
                deliveredIndicator.setVisibility(View.VISIBLE);
                break;
            case "Cancelled":
                if (model.getPackedDate().after(model.getOrderedDate())) {
                    if (model.getShippedDate().after(model.getPackedDate())) {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.cancelled)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("your order has been cancelled");


                        P_S_progress.setVisibility(View.VISIBLE);
                        S_D_progress.setVisibility(View.VISIBLE);
                        O_P_progress.setVisibility(View.VISIBLE);
                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);

                        packedIndicator.setVisibility(View.VISIBLE);
                        packedBody.setVisibility(View.VISIBLE);
                        packedDate.setVisibility(View.VISIBLE);
                        packedTitle.setVisibility(View.VISIBLE);

                        shippedBody.setVisibility(View.VISIBLE);
                        shippedDate.setVisibility(View.VISIBLE);
                        shippedIndicator.setVisibility(View.VISIBLE);
                        shippedTitle.setVisibility(View.VISIBLE);

                        deliveredBody.setVisibility(View.VISIBLE);
                        deliveredTitle.setVisibility(View.VISIBLE);
                        deliveredDate.setVisibility(View.VISIBLE);
                        deliveredIndicator.setVisibility(View.VISIBLE);
                    } else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.cancelled)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("your order has been cancelled");

                        P_S_progress.setVisibility(View.VISIBLE);
                        S_D_progress.setVisibility(View.GONE);
                        O_P_progress.setVisibility(View.VISIBLE);
                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);

                        packedIndicator.setVisibility(View.VISIBLE);
                        packedBody.setVisibility(View.VISIBLE);
                        packedDate.setVisibility(View.VISIBLE);
                        packedTitle.setVisibility(View.VISIBLE);

                        shippedBody.setVisibility(View.VISIBLE);
                        shippedDate.setVisibility(View.VISIBLE);
                        shippedIndicator.setVisibility(View.VISIBLE);
                        shippedTitle.setVisibility(View.VISIBLE);

                        deliveredBody.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredIndicator.setVisibility(View.GONE);
                    }
                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.cancelled)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("your order has been cancelled");

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);
                    O_P_progress.setVisibility(View.VISIBLE);
                    O_P_progress.setProgress(100);

                    packedIndicator.setVisibility(View.VISIBLE);
                    packedBody.setVisibility(View.VISIBLE);
                    packedDate.setVisibility(View.VISIBLE);
                    packedTitle.setVisibility(View.VISIBLE);

                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedIndicator.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredBody.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredIndicator.setVisibility(View.GONE);
                }
                break;
        }

        ///////////ratings layout
        rate = model.getRating();
        setPosition(rate);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setPosition(starPosition);
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductId());

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
                            if (DbQueries.myRatedIds.contains(model.getProductId())) {
                                myRating.put("rating_" + DbQueries.myRatedIds.indexOf(model.getProductId()), (long) starPosition + 1);
                            } else {
                                myRating.put("list_size", (long) DbQueries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DbQueries.myRatedIds.size(), model.getProductId());
                                myRating.put("rating_" + DbQueries.myRatedIds.size(), (long) starPosition + 1);
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DbQueries.orderModelList.get(position).setRating(starPosition);
                                        if (DbQueries.myRatedIds.contains(model.getProductId())) {
                                            DbQueries.myRating.set(DbQueries.myRatedIds.indexOf(model.getProductId()), Long.parseLong(String.valueOf(starPosition + 1)));
                                        } else {
                                            DbQueries.myRatedIds.add(model.getProductId());
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

        if (model.isCancellationRequested()) {

            canceledButton.setVisibility(View.VISIBLE);
            canceledButton.setEnabled(false);
            canceledButton.setText("Cancellation in process.");
            canceledButton.setTextColor(getResources().getColor(R.color.red));
            canceledButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

        } else {
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")) {
                canceledButton.setVisibility(View.VISIBLE);
                canceledButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        canceledOrderDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                canceledOrderDialog.dismiss();
                            }
                        });
                        canceledOrderDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                canceledOrderDialog.dismiss();
                                loadingDialog.show();
                                Map<String, Object> map = new HashMap<>();
                                map.put("Order Id", model.getOrderID());
                                map.put("Product Id", model.getProductId());
                                map.put("Order Cancelled", model.getOrderID());
                                firebaseFirestore.collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    firebaseFirestore.collection("ORDERS").document(model.getOrderID()).collection("OrderItems").document(model.getProductId())
                                                            .update("Cancellation requested",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                model.setCancellationRequested(true);
                                                                canceledButton.setEnabled(false);
                                                                canceledButton.setText("Cancellation in process.");
                                                                canceledButton.setTextColor(getResources().getColor(R.color.red));
                                                                canceledButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                            }else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                                } else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        canceledOrderDialog.show();
                    }
                });

            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pinCode.setText(model.getPincode());

        totalItems.setText("Price(" + model.getProductQuantity() + ") items");
        Long totalItemsPriceValue;

        if (model.getDiscountedPrice().equals("")) {
            totalItemsPriceValue = model.getProductQuantity() * Long.parseLong(model.getProductPrice());
            totalItemPrice.setText("Rs." + totalItemsPriceValue + "/-");
        } else {
            totalItemsPriceValue = model.getProductQuantity() * Long.parseLong(model.getDiscountedPrice());
            totalItemPrice.setText("Rs." + totalItemsPriceValue + "/-");
        }

        if (model.getDeliveryPrice().equals("FREE")) {
            deliveryPrice.setText(model.getDeliveryPrice());
            totalAmount.setText(totalItemPrice.getText());
        } else {
            deliveryPrice.setText("Rs." + model.getDeliveryPrice() + "/-");
            totalAmount.setText("Rs." + (totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice())) + "/-");
        }

        if (!model.getCuttedPrice().equals("")) {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved Rs." + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order");
            } else {
                savedAmount.setText("You saved Rs." + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getProductPrice())) + "/- on this order");
            }
        } else {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved Rs." + model.getProductQuantity() * (Long.valueOf(model.getProductPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order");
            } else {
                savedAmount.setText("You saved Rs.0/- on this order");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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