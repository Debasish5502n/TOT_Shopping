package com.example.totshopping.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.totshopping.Activity.ProductDetailsActivity;
import com.example.totshopping.Activity.ViewAllActivity;
import com.example.totshopping.Model.HomePageModel;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.Model.SliderModel;
import com.example.totshopping.Model.WishlistModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    int lastPosition = -1;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.STRIP_AD_SLIDER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_banner_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomePageModel.STRIP_AD_SLIDER:
                View stripAddView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_add_layout, parent, false);
                return new StripAddBannerViewHolder(stripAddView);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalScrollViewHolder(horizontalView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_layout, parent, false);
                return new GridProductViewHolder(gridView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.STRIP_AD_SLIDER:
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                ((StripAddBannerViewHolder) holder).setStripAdd(resource, color);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String title = homePageModelList.get(position).getTitle();
                String layoutColor = homePageModelList.get(position).getBackgroundColor();
                List<HorizontalScrollModel> horizontalScrollModelLis = homePageModelList.get(position).getHorizontalScrollModelList();
                List<WishlistModel> viewProductList = homePageModelList.get(position).getViewProductList();
                ((HorizontalScrollViewHolder) holder).setHorizontalScrollLayout(horizontalScrollModelLis, title, layoutColor, viewProductList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridTitle = homePageModelList.get(position).getTitle();
                String bagroundColor = homePageModelList.get(position).getBackgroundColor();
                List<HorizontalScrollModel> gridlModelLis = homePageModelList.get(position).getHorizontalScrollModelList();
                ((GridProductViewHolder) holder).setGridProductView(gridlModelLis, gridTitle, bagroundColor);
                break;
            default:
                return;
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        ViewPager bannerSlider;
        private int currentPage = 2;
        private Timer timer;
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        List<SliderModel> arrangeModelList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSlider = itemView.findViewById(R.id.view_pager_slider);


        }

        ///////////// banner slider
        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {

            currentPage = 2;
            if (timer != null) {
                timer.cancel();
            }
            arrangeModelList = new ArrayList<>();
            for (int x = 0; x < sliderModelList.size(); x++) {
                arrangeModelList.add(x, sliderModelList.get(x));
            }
            arrangeModelList.add(0, sliderModelList.get(sliderModelList.size() - 2));
            arrangeModelList.add(1, sliderModelList.get(sliderModelList.size() - 1));
            arrangeModelList.add(sliderModelList.get(0));
            arrangeModelList.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangeModelList);
            bannerSlider.setAdapter(sliderAdapter);
            bannerSlider.setClipToPadding(false);
            bannerSlider.setPageMargin(20);

            bannerSlider.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        PageLooper(arrangeModelList);
                    }
                }
            };
            bannerSlider.addOnPageChangeListener(onPageChangeListener);

            StartBannerSlideShow(arrangeModelList);
            bannerSlider.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    PageLooper(arrangeModelList);
                    StopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        StartBannerSlideShow(arrangeModelList);
                    }
                    return false;
                }
            });
        }

        private void PageLooper(List<SliderModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerSlider.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerSlider.setCurrentItem(currentPage, false);
            }
        }

        private void StartBannerSlideShow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSlider.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void StopBannerSlideShow() {
            timer.cancel();
        }
        ///////////// banner slider
    }

    public class StripAddBannerViewHolder extends RecyclerView.ViewHolder {
        ImageView stripAddImage;
        ConstraintLayout stripContainer;

        public StripAddBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            stripAddImage = itemView.findViewById(R.id.strip_add);
            stripContainer = itemView.findViewById(R.id.strip_contaner);
        }

        public void setStripAdd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).placeholder(R.drawable.ny_home).into(stripAddImage);
            stripContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalScrollViewHolder extends RecyclerView.ViewHolder {
        TextView horizontalScrollText, horizontalScrollButton;
        RecyclerView horizontalScrollRecyclerView;
        ConstraintLayout container;

        public HorizontalScrollViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalScrollRecyclerView = itemView.findViewById(R.id.horizontal_scroll_recyclerView);
            horizontalScrollButton = itemView.findViewById(R.id.horizontal_scroll_btn);
            horizontalScrollText = itemView.findViewById(R.id.horizontal_scroll_text);
            horizontalScrollRecyclerView.setRecycledViewPool(recycledViewPool);
            container = itemView.findViewById(R.id.container);
        }

        private void setHorizontalScrollLayout(List<HorizontalScrollModel> horizontalScrollModelList, String title, String color, List<WishlistModel> viewProductList) {
            horizontalScrollText.setText(title);
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));

            for (HorizontalScrollModel model : horizontalScrollModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {

                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                model.setProductTitle(task.getResult().getString("product_title"));
                                model.setProductPrice(task.getResult().getString("product_price"));
                                model.setProductImage(task.getResult().getString("product_image_1"));

                                WishlistModel wishlistModel = viewProductList
                                        .get(horizontalScrollModelList.indexOf(model));

                                wishlistModel.setTotalRatting(task.getResult().getLong("total_ratings"));
                                wishlistModel.setRatting(task.getResult().getString("avrage_rating"));
                                wishlistModel.setProductTitle(task.getResult().getString("avrage_rating"));
                                wishlistModel.setProductImage(task.getResult().getString("avrage_rating"));
                                wishlistModel.setCuttedPrice(task.getResult().getString("avrage_rating"));
                                wishlistModel.setFreeCoupons(task.getResult().getLong("free_coupens"));
                                wishlistModel.setCuttedPrice(task.getResult().getString("avrage_rating"));
                                wishlistModel.setCod(task.getResult().getBoolean("cod"));
                                wishlistModel.setInStock(task.getResult().getLong("stock_quantity") > 0);

                                if (horizontalScrollModelList.indexOf(model) == horizontalScrollModelList.size() - 1) {
                                    if (horizontalScrollRecyclerView.getAdapter() != null) {
                                        horizontalScrollRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            } else {

                            }
                        }
                    });
                }
            }

            if (horizontalScrollModelList.size() > 8) {
                horizontalScrollButton.setVisibility(View.VISIBLE);
                horizontalScrollButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewProductList;
                        Intent intent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        intent.putExtra("Layout_code", 0);
                        intent.putExtra("title", title);
                        itemView.getContext().startActivity(intent);
                    }
                });
            } else {
                horizontalScrollButton.setVisibility(View.INVISIBLE);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            horizontalScrollRecyclerView.setLayoutManager(layoutManager);
            HorizontalScrollAdapter horizontalScrollAdapter = new HorizontalScrollAdapter(horizontalScrollModelList);
            horizontalScrollRecyclerView.setAdapter(horizontalScrollAdapter);
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {
        TextView gridTitle;
        Button gridButton;
        GridLayout gridProductLayout;
        ConstraintLayout container;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridTitle = itemView.findViewById(R.id.grid_product_title);
            gridButton = itemView.findViewById(R.id.grid_product_button);
            gridProductLayout = itemView.findViewById(R.id.grid_layout);
            container = itemView.findViewById(R.id.container);
        }

        private void setGridProductView(List<HorizontalScrollModel> horizontalScrollModelList, String title, String color) {
            gridTitle.setText(title);
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));

            for (HorizontalScrollModel model : horizontalScrollModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {

                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                model.setProductTitle(task.getResult().getString("product_title"));
                                model.setProductPrice(task.getResult().getString("product_price"));
                                model.setProductImage(task.getResult().getString("product_image_1"));

                                if (horizontalScrollModelList.indexOf(model) == horizontalScrollModelList.size() - 1) {
                                    setGridData(horizontalScrollModelList, title);

                                    if (!title.equals("")) {
                                        gridButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ViewAllActivity.horizontalScrollModelList = horizontalScrollModelList;
                                                Intent intent = new Intent(itemView.getContext(), ViewAllActivity.class);
                                                intent.putExtra("Layout_code", 1);
                                                intent.putExtra("title", title);
                                                itemView.getContext().startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            } else {

                            }
                        }
                    });
                }
            }
            setGridData(horizontalScrollModelList, title);
        }

        private void setGridData(List<HorizontalScrollModel> horizontalScrollModelList, String title) {
            for (int x = 0; x < 4; x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_item);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_title);
                TextView productDesc = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_decs);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_price);

                Glide.with(itemView.getContext()).load(horizontalScrollModelList.get(x).getProductImage()).into(productImage);
                productTitle.setText(horizontalScrollModelList.get(x).getProductTitle());
                productDesc.setText(horizontalScrollModelList.get(x).getProductDescription());
                productPrice.setText("Rs." + horizontalScrollModelList.get(x).getProductPrice() + "/-");

                if (!title.equals("")) {
                    int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            intent1.putExtra("PRODUCT_ID", horizontalScrollModelList.get(finalX).getProductID());
                            itemView.getContext().startActivity(intent1);
                        }
                    });
                }
            }
        }

    }
}
