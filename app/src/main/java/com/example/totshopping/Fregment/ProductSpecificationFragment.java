package com.example.totshopping.Fregment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.totshopping.Adapter.ProductSpecificationAdapter;
import com.example.totshopping.Model.ProductSpecifiactionModel;
import com.example.totshopping.R;

import java.util.List;

public class ProductSpecificationFragment extends Fragment {

    RecyclerView recyclerView;
    ProductSpecificationAdapter adapter;
    public List<ProductSpecifiactionModel> productSpecifiactionModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_product_specification, container, false);

        recyclerView=view.findViewById(R.id.recyclerView);
        adapter=new ProductSpecificationAdapter(productSpecifiactionModelList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(0,"General"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(0,"Electronics"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
//        productSpecifiactionModelList.add(new ProductSpecifiactionModel(1,"Ram","4gb"));
        recyclerView.setAdapter(adapter);

        return view;
    }
}