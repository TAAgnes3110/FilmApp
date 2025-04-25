package com.example.moviesapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.moviesapp.Fragment.CinemaFragment;
import com.example.moviesapp.Fragment.FavouriteFragment;
import com.example.moviesapp.Fragment.HomeFragment;
import com.example.moviesapp.Fragment.MovieSearchResultsFragment;
import com.example.moviesapp.Fragment.ProfileFragment;
import com.example.moviesapp.R;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private FavouriteFragment favouriteFragment;
    private CinemaFragment cinemaFragment;
    private ProfileFragment profileFragment;
    private MovieSearchResultsFragment movieSearchResultsFragment;

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
    }

    public void switchFragment(Fragment showFragment) {
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
        if (movieSearchResultsFragment.isAdded()) {
            transaction.hide(movieSearchResultsFragment);
        }

        if (!showFragment.isAdded()) {
            // Dùng lại tag như ban đầu trong initView()
            String tag = getFragmentTag(showFragment);
            transaction.add(R.id.fragment_main, showFragment, tag);
            Log.d("MainActivity", "Adding fragment: " + tag);
        }

        transaction.show(showFragment);
        transaction.commit();
    }

    private String getFragmentTag(Fragment fragment) {
        if (fragment instanceof HomeFragment) return "Home";
        if (fragment instanceof FavouriteFragment) return "Favourite";
        if (fragment instanceof CinemaFragment) return "Cinema";
        if (fragment instanceof ProfileFragment) return "Profile";
        if (fragment instanceof MovieSearchResultsFragment) return "Search";
        return fragment.getClass().getSimpleName();
    }

    private void onClick() {
        imageViewHome.setOnClickListener(view -> {
            if (!homeFragment.isVisible()) {
                switchFragment(homeFragment);
            }
        });

        imageViewFavourite.setOnClickListener(view -> {
            if (!favouriteFragment.isVisible()) {
                switchFragment(favouriteFragment);
            }
        });

        imageViewCinema.setOnClickListener(view -> {
            if (!cinemaFragment.isVisible()) {
                switchFragment(cinemaFragment);
            }
        });

        imageViewProfile.setOnClickListener(view -> {
            if (!profileFragment.isVisible()) {
                switchFragment(profileFragment);
            }
        });
    }

    private void initView(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        favouriteFragment = new FavouriteFragment();
        cinemaFragment = new CinemaFragment();
        profileFragment = new ProfileFragment();
        movieSearchResultsFragment = new MovieSearchResultsFragment();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_main, homeFragment, "Home");
            transaction.add(R.id.fragment_main, favouriteFragment, "Favourite");
            transaction.add(R.id.fragment_main, cinemaFragment, "Cinema");
            transaction.add(R.id.fragment_main, profileFragment, "Profile");
            transaction.add(R.id.fragment_main, movieSearchResultsFragment, "Search");
            transaction.hide(favouriteFragment);
            transaction.hide(cinemaFragment);
            transaction.hide(profileFragment);
            transaction.hide(movieSearchResultsFragment);
            transaction.commit();
        }

        imageViewHome = findViewById(R.id.imageView_Home);
        imageViewFavourite = findViewById(R.id.imageView_Favourite);
        imageViewCinema = findViewById(R.id.imageView_Cinema);
        imageViewProfile = findViewById(R.id.imageView_Profile);
    }
}