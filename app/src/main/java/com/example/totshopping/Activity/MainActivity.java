package com.example.totshopping.Activity;

import static com.example.totshopping.Activity.RegistrationActivity.signUpFragment;
import static com.example.totshopping.DatabaseQueries.DbQueries.cartList;
import static com.example.totshopping.DatabaseQueries.DbQueries.clearData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.Fregment.HomeFragment;
import com.example.totshopping.Fregment.MyAccountFragment;
import com.example.totshopping.Fregment.MyCartFragment;
import com.example.totshopping.Fregment.MyOrderFragment;
import com.example.totshopping.Fregment.MyRewardFragment;
import com.example.totshopping.Fregment.SigniinFregment;
import com.example.totshopping.Fregment.SignupFragment;
import com.example.totshopping.Fregment.WishlistFragment;
import com.example.totshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public static final int HOME_FRAGMENT = 0;
    public static final int CART_FRAGMENT = 1;
    public static final int MY_ORDER_FRAGMENT = 2;
    public static final int WISHLIST_FRAGMENT = 3;
    public static final int REWARD_FRAGMENT = 4;
    public static final int MY_ACCOUNT_FRAGMENT = 5;
    public static Boolean showCart = false;
    public static Boolean resetFragment = false;
    public static Activity MainActivity;

    public int CurrentFragment = -1;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    public static DrawerLayout drawerLayout;
    FrameLayout frameLayout;
    TextView toolbarTitle;
    Toolbar toolbar;
    Context context;
    private Window window;
    Dialog signDialog;
    TextView badgeCount;

    FirebaseUser currentUser;

    ShimmerRecyclerView shimmerRecyclerView;

    CircleImageView profileImage;
    TextView fullname,email;
    ImageView profileIcon;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        shimmerRecyclerView = findViewById(R.id.shimmer_recyclerView);
        shimmerRecyclerView.hideShimmerAdapter();
        toolbarTitle.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        nav = (NavigationView) findViewById(R.id.navmenu);
        nav.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.setVisibility(View.VISIBLE);
        shimmerRecyclerView.setVisibility(View.GONE);

        profileImage=nav.getHeaderView(0).findViewById(R.id.main_profile_image);
        fullname=nav.getHeaderView(0).findViewById(R.id.main_fullname);
        email=nav.getHeaderView(0).findViewById(R.id.email);
        profileIcon=nav.getHeaderView(0).findViewById(R.id.add_profile_icon);

        if (showCart) {
            MainActivity = this;
            drawerLayout.setDrawerLockMode(1);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            setCheckedChancel();
            invalidateOptionsMenu();
            toolbarTitle.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Bb Cart");
            setFragment(new MyCartFragment(), CART_FRAGMENT);
            nav.getMenu().getItem(3).setChecked(true);
        } else {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close);
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        }

        signDialog = new Dialog(MainActivity.this);
        signDialog.setContentView(R.layout.sign_in_dialog);
        signDialog.setCancelable(true);
        signDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);

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

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (currentUser != null) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_my_mall:
                            setCheckedChancel();
                            invalidateOptionsMenu();
                            getSupportActionBar().setDisplayShowTitleEnabled(false);
                            toolbarTitle.setVisibility(View.VISIBLE);
                            nav.getMenu().getItem(0).setChecked(true);
                            setFragment(new HomeFragment(), HOME_FRAGMENT);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.nav_my_orders:
                            myOrder();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.nav_my_rewards:
                            myReward();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.nav_my_card:
                            if (currentUser != null) {
                                myCart();
                            } else {
                                signDialog.show();
                            }
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.nav_my_wishlist:
                            myWishlist();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.nav_my_account:
                            myAccount();

                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.nav_sign_out:
                            FirebaseAuth.getInstance().signOut();
                            clearData();
                            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                            finish();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;
                    }
                } else {
                    signDialog.show();
                }
                return true;
            }

        });

        if (isconnected()) {
            drawerLayout.setDrawerLockMode(0);
            frameLayout.setVisibility(View.VISIBLE);
            shimmerRecyclerView.setVisibility(View.GONE);
        } else {
            drawerLayout.setDrawerLockMode(1);
            frameLayout.setVisibility(View.GONE);
            shimmerRecyclerView.setVisibility(View.VISIBLE);
            shimmerRecyclerView.showShimmerAdapter();
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
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

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            nav.getMenu().getItem(nav.getMenu().size() - 1).setEnabled(false);
        } else {

            if (DbQueries.email == null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DbQueries.fullname = task.getResult().getString("fullname");
                                    DbQueries.email = task.getResult().getString("email");
                                    DbQueries.profile = task.getResult().getString("profile");

                                    fullname.setText(DbQueries.fullname);
                                    email.setText(DbQueries.email);
                                    if (DbQueries.profile.equals("")) {
                                        profileIcon.setVisibility(View.VISIBLE);
                                    } else {
                                        profileIcon.setVisibility(View.INVISIBLE);
                                        Glide.with(MainActivity.this).load(DbQueries.profile).placeholder(R.drawable.avatarra).into(profileImage);
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                nav.getMenu().getItem(nav.getMenu().size() - 1).setEnabled(true);
            }else {
                fullname.setText(DbQueries.fullname);
                email.setText(DbQueries.email);
                if (DbQueries.profile.equals("")) {
                    profileImage.setImageResource(R.drawable.avatarra);
                    profileIcon.setVisibility(View.VISIBLE);
                } else {
                    profileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(MainActivity.this).load(DbQueries.profile).placeholder(R.drawable.avatarra).into(profileImage);
                }
            }
        }
            if (resetFragment) {
                resetFragment = false;
                toolbarTitle.setVisibility(View.VISIBLE);
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                nav.getMenu().getItem(0).setChecked(true);
            }
            invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DbQueries.checkNotification(true,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (CurrentFragment == HOME_FRAGMENT) {
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem cartMenu = menu.findItem(R.id.main_card_icon);
            cartMenu.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartMenu.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.card_white);
            badgeCount = cartMenu.getActionView().findViewById(R.id.badhge_count);
            if (currentUser != null) {

                if (DbQueries.cartList.size() == 0) {
                    DbQueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false, badgeCount, new TextView(MainActivity.this));
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
                        myCart();
                    }
                }
            });

            MenuItem notifyItem = menu.findItem(R.id.main_notification_icon);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.bell_icon);
            TextView  notifyCount= notifyItem.getActionView().findViewById(R.id.badhge_count);

            if (currentUser != null){
                DbQueries.checkNotification(false,notifyCount);
            }

            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,NotificationActivity.class);
                    startActivity(intent);
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.main_search_icon) {
         //   setFragment(new HomeFragment(), HOME_FRAGMENT);
            Intent intent=new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_notification_icon) {
            Intent intent=new Intent(MainActivity.this,NotificationActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_card_icon) {

            if (currentUser == null) {
                signDialog.show();
            } else {
                myCart();
            }
            return true;
        } else if (id == android.R.id.home) {
            MainActivity = null;
            showCart = false;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if (fragmentNo != CurrentFragment) {
            if (fragmentNo == REWARD_FRAGMENT) {
                window.setStatusBarColor(Color.parseColor("#FF6200EE"));
                toolbar.setBackgroundColor(Color.parseColor("#FF6200EE"));
            } else {
                window.setStatusBarColor(getResources().getColor(R.color.red));
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
            }
            CurrentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    private void setCheckedChancel() {
        nav.getMenu().getItem(0).setChecked(false);
        nav.getMenu().getItem(1).setChecked(false);
        nav.getMenu().getItem(2).setChecked(false);
        nav.getMenu().getItem(3).setChecked(false);
        nav.getMenu().getItem(4).setChecked(false);
        nav.getMenu().getItem(5).setChecked(false);
        nav.getMenu().getItem(6).setChecked(false);
    }

    private void myCart() {
        setCheckedChancel();
        invalidateOptionsMenu();
        toolbarTitle.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Cart");
        setFragment(new MyCartFragment(), CART_FRAGMENT);
        nav.getMenu().getItem(3).setChecked(true);
    }

    private void myOrder() {
        setCheckedChancel();
        invalidateOptionsMenu();
        toolbarTitle.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Order");
        setFragment(new MyOrderFragment(), MY_ORDER_FRAGMENT);
        nav.getMenu().getItem(1).setChecked(true);
    }

    private void myWishlist() {
        setCheckedChancel();
        invalidateOptionsMenu();
        toolbarTitle.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Wishlist");
        setFragment(new WishlistFragment(), WISHLIST_FRAGMENT);
        nav.getMenu().getItem(4).setChecked(true);
    }

    private void myReward() {
        setCheckedChancel();
        invalidateOptionsMenu();
        toolbarTitle.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Reward");
        setFragment(new MyRewardFragment(), REWARD_FRAGMENT);
        nav.getMenu().getItem(2).setChecked(true);
    }

    private void myAccount() {
        setCheckedChancel();
        invalidateOptionsMenu();
        toolbarTitle.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Account");
        setFragment(new MyAccountFragment(), MY_ACCOUNT_FRAGMENT);
        nav.getMenu().getItem(5).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (CurrentFragment == HOME_FRAGMENT) {
            super.onBackPressed();
        } else {
            if (showCart) {
                MainActivity = null;
                showCart = false;
                finish();
            } else {
                setCheckedChancel();
                invalidateOptionsMenu();
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarTitle.setVisibility(View.VISIBLE);
                nav.getMenu().getItem(0).setChecked(true);
                setFragment(new HomeFragment(), HOME_FRAGMENT);
            }
        }
    }

    private boolean isconnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
