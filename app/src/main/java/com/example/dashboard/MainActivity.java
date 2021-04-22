package com.example.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.dashboard.data.Preferences;
import com.example.dashboard.fragment.AddressBookFragment;
import com.example.dashboard.fragment.CartFragment;
import com.example.dashboard.fragment.CategoriesFragment;
import com.example.dashboard.fragment.HomeFragment;
import com.example.dashboard.fragment.SearchFragment;
import com.example.dashboard.fragment.YourOrderFragment;
import com.example.dashboard.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    View headerLayout;

    TextView userNameText, userEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.Nav_drawer_open, R.string.Nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Preferences preferences = Preferences.getPreferences(getApplicationContext());
        User currentUser = preferences.getCurrentUser();

        headerLayout = navigationView.getHeaderView(0);
        userNameText = headerLayout.findViewById(R.id.name_user);
        userEmailText = headerLayout.findViewById(R.id.email_user);
//        userNameText = findViewById(R.id.name_user);
//        userEmailText = findViewById(R.id.email_user);
        userNameText.setText(currentUser.name);
        userEmailText.setText(currentUser.email);

        Button contact_us = findViewById(R.id.contact_us);
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Opening default Email client", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("f20180605@hyderabad.bits-pilani.ac.in") +
                        "?subject=" + Uri.encode("") +
                        "&body=" + Uri.encode("");
                Uri uri = Uri.parse(uriText);

                intent.setData(uri);
                try {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }catch(android.content.ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this,
                            "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_categories:
                    selectedFragment = new CategoriesFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.nav_cart:
                    selectedFragment = new CartFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_your_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new YourOrderFragment()).commit();
                break;
            case R.id.nav_address_book:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddressBookFragment()).commit();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}