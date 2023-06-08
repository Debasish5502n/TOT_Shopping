package com.example.totshopping.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.totshopping.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OtpVerificationActivity extends AppCompatActivity {

    TextView phoneNumber,capture;
    EditText otp;
    Button verifyBtn;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        phoneNumber=findViewById(R.id.phone_number);
        otp=findViewById(R.id.otp);
        verifyBtn=findViewById(R.id.verify_btn);
        capture=findViewById(R.id.capture);
        number=getIntent().getStringExtra("mobileNo");
        phoneNumber.setText("Our team contact you with this no. "+number);
        capture.setText(UUID.randomUUID().toString().substring(0,6));

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(otp.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Blank Field can not be processed",Toast.LENGTH_LONG).show();
                else if(otp.getText().toString().length()!=6)
                    Toast.makeText(getApplicationContext(),"invalid capture code",Toast.LENGTH_LONG).show();
                else if (capture.getText().toString().equals(otp.getText().toString()))
                {
                    Map<String, Object> orderDetails = new HashMap<>();
                    orderDetails.put("Payment Status", "COD");
                    orderDetails.put("Order Status", "Ordered");
                    String order_id =getIntent().getStringExtra("orderId");
                    FirebaseFirestore.getInstance().collection("ORDERS").document(order_id).
                            update(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> orderId = new HashMap<>();
                                orderId.put("order_id", order_id);
                                orderId.put("time", FieldValue.serverTimestamp());
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDER").document(order_id).set(orderId)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    DeliveryActivity.codConformationLayout = true;
                                                    finish();
                                                } else {
                                                    Toast.makeText(OtpVerificationActivity.this, "Failed to update user order list", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(OtpVerificationActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(getApplicationContext(),"invalid capture code",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}