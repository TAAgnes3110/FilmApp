package com.example.moviesapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviesapp.presentation.fragment.CinemaFragment;
import com.example.moviesapp.presentation.fragment.FavouriteFragment;
import com.example.moviesapp.presentation.fragment.HomeFragment;
import com.example.moviesapp.presentation.fragment.ProfileFragment;
import com.example.moviesapp.R;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private FavouriteFragment favouriteFragment;
    private CinemaFragment cinemaFragment;
    private ProfileFragment profileFragment;

    private ImageView imageViewHome, imageViewFavourite, imageViewCinema, imageViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView(savedInstanceState);
        onClick();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        int userId = intent.getIntExtra("USER_ID",-1);


    }

    private void switchFragment(Fragment showFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (homeFragment.isAdded()) {
            transaction.hide(homeFragment);
        }
        if (favouriteFragment.isAdded()) {
            transaction.hide(favouriteFragment);
        }
        if (cinemaFragment.isAdded()) {
            transaction.hide(cinemaFragment);
        }
        if (profileFragment.isAdded()) {
            transaction.hide(profileFragment);
        }

        // Chỉ thêm nếu Fragment chưa tồn tại
        if (!showFragment.isAdded()) {
            transaction.add(R.id.fragment_main, showFragment);
        }

        // Hiển thị Fragment mong muốn
        transaction.show(showFragment);
        transaction.commitAllowingStateLoss();
    }



    private void onClick() {
        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!homeFragment.isVisible()) {
                    switchFragment(homeFragment);
                }
            }
        });

        imageViewFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!favouriteFragment.isVisible()) {
                    switchFragment(favouriteFragment);
                }
            }
        });

        imageViewCinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cinemaFragment.isVisible()) {
                    switchFragment(cinemaFragment);
                }
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!profileFragment.isVisible()) {
                    switchFragment(profileFragment);
                }
            }
        });
    }

    private void initView(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        favouriteFragment = new FavouriteFragment();
        cinemaFragment = new CinemaFragment();
        profileFragment = new ProfileFragment();
        // Thêm Fragment vào FragmentManager
        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_main, homeFragment, "Home");
            transaction.add(R.id.fragment_main, favouriteFragment, "Favourite");
            transaction.add(R.id.fragment_main, cinemaFragment, "Cinema");
            transaction.add(R.id.fragment_main, profileFragment, "Profile");
            transaction.hide(favouriteFragment);
            transaction.hide(cinemaFragment);
            transaction.hide(profileFragment);
            transaction.commit();
        }

        imageViewHome = findViewById(R.id.imageView_Home);
        imageViewFavourite = findViewById(R.id.imageView_Favourite);
        imageViewCinema = findViewById(R.id.imageView_Cinema);
        imageViewProfile = findViewById(R.id.imageView_Profile);
    }
}