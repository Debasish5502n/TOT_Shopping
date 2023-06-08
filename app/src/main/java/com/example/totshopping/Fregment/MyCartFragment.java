package com.example.totshopping.Fregment;

import static com.example.totshopping.DatabaseQueries.DbQueries.cartItemModelList;
import static com.example.totshopping.DatabaseQueries.DbQueries.cartList;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadAddresses;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.totshopping.Activity.DeliveryActivity;
import com.example.totshopping.Adapter.CartAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.CartItemModel;
import com.example.totshopping.Model.RewardModel;
import com.example.totshopping.R;

import java.util.ArrayList;

public class MyCartFragment extends Fragment {

    RecyclerView cartRecyclerView;
    AppCompatButton continue_btn;
    public static Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    TextView totalAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        totalAmount = view.findViewById(R.id.total_Items);
        continue_btn = view.findViewById(R.id.continue_btn);
        LinearLayoutManager layoutInflater = new LinearLayoutManager(getContext());
        layoutInflater.setOrientation(RecyclerView.VERTICAL);
        cartRecyclerView.setLayoutManager(layoutInflater);


        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, true);
        cartRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();


        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.fromCart = true;
                DeliveryActivity.cartItemModelList = new ArrayList<>();

                for (int x = 0; x < DbQueries.cartItemModelList.size(); x++) {
                    CartItemModel cartItemModel = DbQueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()) {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);

                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_ITEM));
                loadingDialog.show();
                if (DbQueries.addressesModelList.size() == 0) {
                    loadAddresses(getContext(), loadingDialog,true);
                } else {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DbQueries.rewardModelList.size() == 0) {
            loadingDialog.show();
            DbQueries.loadReward(getContext(), loadingDialog, false);
        }
        if (cartItemModelList.size() == 0) {
            cartList.clear();
            DbQueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        } else {
            if (cartItemModelList.get(cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_ITEM) {
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCouponId())) {
                for (RewardModel rewardModel : DbQueries.rewardModelList) {

                    if (rewardModel.getCouponId().equals(cartItemModel.getSelectedCouponId())) {

                        rewardModel.setAlreadyUsed(false);
                    }

                }
                cartItemModel.setSelectedCouponId(null);
                if (MyRewardFragment.rewardAdapter != null) {
                MyRewardFragment.rewardAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}