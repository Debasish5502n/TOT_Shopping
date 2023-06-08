package com.example.totshopping.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.totshopping.Fregment.ProductDescriptionFragment;
import com.example.totshopping.Fregment.ProductSpecificationFragment;
import com.example.totshopping.Model.ProductSpecifiactionModel;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {

    private int totalTabs;
    String productDescription;
    String productOtherDetails;
    List<ProductSpecifiactionModel> productSpecifiactionModelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productDescription, String productOtherDetails, List<ProductSpecifiactionModel> productSpecifiactionModelList) {
        super(fm);
        this.totalTabs = totalTabs;
        this.productDescription = productDescription;
        this.productOtherDetails = productOtherDetails;
        this.productSpecifiactionModelList = productSpecifiactionModelList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ProductDescriptionFragment productDescriptionFragment1 =new ProductDescriptionFragment();
                productDescriptionFragment1.body=productDescription;
                return productDescriptionFragment1;
            case 1:
                ProductSpecificationFragment productSpecificationFragment=new ProductSpecificationFragment();
                productSpecificationFragment.productSpecifiactionModelList =productSpecifiactionModelList;
                return productSpecificationFragment;
            case 2:
                ProductDescriptionFragment productDescriptionFragment2=new ProductDescriptionFragment();
                productDescriptionFragment2.body=productOtherDetails;
                return productDescriptionFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

}
