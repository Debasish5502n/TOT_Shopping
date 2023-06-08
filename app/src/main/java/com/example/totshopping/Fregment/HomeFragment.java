package com.example.totshopping.Fregment;

import static com.example.totshopping.DatabaseQueries.DbQueries.categoryModelList;
import static com.example.totshopping.DatabaseQueries.DbQueries.list;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadCategories;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadCategoriesName;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.totshopping.Activity.MainActivity;
import com.example.totshopping.Adapter.ChategoryAdapter;
import com.example.totshopping.Adapter.HomePageAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Model.CategoryModel;
import com.example.totshopping.Model.HomePageModel;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.Model.SliderModel;
import com.example.totshopping.Model.WishlistModel;
import com.example.totshopping.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ChategoryAdapter adapter;
    RecyclerView homePageRecyclerView;
    List<CategoryModel> categoryFakeModelList = new ArrayList<>();
    List<HomePageModel> homePageFakeModelList = new ArrayList<>();
    HomePageAdapter homePageAdapter;
    public static SwipeRefreshLayout swipeRefreshLayout;
    ShimmerRecyclerView shimmerRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        shimmerRecyclerView = view.findViewById(R.id.shimmer_recyclerView1);
        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerView);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.red), getContext().getResources().getColor(R.color.red), getContext().getResources().getColor(R.color.red));
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayout);

        ////////////category fake model list
        categoryFakeModelList.add(new CategoryModel("null", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        categoryFakeModelList.add(new CategoryModel("", ""));
        ////////////category fake model list

        ////////////Slider fake model list
        List<SliderModel> sliderFakeModelList=new ArrayList<>();
        sliderFakeModelList.add(new SliderModel("null","#dfdfdf"));
        sliderFakeModelList.add(new SliderModel("null","#dfdfdf"));
        sliderFakeModelList.add(new SliderModel("null","#dfdfdf"));
        sliderFakeModelList.add(new SliderModel("null","#dfdfdf"));
        sliderFakeModelList.add(new SliderModel("null","#dfdfdf"));
        sliderFakeModelList.add(new SliderModel("null","#dfdfdf"));

        List<HorizontalScrollModel> horizontalFakeScrollModelList=new ArrayList<>();
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));
        horizontalFakeScrollModelList.add(new HorizontalScrollModel("","","","",""));

        homePageFakeModelList.add(new HomePageModel(0,sliderFakeModelList));
        homePageFakeModelList.add(new HomePageModel(1,"","#dfdfdf"));
        homePageFakeModelList.add(new HomePageModel(2, "", "#dfdfdf", horizontalFakeScrollModelList, new ArrayList<WishlistModel>() ));
        homePageFakeModelList.add(new HomePageModel(3,"","#dfdfdf",horizontalFakeScrollModelList));
        ////////////Slider fake model list
        adapter = new ChategoryAdapter(categoryFakeModelList);
        homePageAdapter = new HomePageAdapter(homePageFakeModelList);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (categoryModelList.size() == 0) {
            loadCategories(recyclerView, getContext());
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter = new ChategoryAdapter(categoryModelList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager1.setOrientation(RecyclerView.VERTICAL);
        homePageRecyclerView.setLayoutManager(layoutManager1);


        if (list.size() == 0) {
            loadCategoriesName.add("HOME");
            list.add(new ArrayList<HomePageModel>());
            loadFragment(homePageRecyclerView, getContext(), 0, "Home");
        } else {
            homePageAdapter = new HomePageAdapter(list.get(0));
            homePageAdapter.notifyDataSetChanged();

        }
        homePageRecyclerView.setAdapter(homePageAdapter);

        ///////////////////////////////////

        //////////////////swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onRefresh() {
                shimmerRecyclerView.setVisibility(View.GONE);
                homePageRecyclerView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(true);

//                list.clear();
//                categoryModelList.clear();
//                loadCategoriesName.clear();
                DbQueries.clearData();
                homePageRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                shimmerRecyclerView.setVisibility(View.VISIBLE);
                shimmerRecyclerView.showShimmerAdapter();


                if (connectivityManager.getActiveNetworkInfo() != null) {
                    connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
                    MainActivity.drawerLayout.setDrawerLockMode(0);
                    adapter=new ChategoryAdapter(categoryFakeModelList);
                    homePageAdapter=new HomePageAdapter(homePageFakeModelList);
                    recyclerView.setAdapter(adapter);
                    homePageRecyclerView.setAdapter(homePageAdapter);

                    loadCategories(recyclerView, getContext());

                    loadCategoriesName.add("HOME");
                    list.add(new ArrayList<HomePageModel>());
                    loadFragment(homePageRecyclerView, getContext(), 0, "Home");

                    shimmerRecyclerView.setVisibility(View.GONE);
                    homePageRecyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    MainActivity.drawerLayout.setDrawerLockMode(1);
                    homePageRecyclerView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    shimmerRecyclerView.setVisibility(View.VISIBLE);
                    shimmerRecyclerView.showShimmerAdapter();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    // Setting Alert Dialog Title
                    alertDialogBuilder.setTitle("NO INTERNET CONNECTION");
                    // Icon Of Alert Dialog
                    alertDialogBuilder.setIcon(R.drawable.wifi);
                    // Setting Alert Dialog Message
                    alertDialogBuilder.setMessage("Please check your network connection");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        });
        //////////////////swipe refresh layout
        return view;

    }
}