package com.example.totshopping.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.example.totshopping.Adapter.GridProductAdapter;
import com.example.totshopping.Adapter.WishlistAdapter;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.Model.WishlistModel;
import com.example.totshopping.R;

import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    GridView gridView;
    public static List<HorizontalScrollModel> horizontalScrollModelList;
    public static List<WishlistModel> wishlistModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        gridView=findViewById(R.id.grid_view);
        int Layout_code=getIntent().getIntExtra("Layout_code",-1);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        if (Layout_code ==0) {
            recyclerView.setVisibility(View.VISIBLE);

            WishlistAdapter wishlistAdapter = new WishlistAdapter(wishlistModelList, false);
            recyclerView.setAdapter(wishlistAdapter);
            wishlistAdapter.notifyDataSetChanged();
        }else if (Layout_code == 1) {
            gridView.setVisibility(View.VISIBLE);

            GridProductAdapter gridProductAdapter = new GridProductAdapter(horizontalScrollModelList);
            gridView.setAdapter(gridProductAdapter);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}