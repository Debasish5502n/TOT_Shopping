package com.example.totshopping.Fregment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.totshopping.R;
public class ProductDescriptionFragment extends Fragment {

    TextView tv_productDescription;
    public String body;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_product_description, container, false);
        tv_productDescription =view.findViewById(R.id.tv_product_description);
        tv_productDescription.setText(body);

        return view;
    }
}