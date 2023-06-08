package com.example.totshopping.Fregment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.totshopping.Activity.MainActivity;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupFragment extends Fragment {

    TextView already_have_an_account;
    FrameLayout frameLayout;

    EditText email, name, password, conform_password;
    ImageView close;
    Button signup;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_report, container, false);
        already_have_an_account = view.findViewById(R.id.already_have_an_account);
        frameLayout = getActivity().findViewById(R.id.frameLayout);

        email = view.findViewById(R.id.sign_up_email);
        name = view.findViewById(R.id.sign_up_name);
        password = view.findViewById(R.id.sign_up_password);
        conform_password = view.findViewById(R.id.sign_up_conform_password);
        close = view.findViewById(R.id.sign_up_skip);
        signup = view.findViewById(R.id.sign_up_ptn);
        progressBar = view.findViewById(R.id.sign_up_progressbar);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (disableCloseBtn) {
            close.setVisibility(View.GONE);
        } else {
            close.setVisibility(View.VISIBLE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        conform_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemailandPassword();
            }
        });

        already_have_an_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigniinFregment());
            }
        });
        return view;
    }

    private void checkemailandPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.custom_error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());
        if (email.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(conform_password.getText().toString())) {

                progressBar.setVisibility(View.VISIBLE);
                signup.setEnabled(false);
                signup.setTextColor(Color.argb(50, 255, 255, 255));
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Map<Object, String> userData = new HashMap<>();
                                    userData.put("fullname", name.getText().toString());
                                    userData.put("email", email.getText().toString());
                                    userData.put("uid", auth.getUid());
                                    userData.put("profile","");

                                    firebaseFirestore.collection("USERS").document(auth.getUid()).set(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(auth.getUid()).collection("USER_DATA");

                                                        ////////maps
                                                        Map<Object, Long> wishlistMap = new HashMap<>();
                                                        wishlistMap.put("list_size", (long) 0);

                                                        Map<Object, Long> ratingsMap = new HashMap<>();
                                                        ratingsMap.put("list_size", (long) 0);

                                                        Map<Object, Long> cartsMap = new HashMap<>();
                                                        cartsMap.put("list_size", (long) 0);

                                                        Map<Object, Long> myAddressMap = new HashMap<>();
                                                        myAddressMap.put("list_size", (long) 0);

                                                        Map<Object, Long> myNotificationMap = new HashMap<>();
                                                        myNotificationMap.put("list_size", (long) 0);
                                                        ////////maps

                                                        List<String> documentsName =new ArrayList<>();
                                                        documentsName.add(new String("MY_WISHLIST"));
                                                        documentsName.add(new String("MY_RATINGS"));
                                                        documentsName.add(new String("MY_CART"));
                                                        documentsName.add(new String("MY_ADDRESS"));
                                                        documentsName.add(new String("MY_NOTIFICATION"));

                                                        List<Map<Object, Long>> documentFields =new ArrayList<>();
                                                        documentFields.add(wishlistMap);
                                                        documentFields.add(ratingsMap);
                                                        documentFields.add(cartsMap);
                                                        documentFields.add(myAddressMap);
                                                        documentFields.add(myNotificationMap);

                                                        for (int x=0;x <documentsName.size();x++){

                                                            int finalX = x;
                                                            userDataReference.document(documentsName.get(x)).
                                                                    set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        if (finalX ==documentsName.size() -1) {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                                                            disableCloseBtn = false;
                                                                            getActivity().finish();
                                                                        }
                                                                    } else {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        signup.setEnabled(true);
                                                                        signup.setTextColor(Color.rgb(255, 255, 255));
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                        }

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {

                                }
                            }
                        });
            } else {
                conform_password.setError("Doesn't match email!");
            }
        } else {
            email.setError("Invalid Email!");
        }

    }

    private void setFragment(SigniinFregment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkedInput() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(name.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                    if (!TextUtils.isEmpty(conform_password.getText()) && conform_password.length() >= 8) {
                        signup.setEnabled(true);
                        signup.setTextColor(Color.rgb(255, 255, 255));
                    } else {
                        signup.setEnabled(false);
                        signup.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                } else {
                    signup.setEnabled(false);
                    signup.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                signup.setEnabled(false);
                signup.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signup.setEnabled(false);
            signup.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

}