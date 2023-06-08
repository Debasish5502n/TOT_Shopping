package com.example.totshopping.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.ProductDetailsActivity;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.WishlistModel;
import com.example.totshopping.R;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.myViewHolder> {

    boolean fromSearch;
    public static List<WishlistModel> wishlistModelList =new ArrayList<>();
    public Boolean wishList;
    int lastPosition = -1;

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishList) {
        this.wishlistModelList = wishlistModelList;
        this.wishList = wishList;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public static List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public static void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        WishlistAdapter.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String productId =wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        long totalRatingsNo = wishlistModelList.get(position).getTotalRatting();
        long freeCoupon = wishlistModelList.get(position).getFreeCoupons();
        String productTitle = wishlistModelList.get(position).getProductTitle();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String cuttedPrice = wishlistModelList.get(position).getCuttedPrice();
        Boolean paymentMethod = wishlistModelList.get(position).getPaymentMethod();
        String ratings = wishlistModelList.get(position).getRatting();
        Boolean inStock = wishlistModelList.get(position).isInStock();

        holder.setData(productId,resource, productTitle, freeCoupon, ratings, totalRatingsNo, productPrice, cuttedPrice, paymentMethod,inStock);
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQueries.removeFromWishlist(position,holder.itemView.getContext());

            }
        });

    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        ImageView couponIcon;
        TextView freeCoupons;
        TextView ratting;
        TextView totalRatting;
        TextView productPrice;
        View cutPrice;
        TextView cuttedPrice;
        TextView paymentMethod;
        ImageView delete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            couponIcon = itemView.findViewById(R.id.free_coupon_icon);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupon);
            ratting = itemView.findViewById(R.id.avg_rating_mini_view);
            totalRatting = itemView.findViewById(R.id.total_ratting_mini_view);
            productPrice = itemView.findViewById(R.id.product_price);
            cutPrice = itemView.findViewById(R.id.price_cut);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            delete = itemView.findViewById(R.id.delete);

        }

        @SuppressLint("ResourceAsColor")
        private void setData(String productId, String resource, String title, long freeCouponsNo, String avgRate, long totalRatingsNo, String price, String cutPriceValue, Boolean cod, boolean inStock) {
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);
            if (freeCouponsNo > 0 && inStock) {
                couponIcon.setVisibility(View.VISIBLE);
                freeCoupons.setVisibility(View.VISIBLE);
                if (freeCouponsNo == 1) {
                    freeCoupons.setText("Free " + freeCouponsNo + " coupon");
                } else {
                    freeCoupons.setText("Free " + freeCouponsNo + " coupons");

                }
            } else {
                couponIcon.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.GONE);
            }
            if (inStock){
                ratting.setVisibility(View.VISIBLE);
                totalRatting.setVisibility(View.VISIBLE);
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                cuttedPrice.setVisibility(View.VISIBLE);

                ratting.setText(avgRate);
                totalRatting.setText(totalRatingsNo + "(Ratings)");
                productPrice.setText(price);
                cuttedPrice.setText(cutPriceValue);
                if (cod) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.GONE);
                }
            }else {
                LinearLayout linearLayout=(LinearLayout) ratting.getParent();
                linearLayout.setVisibility(View.INVISIBLE);
                ratting.setVisibility(View.INVISIBLE);
                totalRatting.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                cuttedPrice.setVisibility(View.GONE);
                paymentMethod.setVisibility(View.GONE);
            }


            if (wishList) {
                delete.setVisibility(View.GONE);
            } else {
                delete.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromSearch){
                        ProductDetailsActivity.fromSearch =true;
                    }
                    Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID",productId);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
