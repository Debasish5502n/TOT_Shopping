package com.example.totshopping.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.totshopping.Adapter.CartAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.CartItemModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    Toolbar toolbar;
    RecyclerView deliveryRecyclerView;
    Button add_or_change_address;
    public static final int SELECT_ADDRESS = 0;
    TextView totalAmount;
    TextView fullName, address, pinCode;
    String number, name;
    public static Dialog loadingDialog;
    Dialog paymentMethodDialog;
    AppCompatButton continueBtn;
    ImageView payment, cod;
    TextView codTitle;
    View divider;
    ConstraintLayout orderConformationLayout;
    ImageView continueShopping;
    TextView orderID;
    private boolean successResponse = false;
    public static boolean fromCart;
    public static boolean codConformationLayout = false;
    String order_id;
    public static boolean getQtyIDs = true;
    FirebaseFirestore firebaseFirestore;
    public static CartAdapter adapter;
    private String paymentMethod = "PAYTM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delivery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerView);
        add_or_change_address = findViewById(R.id.add_or_change_address);
        fullName = findViewById(R.id.fullName);
        address = findViewById(R.id.address);
        pinCode = findViewById(R.id.pincode);
        totalAmount = findViewById(R.id.total_Items);
        continueBtn = findViewById(R.id.continue_btn);
        orderConformationLayout = findViewById(R.id.order_conformation_layout);
        orderID = findViewById(R.id.your_order);
        continueShopping = findViewById(R.id.continue_shopping);
        order_id = UUID.randomUUID().toString().substring(0, 28);
        firebaseFirestore = FirebaseFirestore.getInstance();

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////////////////loading dialog

        ////////////////Payment method dialog
        paymentMethodDialog = new Dialog(this);
        paymentMethodDialog.setContentView(R.layout.payment_method_layout);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        payment = paymentMethodDialog.findViewById(R.id.paytm);
        cod = paymentMethodDialog.findViewById(R.id.cod);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_title);
        divider = paymentMethodDialog.findViewById(R.id.divider);
        ////////////////Payment method dialog
        getQtyIDs = true;

        LinearLayoutManager layoutInflater = new LinearLayoutManager(this);
        layoutInflater.setOrientation(RecyclerView.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutInflater);

        adapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        add_or_change_address.setVisibility(View.VISIBLE);
        add_or_change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductAvailable = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCod()) {
                            cod.setEnabled(false);
                            codTitle.setAlpha(0.5f);
                            cod.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            codTitle.setAlpha(1f);
                            cod.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductAvailable) {
                    paymentMethodDialog.show();
                }

            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                placeOrderDetails();

            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "PAYTM";
                placeOrderDetails();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    String quantityDocument = UUID.randomUUID().toString().substring(0, 20);
                    Map<String, Object> timeStamp = new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    int finalX = x;
                    int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(quantityDocument).set(timeStamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocument);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {
                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductId()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<>();

                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }

                                                                long availableQty = 0;
                                                                boolean noLongerAvailable = true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                    cartItemModelList.get(finalX).setQtyError(false);

                                                                    if (!serverQuantity.contains(qtyId)) {
                                                                        if (noLongerAvailable) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        } else {
                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry ! all product may not be available in required quantity....", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } else {
                                                                        availableQty++;
                                                                        noLongerAvailable = false;
                                                                    }
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });

                                        }

                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }

            }
        } else {
            getQtyIDs = true;
        }

        try {
            name = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getName();
            number = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getMobileNo();
            if (DbQueries.addressesModelList.get(DbQueries.selectedAddress).getAlternativeMobileNo().equals("")) {
                fullName.setText(name + " - " + number);
            }else {
                fullName.setText(name + " - " + number + " or " +DbQueries.addressesModelList.get(DbQueries.selectedAddress).getAlternativeMobileNo());
            }
            String city = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getCity();
            String locality = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getLocality();
            String flatNo = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getFlatNo();
            String landMark = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getLandMark();
            String state = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getState();

            if (landMark.equals("")){
                address.setText(flatNo + ", " + locality + ", " + city + ", " + state);

            }else {
                address.setText(flatNo + ", " + locality + ", " + landMark + ", " + city + ", " + state);
            }
            pinCode.setText(DbQueries.addressesModelList.get(DbQueries.selectedAddress).getPinCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (codConformationLayout) {
            showOrderConformationLayout();
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

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        int finalX = x;
                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                            cartItemModelList.get(finalX).getQtyIDs().clear();

                                        }
                                    }
                                });
                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            startActivity(new Intent(DeliveryActivity.this, MainActivity.class));
            DbQueries.clearData();
            return;
        }
        super.onBackPressed();
    }

    private void showOrderConformationLayout() {
        successResponse = true;
        codConformationLayout = false;

        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                FirebaseFirestore.getInstance().collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }
        }

        if (MainActivity.MainActivity != null) {
            MainActivity.MainActivity.finish();
            MainActivity.MainActivity = null;
            MainActivity.showCart = false;
        } else {
            MainActivity.resetFragment = true;
        }
        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }
        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            List<Integer> indexList = new ArrayList<>();

            for (int x = 0; x < DbQueries.cartList.size(); x++) {
                if (!DbQueries.cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, DbQueries.cartItemModelList.get(x).getProductId());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
                loadingDialog.dismiss();
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        for (int x = 0; x < indexList.size(); x++) {
//                              DbQueries.cartList.remove(indexList.get(x).intValue());
//                              DbQueries.cartItemModelList.remove(indexList.get(x).intValue());
//                            DbQueries.cartItemModelList.remove(DbQueries.cartItemModelList.size() - 1);
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }

        add_or_change_address.setEnabled(false);
        continueBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderConformationLayout.setVisibility(View.VISIBLE);
        orderID.setText("Order id-" + order_id);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DeliveryActivity.this, MainActivity.class));
                DbQueries.clearData();

            }
        });
    }

    private void placeOrderDetails() {
        loadingDialog.show();
        String userId = FirebaseAuth.getInstance().getUid();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Delivery Price", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("Product Id", cartItemModel.getProductId());
                orderDetails.put("User Id", userId);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                } else {
                    orderDetails.put("Cutted Price", "");
                }
                orderDetails.put("Product Price", cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCouponId() != null) {
                    orderDetails.put("Coupon Id", cartItemModel.getSelectedCouponId());
                } else {
                    orderDetails.put("Coupon Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                } else {
                    orderDetails.put("Discounted Price", "");
                }
                orderDetails.put("Ordered Date", FieldValue.serverTimestamp());
                orderDetails.put("Packed Date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped Date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered Date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled Date", FieldValue.serverTimestamp());
                orderDetails.put("Order Status", "Ordered");
                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", address.getText());
                orderDetails.put("FullName", fullName.getText());
                orderDetails.put("PinCode", pinCode.getText());
                orderDetails.put("Free Coupons", cartItemModel.getFreeCoupon());
                orderDetails.put("Cancellation requested", false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductId())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount", cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status", "Not paid");
                orderDetails.put("Order Status", "Canceled");

                firebaseFirestore.collection("ORDERS").document(order_id).
                        set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (paymentMethod.equals("PAYTM")) {
                                paytm();
                            } else {
                                cod();
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void paytm() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.dismiss();

        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("Payment Status", "paid");
        orderDetails.put("Order Status", "Ordered");
        firebaseFirestore.collection("ORDERS").document(order_id).
                update(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> orderId = new HashMap<>();
                    orderId.put("order_id", order_id);
                    orderId.put("time",FieldValue.serverTimestamp());
                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDER").document(order_id).set(orderId)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showOrderConformationLayout();
                                    } else {
                                        Toast.makeText(DeliveryActivity.this, "Failed to update user order list", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(DeliveryActivity.this, "ORDER CANCELED", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cod() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent intent = new Intent(DeliveryActivity.this, OtpVerificationActivity.class);
        intent.putExtra("mobileNo", "+91" + number.substring(0, 10));
        intent.putExtra("orderId", order_id);
        startActivity(intent);
    }
}