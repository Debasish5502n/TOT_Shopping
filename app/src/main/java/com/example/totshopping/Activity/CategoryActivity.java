package com.example.totshopping.Activity;

import static com.example.totshopping.DatabaseQueries.DbQueries.list;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadCategoriesName;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.totshopping.Adapter.HomePageAdapter;
import com.example.totshopping.Model.HomePageModel;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.Model.SliderModel;
import com.example.totshopping.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView categoryRecyclerView;
    ImageView backArrow;
    TextView title1;
    HomePageAdapter homePageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        String title=getIntent().getStringExtra("name");
        title1=findViewById(R.id.title);
        title1.setText(" " +title);
        backArrow=findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ///////////// banner slider
        List<SliderModel> sliderModelList=new ArrayList<SliderModel>();

        ///////////// banner slider

        ///////////// Horizontal Scroll Layout
        List<HorizontalScrollModel> horizontalScrollModelList=new ArrayList<>();
        ///////////// Horizontal Scroll Layout

        ///////////////////////////////////
        categoryRecyclerView=findViewById(R.id.category_recyclerView);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(this);
        layoutManager1.setOrientation(RecyclerView.VERTICAL);
        categoryRecyclerView.setLayoutManager(layoutManager1);

        int listPosition =0;
        for (int x=0;x < loadCategoriesName.size();x++){
            if (loadCategoriesName.get(x).equals(title.toUpperCase(Locale.ROOT))){
                listPosition = x;
            }
        }
        if (listPosition == 0){
            loadCategoriesName.add(title.toUpperCase());
            list.add(new ArrayList<HomePageModel>());
            homePageAdapter=new HomePageAdapter(list.get(loadCategoriesName.size() -1));
            loadFragment(categoryRecyclerView,this,loadCategoriesName.size() -1,title);
        }else {
            homePageAdapter=new HomePageAdapter(list.get(listPosition));
        }

        categoryRecyclerView.setAdapter(homePageAdapter);
        homePageAdapter.notifyDataSetChanged();
        ///////////////////////////////////
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id== R.id.main_search_icon) {
            Intent intent=new Intent(CategoryActivity.this,SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}