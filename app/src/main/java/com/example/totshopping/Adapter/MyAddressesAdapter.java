package com.example.totshopping.Adapter;

import static com.example.totshopping.Activity.DeliveryActivity.SELECT_ADDRESS;
import static com.example.totshopping.Activity.MyAddressActivity.refreshItem;
import static com.example.totshopping.Fregment.MyAccountFragment.MANAGE_ADDRESS;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.totshopping.Activity.AddAddressActivity;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.MyAddressesModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAddressesAdapter extends RecyclerView.Adapter<MyAddressesAdapter.myViewHolder> {

    List<MyAddressesModel> myAddressesModelList;
    int MODE;
    int preSelectedPosition;
    boolean refresh = false;
    Dialog loadingDialog;

    public MyAddressesAdapter(List<MyAddressesModel> myAddressesModelList, int MODE, Dialog loadingDialog) {
        this.myAddressesModelList = myAddressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DbQueries.selectedAddress;
        this.loadingDialog = loadingDialog;

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String name = myAddressesModelList.get(position).getName();
        String mobile = myAddressesModelList.get(position).getMobileNo();
        String alternativeMobile = myAddressesModelList.get(position).getAlternativeMobileNo();
        String pinCode = myAddressesModelList.get(position).getPinCode();
        String city = myAddressesModelList.get(position).getCity();
        String landMark = myAddressesModelList.get(position).getLandMark();
        String locality = myAddressesModelList.get(position).getLocality();
        String state = myAddressesModelList.get(position).getState();
        String flatNo = myAddressesModelList.get(position).getFlatNo();
        boolean selected = myAddressesModelList.get(position).getSelected();

        holder.setData(name, mobile, alternativeMobile, pinCode, city, landMark, locality, state, flatNo, selected, position);
    }

    @Override
    public int getItemCount() {
        return myAddressesModelList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, addresses, pinCode;
        ImageView icon;
        LinearLayout optionContainer;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.name);
            addresses = itemView.findViewById(R.id.addresses);
            pinCode = itemView.findViewById(R.id.pinCode);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);

        }

        private void setData(String name, String mobile, String alternativeMobile, String pinCode1, String city, String landMark, String locality, String state, String flatNo, boolean selected, int position) {
            if (alternativeMobile.equals("")) {
                fullName.setText(name + " - " + mobile);
            } else {
                fullName.setText(name + " - " + mobile + " or " + alternativeMobile);
            }
            if (landMark.equals("")) {
                addresses.setText(flatNo + ", " + locality + ", " + city + ", " + state);
            } else {
                addresses.setText(flatNo + ", " + locality + ", " + landMark + ", " + city + ", " + state);
            }
            pinCode.setText(pinCode1);
            if (MODE == SELECT_ADDRESS) {
                icon.setImageResource(R.drawable.right_png);
                if (selected) {
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            myAddressesModelList.get(position).setSelected(true);
                            myAddressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DbQueries.selectedAddress = position;
                        }
                    }
                });

            } else if (MODE == MANAGE_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), AddAddressActivity.class);
                        intent.putExtra("INTENT", "update_address");
                        intent.putExtra("index", position);
                        itemView.getContext().startActivity(intent);
                        refresh = false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        Map<String, Object> addresses = new HashMap<>();
                        int x = 0;
                        int selected = -1;
                        for (int i = 0; i < myAddressesModelList.size(); i++) {
                            if (i != position) {

                                x++;
                                addresses.put("city_" + x, myAddressesModelList.get(i).getCity());
                                addresses.put("locality_" + x, myAddressesModelList.get(i).getLocality());
                                addresses.put("flat_no_" + x, myAddressesModelList.get(i).getFlatNo());
                                addresses.put("pinCode_" + x, myAddressesModelList.get(i).getPinCode());
                                addresses.put("landmark_" + x, myAddressesModelList.get(i).getLandMark());
                                addresses.put("name_" + x, myAddressesModelList.get(i).getName());
                                addresses.put("mobile_no_" + x, myAddressesModelList.get(i).getMobileNo());
                                addresses.put("alternative_mobile_no_" + x, myAddressesModelList.get(i).getAlternativeMobileNo());
                                addresses.put("state_" + x, myAddressesModelList.get(i).getState());

                                if (myAddressesModelList.get(position).getSelected()) {
                                    if (position - 1 >= 0) {
                                        if (x == position) {
                                            addresses.put("selected_" + x, true);
                                            selected = x;
                                        }else {
                                            addresses.put("selected_" + x, myAddressesModelList.get(i).getSelected());

                                        }
                                    } else {
                                        if (x == 1) {
                                            addresses.put("selected_" + x, true);
                                            selected = x;
                                        }else {
                                            addresses.put("selected_" + x, myAddressesModelList.get(i).getSelected());

                                        }
                                    }
                                } else {
                                    addresses.put("selected_" + x, myAddressesModelList.get(i).getSelected());
                                    if (myAddressesModelList.get(i).getSelected()){
                                        selected =x;
                                    }
                                }
                                addresses.put("list_size", x);

                                int finalSelected = selected;
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESS")
                                        .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DbQueries.addressesModelList.remove(position);
                                            if (finalSelected != -1) {
                                                DbQueries.selectedAddress = finalSelected - 1;
                                                DbQueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                            }else if (DbQueries.addressesModelList.size() ==0){
                                                DbQueries.selectedAddress =-1;
                                            }

                                            notifyDataSetChanged();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        }

                        refresh = false;

                    }
                });
                icon.setImageResource(R.drawable.more_vertical);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(preSelectedPosition, preSelectedPosition);
                        } else {
                            refresh = true;
                        }
                        preSelectedPosition = position;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preSelectedPosition, preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }
}
