package com.example.totshopping.Activity;

import static com.example.totshopping.Activity.DeliveryActivity.SELECT_ADDRESS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.totshopping.Adapter.MyAddressesAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyAddressActivity extends AppCompatActivity {

    private int previousAddress;
    Toolbar toolbar;
    RecyclerView addressesRecyclerView;
    Button deliverHereBtn;
    private static MyAddressesAdapter myAddressesAdapter;
    TextView addNewAddress,savedAddress;
    Dialog loadingDialog;
    private int Mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        toolbar=findViewById(R.id.toolbar);
        addressesRecyclerView=findViewById(R.id.addresses_recyclerView);
        deliverHereBtn=findViewById(R.id.delivered_here_btn);
        addNewAddress=findViewById(R.id.add_new_address_btn);
        savedAddress=findViewById(R.id.address_saved);
        previousAddress =DbQueries.selectedAddress;

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                savedAddress.setText(String.valueOf(DbQueries.addressesModelList.size()+" Saved address"));

            }
        });
        ////////////////loading dialog

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        addressesRecyclerView.setLayoutManager(layoutManager);

        Mode=getIntent().getIntExtra("MODE",-1);
        if (Mode == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE);
            addNewAddress.setVisibility(View.GONE);
        }
        myAddressesAdapter=new MyAddressesAdapter(DbQueries.addressesModelList,Mode,loadingDialog);
       // ((SimpleItemAnimator)addressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesRecyclerView.setAdapter(myAddressesAdapter);
        myAddressesAdapter.notifyDataSetChanged();

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAddressActivity.this, AddAddressActivity.class);
                if (Mode != SELECT_ADDRESS) {
                    intent.putExtra("INTENT", "manage");

                } else{
                    intent.putExtra("INTENT", "null");
            }
                startActivity(intent);
            }
        });

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                if (DbQueries.selectedAddress != previousAddress){
                    final int previousAddressIndex =previousAddress;

                    Map<String,Object> updateSelection=new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(DbQueries.selectedAddress+1),true);

                    previousAddress =DbQueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA")
                            .document("MY_ADDRESS")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                finish();
                            }else {
                                previousAddress =previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        savedAddress.setText(String.valueOf(DbQueries.addressesModelList.size()+" Saved address"));
    }

    public static void refreshItem(int deSelect, int select){
        myAddressesAdapter.notifyItemChanged(deSelect);
        myAddressesAdapter.notifyItemChanged(select);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if (id == android.R.id.home) {
            if (Mode == SELECT_ADDRESS){
            if (DbQueries.selectedAddress != previousAddress) {
                DbQueries.addressesModelList.get(DbQueries.selectedAddress).setSelected(false);
                DbQueries.addressesModelList.get(previousAddress).setSelected(true);
                DbQueries.selectedAddress = previousAddress;
            }
        }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Mode ==SELECT_ADDRESS) {
            if (DbQueries.selectedAddress != previousAddress) {
                DbQueries.addressesModelList.get(DbQueries.selectedAddress).setSelected(false);
                DbQueries.addressesModelList.get(previousAddress).setSelected(true);
                DbQueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}