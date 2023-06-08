package com.example.totshopping.Fregment;

import static com.example.totshopping.DatabaseQueries.DbQueries.clearData;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.MyAddressActivity;
import com.example.totshopping.Activity.RegistrationActivity;
import com.example.totshopping.Activity.UpdateUserActivity;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.MyOrderModel;
import com.example.totshopping.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    public static final int MANAGE_ADDRESS = 1;
    Button viewAllAddressBtn,signOutBtn;
    FloatingActionButton settingButton;
    CircleImageView profileImage;
    ImageView currentImage;
    TextView fullname, email, currentOrderStatus, recentOrderTitle;
    LinearLayout layoutContainer, recentOrderContainer;
    Dialog loadingDialog;
    ImageView orderIndicator, packedIndicator, shippedIndicator, deliveryIndicator;
    ProgressBar o_p_progress, p_s_progress, s_d_progress;
    TextView addressFullname,addresses,addressPinCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        viewAllAddressBtn = view.findViewById(R.id.address_btn);

        profileImage = view.findViewById(R.id.profile_image);
        fullname = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        layoutContainer = view.findViewById(R.id.layout_container);

        currentImage = view.findViewById(R.id.current_order_image);
        currentOrderStatus = view.findViewById(R.id.current_order_status);

        orderIndicator = view.findViewById(R.id.order_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippedIndicator = view.findViewById(R.id.shiped_indicator);
        deliveryIndicator = view.findViewById(R.id.delivered_indicator);

        o_p_progress = view.findViewById(R.id.order_packed_progress);
        p_s_progress = view.findViewById(R.id.packed_shiped_progress);
        s_d_progress = view.findViewById(R.id.shipped_delivered_progress);

        recentOrderContainer = view.findViewById(R.id.recent_order_container);
        recentOrderTitle = view.findViewById(R.id.recent_order_title);

        addressFullname=view.findViewById(R.id.address_fullName);
        addresses=view.findViewById(R.id.addresses);
        addressPinCode=view.findViewById(R.id.address_pinCode);
        signOutBtn=view.findViewById(R.id.sign_out_btn);

        settingButton=view.findViewById(R.id.setting_btn);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userInfo = new Intent(getContext(),UpdateUserActivity.class);
                userInfo.putExtra("name",fullname.getText());
                userInfo.putExtra("email",email.getText());
                userInfo.putExtra("profile",DbQueries.profile);
                startActivity(userInfo);
            }
        });

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        DbQueries.clearData();

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (MyOrderModel orderModel : DbQueries.orderModelList) {
                    if (!orderModel.isCancellationRequested()) {
                        if (!orderModel.getOrderStatus().equals("Delivered") && !orderModel.getOrderStatus().equals("Cancelled")) {
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(orderModel.getProductImage()).placeholder(R.drawable.mobile_png).into(currentImage);
                            currentOrderStatus.setText(orderModel.getOrderStatus());

                            switch (orderModel.getOrderStatus()) {
                                case "Ordered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    break;
                                case "Packed":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_p_progress.setProgress(100);
                                    break;
                                case "Shipped":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_p_progress.setProgress(100);
                                    p_s_progress.setProgress(100);
                                    break;
                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    deliveryIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_p_progress.setProgress(100);
                                    p_s_progress.setProgress(100);
                                    s_d_progress.setProgress(100);
                                    break;
                            }

                        }
                    }

                }
                int i = 0;
                for (MyOrderModel myOrderModel : DbQueries.orderModelList) {
                    if (i < 4) {
                        if (myOrderModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getActivity()).load(myOrderModel.getProductImage()).placeholder(R.drawable.mobile_png).into((CircleImageView) recentOrderContainer.getChildAt(i));
                            i++;
                        }
                    }else {
                        break;
                    }
                }
                if (i ==0){
                    recentOrderTitle.setText("No recent orders.");
                }
                if (i < 3){
                    for (int x=i; x < 4; x++){
                        recentOrderContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DbQueries.addressesModelList.size() == 0) {
                            addressFullname.setText("No address");
                            addresses.setText("-");
                            addressPinCode.setText("-");
                            loadingDialog.dismiss();
                        }else {
                          setAddress();
                        }
                    }
                });


                loadingDialog.dismiss();
            }
        });

        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressesIntent = new Intent(getContext(), MyAddressActivity.class);
                myAddressesIntent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(myAddressesIntent);
           //
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                clearData();
                startActivity(new Intent(getContext(), RegistrationActivity.class));
                getActivity().finish();
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!DbQueries.profile.equals("")) {
            Glide.with(getActivity()).load(DbQueries.profile).placeholder(R.drawable.avatarra).into(profileImage);
        }else {
            profileImage.setImageResource(R.drawable.avatarra);
        }
        fullname.setText(DbQueries.fullname);
        email.setText(DbQueries.email);

        DbQueries.loadAddresses(getContext(),loadingDialog,false);
        DbQueries.loadOrder(getContext(), null, loadingDialog);

        if (!loadingDialog.isShowing()){
            if (DbQueries.addressesModelList.size() == 0) {
                addressFullname.setText("No address");
                addresses.setText("-");
                addressPinCode.setText("-");
            }else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String name,mobile,city,locality,landMark,alternativeMobile,pinCode,state,flatNo;

        name = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getName();
        mobile = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getMobileNo();
        city = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getCity();
        locality = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getLocality();
        landMark = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getLandMark();
        alternativeMobile = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getAlternativeMobileNo();
        pinCode = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getPinCode();
        state = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getState();
        flatNo = DbQueries.addressesModelList.get(DbQueries.selectedAddress).getFlatNo();

        if (alternativeMobile.equals("")) {
            addressFullname.setText(name + " - " + mobile);
        } else {
            addressFullname.setText(name + " - " + mobile + " or " + alternativeMobile);
        }
        if (landMark.equals("")) {
            addresses.setText(flatNo + ", " + locality + ", " + city + ", " + state);
        }else {
            addresses.setText(flatNo + ", " + locality + ", " + landMark + ", " + city + ", " + state);
        }
        addressPinCode.setText(pinCode);
    }
}