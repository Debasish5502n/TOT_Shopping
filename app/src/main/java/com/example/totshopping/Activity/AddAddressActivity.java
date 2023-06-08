package com.example.totshopping.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.MyAddressesModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    EditText city;
    EditText locality;
    EditText flatNo;
    EditText pinCode;
    EditText landMark;
    EditText name;
    EditText mobileNo;
    EditText alternativeMobileNo;
    Button save_btn;
    Toolbar toolbar;
    AppCompatSpinner spinner;

    String[] stateList;
    String selectedState;
    Dialog loadingDialog;

    private boolean updateAddress = false;
    private MyAddressesModel addressesModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        save_btn = findViewById(R.id.save_btn);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flat_no);
        pinCode = findViewById(R.id.pinCode);
        landMark = findViewById(R.id.landmark);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.phone_no);
        alternativeMobileNo = findViewById(R.id.alternating_phone_no);
        spinner = findViewById(R.id.state_spinner);
        stateList = getResources().getStringArray(R.array.india_states);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.india_states));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////////////////loading dialog

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getIntent().getStringExtra("INTENT").equals("update_address")){
            updateAddress=true;
            position = getIntent().getIntExtra("index",-1);
            addressesModel=DbQueries.addressesModelList.get(position);

            name.setText(addressesModel.getName());
            city.setText(addressesModel.getCity());
            landMark.setText(addressesModel.getLandMark());
            locality.setText(addressesModel.getLocality());
            flatNo.setText(addressesModel.getFlatNo());
            pinCode.setText(addressesModel.getPinCode());
            mobileNo.setText(addressesModel.getMobileNo());
            alternativeMobileNo.setText(addressesModel.getAlternativeMobileNo());
            for (int i=0;i<stateList.length;i++){
                if (stateList[i].equals(addressesModel.getState())){
                    spinner.setSelection(i);
                }
            }
            save_btn.setText("Update");
        }else {
            position= DbQueries.addressesModelList.size();
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(city.getText())) {
                    if (!TextUtils.isEmpty(locality.getText())) {
                        if (!TextUtils.isEmpty(flatNo.getText())) {
                            if (!TextUtils.isEmpty(pinCode.getText()) && pinCode.length() == 6) {
                                if (!TextUtils.isEmpty(name.getText())) {
                                    if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.length() == 10) {
                                        loadingDialog.show();
                                        Map<String, Object> addresses = new HashMap<>();
                                        addresses.put("city_" + String.valueOf(position + 1), city.getText().toString());
                                        addresses.put("locality_" + String.valueOf(position + 1), locality.getText().toString());
                                        addresses.put("flat_no_" + String.valueOf(position + 1), flatNo.getText().toString());
                                        addresses.put("landmark_" + String.valueOf(position + 1), landMark.getText().toString());
                                        addresses.put("pinCode_" + String.valueOf(position + 1), pinCode.getText().toString());
                                        addresses.put("name_" + String.valueOf(position + 1), name.getText().toString());
                                        addresses.put("mobile_no_" + String.valueOf(position + 1), mobileNo.getText().toString());
                                        addresses.put("alternative_mobile_no_" + String.valueOf(position + 1), alternativeMobileNo.getText().toString());
                                        addresses.put("state_" + String.valueOf(position + 1), selectedState);
                                        if (!updateAddress) {
                                            addresses.put("list_size", position + 1);
                                            if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                if (DbQueries.addressesModelList.size() ==0){
                                                    addresses.put("selected_" + String.valueOf(position + 1), true);
                                                }else {
                                                    addresses.put("selected_" + String.valueOf(position + 1), false);
                                                }
                                            }else {
                                                addresses.put("selected_" + String.valueOf(position + 1), true);
                                            }
                                            if (DbQueries.addressesModelList.size() > 0) {
                                                addresses.put("selected_" + (DbQueries.selectedAddress + 1), false);
                                            }
                                        }

                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("MY_ADDRESS")
                                                .update(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (!updateAddress) {
                                                        if (DbQueries.addressesModelList.size() > 0) {
                                                            DbQueries.addressesModelList.get(DbQueries.selectedAddress).setSelected(false);
                                                        }
                                                        DbQueries.addressesModelList.add(new MyAddressesModel(true, city.getText().toString(),
                                                                locality.getText().toString(),
                                                                flatNo.getText().toString(),
                                                                pinCode.getText().toString(),
                                                                landMark.getText().toString(),
                                                                name.getText().toString(),
                                                                mobileNo.getText().toString(),
                                                                alternativeMobileNo.getText().toString(),
                                                                selectedState));

                                                        if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                            if (DbQueries.addressesModelList.size() ==0){
                                                                DbQueries.selectedAddress = DbQueries.addressesModelList.size() - 1;
                                                            }
                                                        }else {
                                                            DbQueries.selectedAddress = DbQueries.addressesModelList.size() - 1;
                                                        }

                                                    }else {
                                                        DbQueries.addressesModelList.set(position,new MyAddressesModel(true, city.getText().toString(),
                                                                locality.getText().toString(),
                                                                flatNo.getText().toString(),
                                                                pinCode.getText().toString(),
                                                                landMark.getText().toString(),
                                                                name.getText().toString(),
                                                                mobileNo.getText().toString(),
                                                                alternativeMobileNo.getText().toString(),
                                                                selectedState));
                                                    }

                                                    if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    } else {
                                                        MyAddressActivity.refreshItem(DbQueries.selectedAddress, DbQueries.addressesModelList.size() - 1);
                                                    }

                                                    finish();
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        mobileNo.requestFocus();
                                        mobileNo.setError("Please enter valid number");
                                    }
                                } else {
                                    name.requestFocus();
                                }
                            } else {
                                pinCode.requestFocus();
                                pinCode.setError("Please enter valid pin");
                            }
                        } else {
                            flatNo.requestFocus();
                        }
                    } else {
                        locality.requestFocus();
                    }
                } else {
                    city.requestFocus();
                }

            }
        });
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
}