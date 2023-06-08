package com.example.totshopping.Activity;

import static com.example.totshopping.Activity.MainActivity.showCart;
import static com.example.totshopping.Activity.RegistrationActivity.signUpFragment;
import static com.example.totshopping.DatabaseQueries.DbQueries.cartItemModelList;
import static com.example.totshopping.DatabaseQueries.DbQueries.cartList;
import static com.example.totshopping.DatabaseQueries.DbQueries.loadAddresses;
import static com.example.totshopping.DatabaseQueries.DbQueries.removeFromWishlist;
import static com.example.totshopping.DatabaseQueries.DbQueries.wishList;
import static com.example.totshopping.DatabaseQueries.DbQueries.wishlistModelList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.totshopping.Adapter.ProductDetailsAdapter;
import com.example.totshopping.Adapter.ProductImagesAdapter;
import com.example.totshopping.Adapter.RewardAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Fregment.SigniinFregment;
import com.example.totshopping.Fregment.SignupFragment;
import com.example.totshopping.Model.CartItemModel;
import com.example.totshopping.Model.ProductSpecifiactionModel;
import com.example.totshopping.Model.WishlistModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;
    public static Activity productDetailsActivity;

    public static boolean fromSearch=false;

    ViewPager productImageViewpager;
    TabLayout viewPagerIndicator;
    AppCompatButton buyNowBtn;
    AppCompatButton couponRedemptionBtn;
    TextView productTitle, avrageRatingMiniView, totalRatingMiniView, productPrice, cuttedPrice, cod_indicator;
    String productOriginalPrice;
    ImageView cod_indicator_icon;
    TextView codTitle;
    TextView rewardTitle, rewardBody;
    Dialog signDialog;
    LinearLayout add_to_cart_btn;
    FirebaseUser currentUser;

    ///////////product Description
    ViewPager productDetailsViewpager;
    TabLayout productDetailsTabLayout;
    ConstraintLayout productDetailsOnlyContainer, getProductDetailsTabsContainer;
    TextView productsDetailsBody;
    List<ProductSpecifiactionModel> productSpecifiactionModelList = new ArrayList<>();
    String productDescription;
    String productOtherDetails;
    ///////////product Description

    ///////////ratings layout
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    TextView total_ratings, totalRatingFigure, avrageRating;
    LinearLayout ratingNumberContainer, ratingProgressBar;
    ///////////ratings layout


    public static Boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static Boolean ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton add_wish_list;
    public static MenuItem cartMenu;

    FirebaseFirestore firebaseFirestore;
    Dialog loadingDialog;
    DocumentSnapshot documentSnapshot;
    ////////////coupon Dialog
    private TextView couponTitle;
    private TextView couponExpiryDate;
    private TextView couponBody;
    private RecyclerView couponRecyclerView;
    private LinearLayout selectedCoupon;
    TextView badgeCount;

    ////////////coupon Dialog
    public static String productID;
    private boolean inStock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        productImageViewpager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        add_wish_list = findViewById(R.id.add_wish_list);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        couponRedemptionBtn = findViewById(R.id.coupon_redemption_btn);
        productTitle = findViewById(R.id.product_title);
        avrageRatingMiniView = findViewById(R.id.avg_rating_mini_view);
        totalRatingMiniView = findViewById(R.id.total_ratting_mini_view);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        cod_indicator = findViewById(R.id.cod_indicator);
        cod_indicator_icon = findViewById(R.id.cod_indicator_icon);
        codTitle = findViewById(R.id.cod_title);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        getProductDetailsTabsContainer = findViewById(R.id.product_details_tab_container);
        productsDetailsBody = findViewById(R.id.product_details_body);
        total_ratings = findViewById(R.id.total_ratings);
        ratingNumberContainer = findViewById(R.id.ratings_number_container);
        ratingProgressBar = findViewById(R.id.ratings_progressbar);
        totalRatingFigure = findViewById(R.id.total_ratings_figure);
        avrageRating = findViewById(R.id.avrage_rating);
        add_to_cart_btn = findViewById(R.id.add_to_cart_btn);

        viewPagerIndicator.setupWithViewPager(productImageViewpager, true);

        productDetailsViewpager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);


        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initialRating = -1;

        ////////////////loading dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog
        productID = getIntent().getStringExtra("PRODUCT_ID");

           /////////coupon redemption DialogD
        final Dialog couponRedemptionDialog = new Dialog(ProductDetailsActivity.this);
        couponRedemptionDialog.setContentView(R.layout.coupen_redeemption_dialog);
        couponRedemptionDialog.setCancelable(true);
        couponRedemptionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        couponRecyclerView = couponRedemptionDialog.findViewById(R.id.coupons_recyclerView);
        ImageView toggleRecyclerView = couponRedemptionDialog.findViewById(R.id.toogle_recyclerView);
        selectedCoupon = couponRedemptionDialog.findViewById(R.id.selected_coupon);
        TextView originalPrice = couponRedemptionDialog.findViewById(R.id.original_price);
        TextView discountedPrice = couponRedemptionDialog.findViewById(R.id.coupon_price);

        couponTitle = couponRedemptionDialog.findViewById(R.id.reward_title);
        couponExpiryDate = couponRedemptionDialog.findViewById(R.id.reward_validDate);
        couponBody = couponRedemptionDialog.findViewById(R.id.reward_body);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        couponRecyclerView.setLayoutManager(layoutManager);

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDilogRecyclerView();
            }
        });
        /////////coupon redemption DialogD

        add_wish_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signDialog.show();
                } else {
                    //  add_wish_list.setEnabled(false);
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = wishList.indexOf(productID);
                            removeFromWishlist(index, ProductDetailsActivity.this);

                            add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));
                        } else {
                            add_wish_list.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DbQueries.wishList.size()), productID);
                            addProduct.put("list_size", DbQueries.wishList.size() + 1);

                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        if (wishlistModelList.size() != 0) {
                                            wishlistModelList.add(new WishlistModel(productID,
                                                    documentSnapshot.get("product_image_1").toString(),
                                                    documentSnapshot.get("product_title").toString(),
                                                    (long) documentSnapshot.get("free_coupens"),
                                                    documentSnapshot.get("avrage_rating").toString(),
                                                    (long) documentSnapshot.get("total_ratings"),
                                                    documentSnapshot.get("product_price").toString(),
                                                    documentSnapshot.get("cutted_price").toString(),
                                                    (Boolean) documentSnapshot.get("cod"),
                                                    inStock));
                                        }
                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        add_wish_list.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                        DbQueries.wishList.add(productID);
                                        Toast.makeText(ProductDetailsActivity.this, "Product added wishlist successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;
                                }
                            });


                        }
                    }
                }
            }
        });


        List<String> productImages = new ArrayList<>();
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();

                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        for (long x = 1; x < (long) documentSnapshot.get("no_of_products_images") + 1; x++) {
                                            productImages.add(documentSnapshot.get("product_image_" + x).toString());

                                        }
                                        ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                        productImageViewpager.setAdapter(productImagesAdapter);

                                        productTitle.setText(documentSnapshot.get("product_title").toString());
                                        avrageRatingMiniView.setText(documentSnapshot.get("avrage_rating").toString());
                                        avrageRating.setText(documentSnapshot.get("avrage_rating").toString());
                                        totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ")Ratings");
                                        productPrice.setText("Rs." + documentSnapshot.get("product_price") + "/-");

                                        ///////for coupon dialog
                                        originalPrice.setText(productPrice.getText());
                                        productOriginalPrice =documentSnapshot.get("product_price").toString();
                                        RewardAdapter adapter = new RewardAdapter(DbQueries.rewardModelList, true,couponRecyclerView,selectedCoupon,productOriginalPrice,couponTitle,couponExpiryDate,couponBody,discountedPrice);
                                        couponRecyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        ///////for coupon dialog

                                        cuttedPrice.setText("Rs." + documentSnapshot.get("cutted_price") + "/-");
                                        if ((boolean) documentSnapshot.get("cod")) {
                                            cod_indicator_icon.setVisibility(View.VISIBLE);
                                            cod_indicator.setVisibility(View.VISIBLE);
                                            codTitle.setVisibility(View.VISIBLE);
                                        } else {
                                            cod_indicator_icon.setVisibility(View.INVISIBLE);
                                            cod_indicator.setVisibility(View.INVISIBLE);
                                            codTitle.setVisibility(View.INVISIBLE);
                                        }
                                        rewardTitle.setText((long) documentSnapshot.get("free_coupens") + documentSnapshot.get("free_coupen_title").toString());
                                        rewardBody.setText(documentSnapshot.get("free_coupen_body").toString());

                                        if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                            getProductDetailsTabsContainer.setVisibility(View.VISIBLE);
                                            productDetailsOnlyContainer.setVisibility(View.GONE);
                                            productDescription = documentSnapshot.get("product_description").toString();
                                            productSpecifiactionModelList = new ArrayList<>();

                                            for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                                productSpecifiactionModelList.add(new ProductSpecifiactionModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                                for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fealds") + 1; y++) {
                                                    productSpecifiactionModelList.add(new ProductSpecifiactionModel(1, documentSnapshot.get("spec_title_" + x + "_feald_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_feald_" + y + "_value").toString()));
                                                }
                                            }
                                            productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                        } else {
                                            getProductDetailsTabsContainer.setVisibility(View.GONE);
                                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                            productsDetailsBody.setText(documentSnapshot.get("product_other_details").toString());
                                        }
                                        total_ratings.setText("(" + (long) documentSnapshot.get("total_ratings") + ")Ratings");

                                        for (int x = 0; x < 5; x++) {
                                            TextView rating = (TextView) ratingNumberContainer.getChildAt(x);
                                            rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                            ProgressBar progressBar = (ProgressBar) ratingProgressBar.getChildAt(x);
                                            int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                            progressBar.setMax(maxProgress);
                                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                        }
                                        totalRatingFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                        productDetailsViewpager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecifiactionModelList));

                                        if (currentUser != null) {
                                            if (DbQueries.myRating.size() == 0) {
                                                DbQueries.loadRating(ProductDetailsActivity.this);
                                            }
                                            if (DbQueries.cartList.size() == 0) {
                                                DbQueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                            }
                                            if (DbQueries.wishList.size() == 0) {
                                                DbQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                                            }
                                            if (DbQueries.rewardModelList.size() == 0) {
                                                DbQueries.loadReward(ProductDetailsActivity.this, loadingDialog,false);
                                            }
                                            if (DbQueries.cartList.size() != 0 &&DbQueries.wishList.size() != 0 &&DbQueries.rewardModelList.size() != 0){
                                                loadingDialog.dismiss();
                                            }
                                        } else {
                                            loadingDialog.dismiss();
                                        }

                                        if (DbQueries.myRatedIds.contains(productID)) {
                                            int index = DbQueries.myRatedIds.indexOf(productID);
                                            initialRating = Integer.parseInt(String.valueOf(DbQueries.myRating.get(index))) - 1;
                                            setRating(initialRating);

                                        }

                                        if (DbQueries.cartList.contains(productID)) {
                                            ALREADY_ADDED_TO_CART = true;
                                        } else {
                                            ALREADY_ADDED_TO_CART = false;
                                        }

                                        if (DbQueries.wishList.contains(productID)) {
                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            add_wish_list.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                        } else {
                                            add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));
                                            ALREADY_ADDED_TO_WISHLIST = false;
                                        }

                                        if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                            inStock=true;
                                            buyNowBtn.setVisibility(View.VISIBLE);
                                            add_to_cart_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (currentUser == null) {
                                                        signDialog.show();
                                                    } else {
                                                        ////// add to cart
                                                        if (!running_cart_query) {
                                                            running_cart_query = true;
                                                            if (ALREADY_ADDED_TO_CART) {
                                                                running_cart_query = false;
                                                                Toast.makeText(ProductDetailsActivity.this, "Already Added to cart", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Map<String, Object> addProduct = new HashMap<>();
                                                                addProduct.put("product_ID_" + DbQueries.cartList.size(), productID);
                                                                addProduct.put("list_size", DbQueries.cartList.size() + 1);

                                                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            if (wishlistModelList.size() != 0) {
                                                                                cartItemModelList.add(0, new CartItemModel(documentSnapshot.getBoolean("cod"),
                                                                                        CartItemModel.CART_ITEM, productID,
                                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                                        documentSnapshot.get("product_title").toString(),
                                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                                        documentSnapshot.get("product_price").toString(),
                                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                                        (long) 1,
                                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                                        (long) 0,
                                                                                        inStock,
                                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                                        (long) documentSnapshot.get("stock_quantity")));
                                                                            }
                                                                            ALREADY_ADDED_TO_CART = true;
                                                                            DbQueries.cartList.add(productID);
                                                                            Toast.makeText(ProductDetailsActivity.this, "Product added cart successfully", Toast.LENGTH_SHORT).show();
                                                                            invalidateOptionsMenu();
                                                                            running_cart_query = false;

                                                                        } else {
                                                                            running_cart_query = false;
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });


                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                        } else {
                                            inStock = false;
                                            buyNowBtn.setVisibility(View.GONE);
                                            TextView outOfStock = (TextView) add_to_cart_btn.getChildAt(0);
                                            outOfStock.setText("Out of stock");
                                            outOfStock.setTextColor(getResources().getColor(R.color.red));
                                            outOfStock.setCompoundDrawables(null, null, null, null);
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        productDetailsViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ///////////ratings layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starPosition);

                                Map<String, Object> updateRating = new HashMap<>();
                                TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);

                                if (DbQueries.myRatedIds.contains(productID)) {
                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("avrage_rating", calculateAverageRating((long) starPosition - initialRating, true));

                                } else {
                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("avrage_rating", calculateAverageRating((long) starPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }
                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myRating = new HashMap<>();
                                            if (DbQueries.myRatedIds.contains(productID)) {
                                                myRating.put("rating_" + DbQueries.myRatedIds.indexOf(productID), (long) starPosition + 1);
                                            } else {
                                                myRating.put("list_size", (long) DbQueries.myRatedIds.size() + 1);
                                                myRating.put("product_ID_" + DbQueries.myRatedIds.size(), productID);
                                                myRating.put("rating_" + DbQueries.myRatedIds.size(), (long) starPosition + 1);
                                            }

                                            FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (DbQueries.myRatedIds.contains(productID)) {
                                                            DbQueries.myRating.set(DbQueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);
                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                        } else {
                                                            DbQueries.myRatedIds.add(productID);
                                                            DbQueries.myRating.add((long) starPosition + 1);

                                                            TextView rating1 = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);
                                                            rating1.setText(String.valueOf(Integer.parseInt(rating1.getText().toString()) + 1));

                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")\tRatings");
                                                            total_ratings.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")Ratings");
                                                            totalRatingFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));


                                                            Toast.makeText(getApplicationContext(), "Thank you ! for rating", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingFigure = (TextView) ratingNumberContainer.getChildAt(x);
                                                            ProgressBar progressBar = (ProgressBar) ratingProgressBar.getChildAt(x);
                                                            int maxProgress = Integer.parseInt(totalRatingFigure.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt(ratingFigure.getText().toString()));
                                                        }
                                                        initialRating = starPosition;
                                                        avrageRating.setText(calculateAverageRating(0, true));
                                                        avrageRatingMiniView.setText(calculateAverageRating(0, true));

                                                        if (wishList.contains(productID) && wishlistModelList.size() != 0) {
                                                            int index = wishList.indexOf(productID);
                                                            wishlistModelList.get(index).setRatting(avrageRating.getText().toString());
                                                            wishlistModelList.get(index).setTotalRatting(Long.parseLong(totalRatingFigure.getText().toString()));
                                                        }
                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            running_rating_query = false;
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

        }
        ///////////ratings layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("cod"),
                            CartItemModel.CART_ITEM, productID,
                            documentSnapshot.get("product_image_1").toString(),
                            documentSnapshot.get("product_title").toString(),
                            (long) documentSnapshot.get("free_coupens"),
                            documentSnapshot.get("product_price").toString(),
                            documentSnapshot.get("cutted_price").toString(),
                            (long) 1,
                            (long) documentSnapshot.get("offers_applied"),
                            (long) 0,
                            inStock,
                            (long) documentSnapshot.get("max-quantity"),
                            (long) documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_ITEM));
                    if (DbQueries.addressesModelList.size() == 0) {
                        loadAddresses(ProductDetailsActivity.this, loadingDialog,true);
                    } else {
                        loadingDialog.dismiss();
                        Intent intent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });



        couponRedemptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                couponRedemptionDialog.show();
            }
        });

        signDialog = new Dialog(ProductDetailsActivity.this);
        signDialog.setContentView(R.layout.sign_in_dialog);
        signDialog.setCancelable(true);
        signDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Intent intent = new Intent(ProductDetailsActivity.this, RegistrationActivity.class);

        Button simpleSign_in = signDialog.findViewById(R.id.sign_in_btn);
        Button simpleSign_up = signDialog.findViewById(R.id.sign_up_btn);

        simpleSign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigniinFregment.disableCloseBtn = true;
                SignupFragment.disableCloseBtn = true;
                signDialog.dismiss();
                signUpFragment = false;
                startActivity(intent);
            }
        });
        simpleSign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigniinFregment.disableCloseBtn = true;
                SignupFragment.disableCloseBtn = true;
                signDialog.dismiss();
                signUpFragment = true;
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            if (DbQueries.myRating.size() == 0) {
                DbQueries.loadRating(ProductDetailsActivity.this);
            }
            if (DbQueries.wishList.size() == 0) {
                DbQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (DbQueries.rewardModelList.size() == 0) {
                DbQueries.loadReward(ProductDetailsActivity.this, loadingDialog,false);
            }
            if (DbQueries.cartList.size() != 0 &&DbQueries.wishList.size() != 0 &&DbQueries.rewardModelList.size() != 0){
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }


        if (DbQueries.myRatedIds.contains(productID)) {
            int index = DbQueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DbQueries.myRating.get(index))) - 1;
            setRating(initialRating);

        }

        if (DbQueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (DbQueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            add_wish_list.setSupportImageTintList(getResources().getColorStateList(R.color.red));
        } else {

            add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));

            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    private void showDilogRecyclerView() {
        if (couponRecyclerView.getVisibility() == View.GONE) {
            couponRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);
        } else {
            selectedCoupon.setVisibility(View.VISIBLE);
            couponRecyclerView.setVisibility(View.GONE);
        }
    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        double totalStar = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingNumberContainer.getChildAt(5 - x);
            totalStar = totalStar + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStar = totalStar + currentUserRating;
        if (update) {
            return String.valueOf(totalStar / Long.parseLong(totalRatingFigure.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStar / (Long.parseLong(totalRatingFigure.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_card, menu);

        cartMenu = menu.findItem(R.id.main_card_icon);
        cartMenu.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartMenu.getActionView().findViewById(R.id.badge_icon);
        badgeCount = cartMenu.getActionView().findViewById(R.id.badhge_count);

        badgeIcon.setImageResource(R.drawable.card_white);
        if (currentUser != null) {
            if (DbQueries.cartList.size() == 0) {
                DbQueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            } else {
                badgeCount.setVisibility(View.VISIBLE);
                if (cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DbQueries.cartList.size()));
                } else {
                    badgeCount.setText("99+");
                }
            }
        }

        cartMenu.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signDialog.show();
                } else {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.main_search_icon) {
            if (fromSearch){
                finish();
            }else {
                Intent intent = new Intent(ProductDetailsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.main_card_icon) {
            if (currentUser == null) {
                signDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
            }
            return true;
        } else if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch=false;
    }
}