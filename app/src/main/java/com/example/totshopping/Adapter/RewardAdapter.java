package com.example.totshopping.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.totshopping.Model.CartItemModel;
import com.example.totshopping.Model.RewardModel;
import com.example.totshopping.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.myViewHolder> {

    List<RewardModel> rewardModelList;
    private Boolean useMiniLayout = false;
    RecyclerView couponRecyclerView;
    LinearLayout selectedCoupon;
    String productOriginalPrice;
    TextView selectedCouponTitle;
    TextView selectedCouponExpiryDate;
    TextView selectedCouponBody;
    TextView discountedPrice;
    int cartItemPosition =-1;
    List<CartItemModel> cartItemModelList;

    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }


    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView couponTitle, TextView couponExpiryDate, TextView couponBody,TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponRecyclerView =couponRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = couponTitle;
        this.selectedCouponExpiryDate = couponExpiryDate;
        this.selectedCouponBody = couponBody;
        this.discountedPrice = discountedPrice;
    }

    public RewardAdapter(int cartItemPosition,List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView couponTitle, TextView couponExpiryDate, TextView couponBody,TextView discountedPrice,List<CartItemModel> cartItemModelList) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponRecyclerView = couponRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = couponTitle;
        this.selectedCouponExpiryDate = couponExpiryDate;
        this.selectedCouponBody = couponBody;
        this.discountedPrice = discountedPrice;
        this.cartItemPosition = cartItemPosition;
        this.cartItemModelList = cartItemModelList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (useMiniLayout){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_my_reward_item_layout, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reward_item_layout, parent, false);
        }
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String couponId=rewardModelList.get(position).getCouponId();
        String type=rewardModelList.get(position).getType();
        String lower_limit=rewardModelList.get(position).getLowerLimit();
        String upper_limit=rewardModelList.get(position).getUpperLimit();
        String discOrAmt=rewardModelList.get(position).getDiscOrAmt();
        String body=rewardModelList.get(position).getCouponBody();
        Date validity=rewardModelList.get(position).getTimestamp();
        Boolean alreadyUsed=rewardModelList.get(position).isAlreadyUsed();

        holder.setData(couponId,type,validity,body,lower_limit,upper_limit,discOrAmt,alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView rewardTitle,rewardValidDate,rewardBody;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            rewardTitle=itemView.findViewById(R.id.reward_title);
            rewardValidDate=itemView.findViewById(R.id.reward_validDate);
            rewardBody=itemView.findViewById(R.id.reward_body);
        }
        private void setData(String couponId,String type, Date validity, String body, String lower_limit, String upper_limit, String discOrAmt,boolean alreadyUsed){

            if (type.equals("Discount")){
                rewardTitle.setText(type);
            }else {
                rewardTitle.setText("FLAT Rs."+discOrAmt+" OFF");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");

            if (alreadyUsed){
                rewardValidDate.setText("Already used");
                rewardValidDate.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                rewardBody.setTextColor(Color.parseColor("#50ffffff"));
                rewardTitle.setTextColor(Color.parseColor("#50ffffff"));
            }else {
                rewardBody.setTextColor(Color.parseColor("#ffffff"));
                rewardTitle.setTextColor(Color.parseColor("#ffffff"));
                rewardValidDate.setTextColor(itemView.getContext().getResources().getColor(R.color.purple_500));
                rewardValidDate.setText("till " + simpleDateFormat.format(validity).toString());
            }
            rewardBody.setText(body);

            if (useMiniLayout){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!alreadyUsed) {

                            selectedCouponTitle.setText(type);
                            selectedCouponExpiryDate.setText("till " + simpleDateFormat.format(validity));
                            selectedCouponBody.setText(body);

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lower_limit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upper_limit)) {
                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discOrAmt) / 100;
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                } else {
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discOrAmt)) + "/-");
                                }
                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(couponId);
                                }
                            } else {
                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(null);
                                }
                                discountedPrice.setText("Invalid");
                                Toast.makeText(itemView.getContext(), "Sorry ! product does match the coupon terms.", Toast.LENGTH_SHORT).show();
                            }

                            if (couponRecyclerView.getVisibility() == View.GONE) {
                                couponRecyclerView.setVisibility(View.VISIBLE);
                                selectedCoupon.setVisibility(View.GONE);
                            } else {
                                selectedCoupon.setVisibility(View.VISIBLE);
                                couponRecyclerView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }

    }
}
