package com.example.totshopping.Adapter;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.DeliveryActivity;
import com.example.totshopping.Activity.MainActivity;
import com.example.totshopping.Activity.ProductDetailsActivity;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.CartItemModel;
import com.example.totshopping.Model.RewardModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    int lastPosition = -1;
    TextView cartTotalAmount;
    boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_ITEM;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_layout, parent, false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_ITEM:
                View totalAmountView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new CartTotalViewHolder(totalAmountView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:

                String productId = cartItemModelList.get(position).getProductId();
                String resource = cartItemModelList.get(position).getProductImage();
                long offersApplied = cartItemModelList.get(position).getOffersApplied();
                long freeCoupon = cartItemModelList.get(position).getFreeCoupon();
                String productTitle = cartItemModelList.get(position).getProductTitle();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                boolean inStock = cartItemModelList.get(position).isInStock();
                long productQuantity = cartItemModelList.get(position).getProductQuantity();
                long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyId = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean cod=cartItemModelList.get(position).isCod();

                ((CartItemViewHolder) holder).setItemDetails(productId, resource, productTitle, freeCoupon, productPrice, cuttedPrice, offersApplied, position, inStock, String.valueOf(productQuantity), maxQuantity, qtyError, qtyId, stockQty,cod);
                break;
            case CartItemModel.TOTAL_ITEM:
                int totalItem = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;

                for (int x = 0; x < cartItemModelList.size(); x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {
                        int quantity=Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItem = totalItem + quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                        }else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) *quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) *quantity;
                            }
                        }else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) *quantity;
                            }
                        }

                    }
                }
                if (totalItemPrice > 500) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                } else {
                    deliveryPrice = "60";
                    totalAmount = 60 + totalItemPrice;
                }

                cartItemModelList.get(position).setTotalItems(totalItem);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalViewHolder) holder).setTotalItems(totalItem, totalItemPrice, deliveryPrice, totalAmount, savedAmount);
                break;
            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageView freeCouponIcons;
        LinearLayout delete;
        TextView productTitle, freeCoupon, productPrice, productCuttedPrice, offersApplied, couponsApplied, productQuantity;
        LinearLayout couponRedemption;
        AppCompatButton redemBtn;
        TextView tvCouponRedeem;

        ////////////coupon Dialog
        private TextView couponTitle;
        private TextView couponExpiryDate;
        private TextView couponBody;
        private RecyclerView couponRecyclerView;
        private LinearLayout selectedCoupon;
        TextView badgeCount;
        String productOriginalPrice;
        ImageView codIndicator;

        ////////////coupon Dialog

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            freeCouponIcons = itemView.findViewById(R.id.free_coupon_icon);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupon = itemView.findViewById(R.id.tv_free_coupon);
            productPrice = itemView.findViewById(R.id.product_price);
            productCuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offer_applied);
            couponsApplied = itemView.findViewById(R.id.coupon_applied);
            productQuantity = itemView.findViewById(R.id.tv_product_qty);
            delete = itemView.findViewById(R.id.removie_item);
            couponRedemption = itemView.findViewById(R.id.coupon_redemption_layout);
            redemBtn = itemView.findViewById(R.id.coupon_redeemption_btn);
            tvCouponRedeem = itemView.findViewById(R.id.tv_coupon_redeemption);
            codIndicator=itemView.findViewById(R.id.cod_indicator);
        }

        private void setItemDetails(String productId, String resource, String title, long freeCouponsNo, String productPriceText, String productCuttedPriceText, long offersAppliedText, int position, boolean inStock, String quantity, long maxQuantity, boolean qtyError, List<String> qtyId, long stockQty,boolean cod) {
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);

            final Dialog couponRedemptionDialog = new Dialog(itemView.getContext());
            couponRedemptionDialog.setContentView(R.layout.coupen_redeemption_dialog);
            couponRedemptionDialog.setCancelable(false);
            couponRedemptionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (cod){
                codIndicator.setVisibility(View.VISIBLE);
            }else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock) {
                if (freeCouponsNo > 0) {
                    freeCouponIcons.setVisibility(View.VISIBLE);
                    freeCoupon.setVisibility(View.VISIBLE);
                    if (freeCouponsNo == 1) {
                        freeCoupon.setText("Free " + freeCouponsNo + " coupon");
                    } else {
                        freeCoupon.setText("Free " + freeCouponsNo + " coupons");
                    }
                } else {
                    freeCouponIcons.setVisibility(View.INVISIBLE);
                    freeCoupon.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("Rs." + productPriceText + "/-");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                productCuttedPrice.setText("Rs." + productCuttedPriceText + "/-");
                couponRedemption.setVisibility(View.VISIBLE);

                /////////coupon redemption DialogD

                couponRecyclerView = couponRedemptionDialog.findViewById(R.id.coupons_recyclerView);
                ImageView toggleRecyclerView = couponRedemptionDialog.findViewById(R.id.toogle_recyclerView);
                selectedCoupon = couponRedemptionDialog.findViewById(R.id.selected_coupon);
                TextView originalPrice = couponRedemptionDialog.findViewById(R.id.original_price);
                TextView discountedPrice = couponRedemptionDialog.findViewById(R.id.coupon_price);

                LinearLayout apply_remove_container = couponRedemptionDialog.findViewById(R.id.remove_apply_container);
                Button applyBtn = couponRedemptionDialog.findViewById(R.id.apply_btn);
                Button removeBtn = couponRedemptionDialog.findViewById(R.id.remove_btn);
                TextView footerText = couponRedemptionDialog.findViewById(R.id.footerText);

                footerText.setVisibility(View.GONE);
                apply_remove_container.setVisibility(View.VISIBLE);

                couponTitle = couponRedemptionDialog.findViewById(R.id.reward_title);
                couponExpiryDate = couponRedemptionDialog.findViewById(R.id.reward_validDate);
                couponBody = couponRedemptionDialog.findViewById(R.id.reward_body);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                couponRecyclerView.setLayoutManager(layoutManager);

                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productPriceText;
                RewardAdapter adapter = new RewardAdapter(position,DbQueries.rewardModelList, true, couponRecyclerView, selectedCoupon, productOriginalPrice, couponTitle, couponExpiryDate, couponBody, discountedPrice,cartItemModelList);
                couponRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                            for (RewardModel rewardModel : DbQueries.rewardModelList) {

                                if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    tvCouponRedeem.setText(rewardModel.getCouponBody());
                                    redemBtn.setText("Coupon");
                                    couponRedemption.setBackground(itemView.getContext().getDrawable(R.drawable.reward_gradient_baground));
                                }
                            }
                            couponsApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length() -2));
                            String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length() -2)));
                            couponsApplied.setText("Coupon applied -Rs." + offerDiscountedAmt+"/-");
                            productPrice.setText(discountedPrice.getText());
                            notifyItemChanged(cartItemModelList.size() -1);
                            couponRedemptionDialog.dismiss();
                        }
                    }
                });

                removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DbQueries.rewardModelList) {

                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {

                                rewardModel.setAlreadyUsed(false);
                            }

                        }
                        couponTitle.setText("Coupon");
                        couponExpiryDate.setText("Validity");
                        couponBody.setText("Tap the icon on the top right corner to select your coupon.");
                        couponsApplied.setVisibility(View.INVISIBLE);
                        tvCouponRedeem.setText("Apply your coupon here.");
                        redemBtn.setText("REDEEM");
                        couponRedemption.setBackgroundColor(itemView.getContext().getColor(R.color.couponRed));
                        cartItemModelList.get(position).setSelectedCouponId(null);
                        notifyItemChanged(cartItemModelList.size() -1);
                        productPrice.setText("Rs." + productPriceText + "/-");
                        productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                        couponRedemptionDialog.dismiss();
                    }
                });
                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DbQueries.rewardModelList) {

                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {

                                rewardModel.setAlreadyUsed(false);
                            }

                        }
                        showDilogRecyclerView();
                    }
                });
                /////////coupon redemption DialogD

                productQuantity.setText("Qty: " + quantity);

                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                    for (RewardModel rewardModel : DbQueries.rewardModelList) {

                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            tvCouponRedeem.setText(rewardModel.getCouponBody());
                            redemBtn.setText("Coupon");
                            couponRedemption.setBackground(itemView.getContext().getDrawable(R.drawable.reward_gradient_baground));

                            couponBody.setText(rewardModel.getCouponBody());
                            if (rewardModel.getType().equals("Discount")){
                                couponTitle.setText(rewardModel.getType());
                            }else {
                                couponTitle.setText("FLAT Rs."+rewardModel.getDiscOrAmt()+" OFF");
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                            couponExpiryDate.setText("till " + simpleDateFormat.format(rewardModel.getTimestamp()).toString());
                        }
                    }
                    couponsApplied.setVisibility(View.VISIBLE);
                    discountedPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    couponsApplied.setText("Coupon applied -Rs." + offerDiscountedAmt+"/-");
                    productPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                }else {
                    couponsApplied.setVisibility(View.INVISIBLE);
                    tvCouponRedeem.setText("Apply your coupon here.");
                    redemBtn.setText("REDEEM");
                    couponRedemption.setBackgroundColor(itemView.getContext().getColor(R.color.couponRed));
                }

                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getColor(R.color.red));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.red)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getColor(R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.black)));
                    }
                }

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productQuantity.setVisibility(View.VISIBLE);
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.setCancelable(true);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancel = quantityDialog.findViewById(R.id.cancel_button);
                        Button ok = quantityDialog.findViewById(R.id.ok_button);
                        quantityNo.setHint("Max " + String.valueOf(maxQuantity));

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {
                                        if (itemView.getContext() instanceof MainActivity) {
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        } else {
                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));

                                            }
                                        }
                                        productQuantity.setVisibility(View.VISIBLE);
                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() -1);

                                        if (!showDeleteBtn) {
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            int initialQty = Integer.parseInt(quantity);
                                            int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQty > initialQty) {
                                                for (int y = 0; y < finalQty - initialQty; y++) {
                                                    String quantityDocument = UUID.randomUUID().toString().substring(0, 20);
                                                    Map<String, Object> timeStamp = new HashMap<>();
                                                    timeStamp.put("time", FieldValue.serverTimestamp());
                                                    int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").document(quantityDocument).set(timeStamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    qtyId.add(quantityDocument);

                                                                    if (finalY + 1 == finalQty - initialQty) {
                                                                        firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            List<String> serverQuantity = new ArrayList<>();

                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }

                                                                                            long availableQty = 0;
                                                                                            for (String qtyId : qtyId) {

                                                                                                if (!serverQuantity.contains(qtyId)) {

                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry ! all product may not be available in required quantity....", Toast.LENGTH_SHORT).show();
                                                                                                } else {
                                                                                                    availableQty++;
                                                                                                }
                                                                                                DeliveryActivity.adapter.notifyDataSetChanged();
                                                                                            }
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }

                                                                }
                                                            });
                                                }
                                            } else if (initialQty > finalQty) {
                                                for (int x = 0; x < initialQty - finalQty; x++) {
                                                    String qtyIds = qtyId.get(qtyId.size() - 1 - x);
                                                    int finalX = x;

                                                    int finalX1 = x;
                                                    FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").document(qtyIds).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    qtyId.remove(qtyIds);
                                                                    DeliveryActivity.adapter.notifyDataSetChanged();
                                                                    if (finalX1 +1 == initialQty - finalQty){
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }

                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max quantity : " + maxQuantity, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });

                if (offersAppliedText > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productCuttedPriceText) - Long.valueOf(productPriceText));
                    offersApplied.setText("Offers applied -Rs." + offerDiscountedAmt + "/-");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }
            } else {
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                productCuttedPrice.setText("");
                couponRedemption.setVisibility(View.GONE);
                productQuantity.setVisibility(View.INVISIBLE);
                couponsApplied.setVisibility(View.GONE);
                freeCoupon.setVisibility(View.INVISIBLE);
                freeCouponIcons.setVisibility(View.INVISIBLE);
                offersApplied.setVisibility(View.GONE);
            }


            if (showDeleteBtn) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }


            redemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel : DbQueries.rewardModelList) {

                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {

                            rewardModel.setAlreadyUsed(false);
                        }

                    }
                    couponRedemptionDialog.show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                        for (RewardModel rewardModel : DbQueries.rewardModelList) {

                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {

                                rewardModel.setAlreadyUsed(false);
                            }

                        }
                    }

                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;

                        DbQueries.removeFromCartlist(position, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }

        private void showDilogRecyclerView() {
            if (couponRecyclerView.getVisibility() == View.GONE) {
                couponRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupon.setVisibility(View.GONE);
            } else {
                selectedCoupon.setVisibility(View.VISIBLE);
                couponRecyclerView.setVisibility(View.GONE);
            }
        }
    }


    public class CartTotalViewHolder extends RecyclerView.ViewHolder {
        TextView totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount;

        public CartTotalViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_Items);
            totalItemsPrice = itemView.findViewById(R.id.total_item_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);

        }


        private void setTotalItems(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price (" + totalItemText + " items)");
            totalItemsPrice.setText("Rs." + totalItemPriceText + "/-");
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            } else {
                deliveryPrice.setText("Rs." + deliveryPriceText + "/-");
            }
            totalAmount.setText("Rs." + totalAmountText + "/-");
            cartTotalAmount.setText("Rs." + totalAmountText + "/-");
            savedAmount.setText("You saved Rs." + savedAmountText + "/- on this order.");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteBtn) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
