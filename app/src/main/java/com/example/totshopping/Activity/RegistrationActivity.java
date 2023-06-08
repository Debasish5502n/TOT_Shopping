package com.example.totshopping.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.example.totshopping.Fregment.SigniinFregment;
import com.example.totshopping.Fregment.SignupFragment;
import com.example.totshopping.R;

public class RegistrationActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    public static boolean onresetPassword=false;
    public static boolean signUpFragment=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        frameLayout=findViewById(R.id.frameLayout);

        if (signUpFragment){
            signUpFragment =false;
            setFragment(new SignupFragment());
        }else {
            setDefaultFregment(new SigniinFregment());
        }
    }

    private void setDefaultFregment(SigniinFregment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            SigniinFregment.disableCloseBtn =false;
            SignupFragment.disableCloseBtn =false;
            if (onresetPassword) {
                onresetPassword =false;
                setDefaultFregment(new SigniinFregment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}