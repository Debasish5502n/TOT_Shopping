package com.example.totshopping.Fregment;

import static com.example.totshopping.DatabaseQueries.DbQueries.wishList;
import static com.example.totshopping.DatabaseQueries.DbQueries.wishlistModelList;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.totshopping.Adapter.WishlistAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.R;

public class WishlistFragment extends Fragment {

    RecyclerView wishlistRecyclerView;
    Dialog loadingDialog;
    public static WishlistAdapter wishlistAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_wishlist, container, false);

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        wishlistRecyclerView=view.findViewById(R.id.wishlist_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        wishlistRecyclerView.setLayoutManager(layoutManager);

        if (wishlistModelList.size() ==0){
            wishList.clear();
            DbQueries.loadWishList(getContext(),loadingDialog,true);
        }else {
            loadingDialog.dismiss();
        }

        wishlistAdapter=new WishlistAdapter(wishlistModelList,true);
        wishlistRecyclerView.setAdapter(wishlistAdapter);
        wishlistAdapter.notifyDataSetChanged();
        return view;
    }
}