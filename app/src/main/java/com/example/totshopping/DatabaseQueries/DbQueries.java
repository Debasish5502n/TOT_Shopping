package com.example.totshopping.DatabaseQueries;

import static com.example.totshopping.Activity.ProductDetailsActivity.add_wish_list;
import static com.example.totshopping.Activity.ProductDetailsActivity.initialRating;
import static com.example.totshopping.Activity.ProductDetailsActivity.productID;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.totshopping.Activity.AddAddressActivity;
import com.example.totshopping.Activity.DeliveryActivity;
import com.example.totshopping.Activity.NotificationActivity;
import com.example.totshopping.Activity.ProductDetailsActivity;
import com.example.totshopping.Adapter.ChategoryAdapter;
import com.example.totshopping.Adapter.HomePageAdapter;
import com.example.totshopping.Adapter.MyOrderAdapter;
import com.example.totshopping.Fregment.HomeFragment;
import com.example.totshopping.Fregment.MyCartFragment;
import com.example.totshopping.Fregment.MyOrderFragment;
import com.example.totshopping.Fregment.MyRewardFragment;
import com.example.totshopping.Fregment.WishlistFragment;
import com.example.totshopping.Model.CartItemModel;
import com.example.totshopping.Model.CategoryModel;
import com.example.totshopping.Model.HomePageModel;
import com.example.totshopping.Model.HorizontalScrollModel;
import com.example.totshopping.Model.MyAddressesModel;
import com.example.totshopping.Model.MyOrderModel;
import com.example.totshopping.Model.NotificationModel;
import com.example.totshopping.Model.RewardModel;
import com.example.totshopping.Model.SliderModel;
import com.example.totshopping.Model.WishlistModel;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbQueries {

    public static String fullname,email,profile;

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> list = new ArrayList<>();
    public static List<String> loadCategoriesName = new ArrayList<>();
    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selectedAddress = -1;
    public static List<MyAddressesModel> addressesModelList = new ArrayList<>();

    public static List<RewardModel> rewardModelList = new ArrayList<>();

    public static List<MyOrderModel> orderModelList = new ArrayList<>();


    public static List<NotificationModel> notificationModels = new ArrayList<>();
    private static ListenerRegistration registration;


    public static void loadCategories(final RecyclerView categoryRecyclerview, Context context) {

        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            ChategoryAdapter chategoryAdapter = new ChategoryAdapter(categoryModelList);
                            categoryRecyclerview.setAdapter(chategoryAdapter);
                            chategoryAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragment(final RecyclerView homePageRecyclerView, Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((long) documentSnapshot.get("view_type") == 0) {

                                    List<SliderModel> sliderModelList = new ArrayList<>();

                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                    for (long x = 1; x < no_of_banners + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(),
                                                documentSnapshot.get("banner_" + x + "_baground").toString()));
                                    }
                                    list.get(index).add(new HomePageModel(0, sliderModelList));
                                } else if ((long) documentSnapshot.get("view_type") == 1) {
                                    list.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(),
                                            documentSnapshot.get("baground").toString()));
                                } else if ((long) documentSnapshot.get("view_type") == 2) {

                                    List<HorizontalScrollModel> horizontalScrollModelList = new ArrayList<>();
                                    List<WishlistModel> viewProductList = new ArrayList<>();

                                    ArrayList<String> productIds= (ArrayList<String>) documentSnapshot.get("products");
                                    for (String productId:productIds) {
                                        horizontalScrollModelList.add(new HorizontalScrollModel(productId,
                                               "",
                                               "",
                                                "",
                                                ""));

                                        viewProductList.add(new WishlistModel(productId,
                                                "",
                                                "",
                                                0,
                                                "",
                                                0,
                                                "",
                                                "",
                                                false,
                                                false));

                                    }
                                    list.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_baground").toString(), horizontalScrollModelList, viewProductList));

                                } else if ((long) documentSnapshot.get("view_type") == 3) {

                                    List<HorizontalScrollModel> gridlayoutModelList = new ArrayList<>();
                                    ArrayList<String> productIds= (ArrayList<String>) documentSnapshot.get("products");
                                    for (String productId:productIds) {
                                        gridlayoutModelList.add(new HorizontalScrollModel(productId,
                                                "",
                                                "",
                                                "",
                                                ""));

                                    }
                                    list.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_baground").toString(), gridlayoutModelList));

                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        HomePageAdapter homePageAdapter = new HomePageAdapter(list.get(index));
                        homePageRecyclerView.setAdapter(homePageAdapter);
                        homePageAdapter.notifyDataSetChanged();
                        HomeFragment.swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public static void loadWishList(final Context context, Dialog dialog, boolean loadProductData) {
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        wishList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DbQueries.wishList.contains(productID)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if (add_wish_list != null) {
                                add_wish_list.setSupportImageTintList(context.getResources().getColorStateList(R.color.red));
                            }
                        } else {
                            if (add_wish_list != null) {
                                add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        }

                        if (loadProductData) {
                            wishlistModelList.clear();
                            String product_Id = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(product_Id)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        firebaseFirestore.collection("PRODUCTS").document(product_Id).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                wishlistModelList.add(new WishlistModel(product_Id,
                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                        documentSnapshot.get("avrage_rating").toString(),
                                                                        (long) documentSnapshot.get("total_ratings"),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (Boolean) documentSnapshot.get("cod"),
                                                                        true));
                                                            } else {
                                                                wishlistModelList.add(new WishlistModel(product_Id,
                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                        documentSnapshot.get("avrage_rating").toString(),
                                                                        (long) documentSnapshot.get("total_ratings"),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (Boolean) documentSnapshot.get("cod"),
                                                                        false));
                                                            }
                                                            WishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });


                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.dismiss();
    }

    public static void removeFromWishlist(int index, Context context) {
        final String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < wishList.size(); x++) {
            updateWishlist.put("product_ID_" + x, wishList.get(x));
        }
        updateWishlist.put("list_size", (long) wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (wishlistModelList.size() != 0) {
                        wishlistModelList.remove(index);

                        WishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                    //  add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));

                } else {
                    wishList.add(index, removedProductId);
                    if (add_wish_list != null) {
                        add_wish_list.setSupportImageTintList(context.getResources().getColorStateList(R.color.red));
                    }
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_wishlist_query = false;
            }
        });
    }

    public static void loadRating(Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRating.clear();
            myRatedIds.clear();

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> orderProductId = new ArrayList<>();
                        for (int x = 0; x < orderModelList.size(); x++) {
                            orderProductId.add(orderModelList.get(x).getProductId());
                        }

                        for (int x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));
                            if (task.getResult().get("product_ID_" + x).toString().equals(productID)) {
                                initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x) - 1));
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(initialRating);
                                }
                            }
                            if (orderProductId.contains(task.getResult().get("product_ID_" + x).toString())) {
                                orderModelList.get(orderProductId.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x) - 1)));
                            }
                        }
                        if (MyOrderFragment.adapter != null) {
                            MyOrderFragment.adapter.notifyDataSetChanged();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, Dialog dialog, boolean loadProductData, final TextView badgeCount, TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DbQueries.cartList.contains(productID)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }

                        if (loadProductData) {
                            cartItemModelList.clear();
                            String product_Id = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(product_Id)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        firebaseFirestore.collection("PRODUCTS").document(product_Id).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            int index = 0;
                                                            if (cartList.size() >= 2) {
                                                                index = cartList.size() - 2;
                                                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                                parent.setVisibility(View.VISIBLE);
                                                            }

                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                cartItemModelList.add(index, new CartItemModel(documentSnapshot.getBoolean("cod"),
                                                                        CartItemModel.CART_ITEM, product_Id,
                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (long) 1,
                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                        (long) 0,
                                                                        true,
                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                        (long) documentSnapshot.get("stock_quantity")));
                                                            } else {
                                                                cartItemModelList.add(index, new CartItemModel(documentSnapshot.getBoolean("cod"),
                                                                        CartItemModel.CART_ITEM, product_Id,
                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (long) 1,
                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                        (long) 0,
                                                                        false,
                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                        (long) documentSnapshot.get("stock_quantity")));
                                                            }
                                                            if (cartList.size() == 1) {
                                                                cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_ITEM));
                                                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                                parent.setVisibility(View.VISIBLE);
                                                            }
                                                            if (cartList.size() == 0) {
                                                                cartItemModelList.clear();
                                                            }

                                                            MyCartFragment.cartAdapter.notifyDataSetChanged();

                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.INVISIBLE);
                    } else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DbQueries.cartList.size()));
                    } else {
                        badgeCount.setText("99+");
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.dismiss();
    }

    public static void removeFromCartlist(int index, Context context, TextView cartTotalAmount) {
        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateWishlist.put("product_ID_" + x, cartList.get(x));
        }
        updateWishlist.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);

                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }

                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                    //  add_wish_list.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#8F8F8F")));

                } else {
                    cartList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, Dialog loadingDialog,boolean goToDeliveryActivity) {
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESS")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Intent deliveryIntent = null;
                    if ((long) task.getResult().get("list_size") == 0) {
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                    } else {

                        for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                            addressesModelList.add(new MyAddressesModel(task.getResult().getBoolean("selected_"+x),
                                    task.getResult().getString("city_"+x),
                                    task.getResult().getString("locality_"+x),
                                    task.getResult().getString("flat_no_"+x),
                                    task.getResult().getString("pinCode_"+x),
                                    task.getResult().getString("landmark_"+x),
                                    task.getResult().getString("name_"+x),
                                    task.getResult().getString("mobile_no_"+x),
                                    task.getResult().getString("alternative_mobile_no_"+x),
                                    task.getResult().getString("state_"+x)));
                            if ((boolean) task.getResult().get("selected_" + x)) {
                                selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                            }
                        }
                        if (goToDeliveryActivity) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if (goToDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void loadReward(Context context, Dialog loadingDialog, boolean onRewardFragment) {
        rewardModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Date lastSeenDate = task.getResult().getDate("Last seen");

                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                    if (documentSnapshot.get("type").toString().equals("Discount") && lastSeenDate.before(documentSnapshot.getDate("validity"))) {
                                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(), documentSnapshot.get("type").toString(),
                                                                documentSnapshot.get("lower_limit").toString(),
                                                                documentSnapshot.get("upper_limit").toString(),
                                                                documentSnapshot.get("percentage").toString(),
                                                                documentSnapshot.get("body").toString(),
                                                                documentSnapshot.getDate("validity"),
                                                                (boolean) documentSnapshot.get("already_used")));
                                                    } else if (documentSnapshot.get("type").toString().equals("Flat Rs.* OFF") && lastSeenDate.before(documentSnapshot.getDate("validity"))) {
                                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(), documentSnapshot.get("type").toString(),
                                                                documentSnapshot.get("lower_limit").toString(),
                                                                documentSnapshot.get("upper_limit").toString(),
                                                                documentSnapshot.get("amount").toString(),
                                                                documentSnapshot.get("body").toString(),
                                                                documentSnapshot.getDate("validity"),
                                                                (boolean) documentSnapshot.get("already_used")));
                                                    }
                                                }
                                                if (onRewardFragment) {
                                                    MyRewardFragment.rewardAdapter.notifyDataSetChanged();
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                            loadingDialog.dismiss();

                                        }
                                    });

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });

    }

    public static void loadOrder(Context context, @Nullable MyOrderAdapter adapter, Dialog loadingDialog) {
        orderModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDER").orderBy("time",Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems")
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {

                                                orderModelList.add(0,new MyOrderModel(orderItems.getString("Product Id"),
                                                        orderItems.getString("Order Status"),
                                                        orderItems.getString("Address"),
                                                        orderItems.getString("Coupon Id"),
                                                        orderItems.getString("Cutted Price"),
                                                        orderItems.getDate("Ordered Date"),
                                                        orderItems.getDate("Packed Date"),
                                                        orderItems.getDate("Shipped Date"),
                                                        orderItems.getDate("Delivered Date"),
                                                        orderItems.getDate("Cancelled Date"),
                                                        orderItems.getString("Discounted Price"),
                                                        orderItems.getLong("Free Coupons"),
                                                        orderItems.getString("FullName"),
                                                        orderItems.getString("ORDER ID"),
                                                        orderItems.getString("Payment Method"),
                                                        orderItems.getString("PinCode"),
                                                        orderItems.getString("Product Price"),
                                                        orderItems.getLong("Product Quantity"),
                                                        orderItems.getString("User Id"),
                                                        orderItems.getString("Product Title"),
                                                        orderItems.getString("Product Image"),
                                                        orderItems.getString("Delivery Price"),
                                                        orderItems.getBoolean("Cancellation requested")));

                                                loadRating(context);
                                                if (adapter !=null) {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void checkNotification(boolean remove,@Nullable TextView notifyCount){

        if (remove){
            registration.remove();
        }else {
            registration= firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATION")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                            if (documentSnapshot !=null && documentSnapshot.exists()){
                                notificationModels.clear();
                                int unRead =0;
                                for (long x = 0; x < (long) documentSnapshot.get("list_size"); x++) {
                                    notificationModels.add(0,new NotificationModel(documentSnapshot.get("Image_"+x).toString()
                                            ,documentSnapshot.get("Body_"+x).toString()
                                            ,documentSnapshot.getBoolean("Readed_"+x)));

                                    if (!documentSnapshot.getBoolean("Readed_"+x)){
                                        if (notifyCount !=null){
                                            unRead++;
                                            if (unRead >0){
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (unRead < 99) {
                                                    notifyCount.setText(String.valueOf(unRead));
                                                } else {
                                                    notifyCount.setText("99+");
                                                }
                                            }else {
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                                if (NotificationActivity.adapter !=null){
                                    NotificationActivity.adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }

    }

    public static void clearData() {
        list.clear();
        loadCategoriesName.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressesModelList.clear();
        rewardModelList.clear();
    }
}
