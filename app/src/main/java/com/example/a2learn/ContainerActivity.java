package com.example.a2learn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.a2learn.fragments.FragmentAbout;
import com.example.a2learn.fragments.FragmentHome;
import com.example.a2learn.fragments.FragmentMatch;
import com.example.a2learn.fragments.FragmentProficiency;
import com.example.a2learn.fragments.FragmentProfile;
import com.example.a2learn.fragments.FragmentSetting;
import com.example.a2learn.model.Student;
import com.example.a2learn.utility.FireStoreDatabase;
import com.google.android.material.navigation.NavigationView;

public class ContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle anActionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = getIntent().getStringExtra("userId");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        anActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(anActionBarDrawerToggle);
        anActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        anActionBarDrawerToggle.syncState();


        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Student stud = documentSnapshot.toObject(Student.class);
                if (stud != null) {
                    if (!stud.getGiveHelpList().isEmpty() || !stud.getNeedHelpList().isEmpty()) {
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentHome(stud)).commit();
                    } else {
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentProficiency(stud)).commit();
                    }
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentManager.popBackStack();
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Student student = documentSnapshot.toObject(Student.class);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentHome(student)).commit();
                        break;
                    case R.id.nav_profile:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentProfile(student)).commit();
                        break;
                    case R.id.proficiency:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentProficiency(student)).commit();
                        break;
                    case R.id.nav_chat:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentMatch(student)).commit();
                        break;
                    case R.id.setting:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentSetting(student)).commit();
                        break;
                    case R.id.about:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentAbout()).commit();
                        break;

                }
            }
            if (item.getItemId() == R.id.logOut) {
                startActivity(new Intent(this, LoginActivity.class));
                fireStoreDatabase.getFirebaseAuth().signOut();
                finish();
            }


        });
        return false;
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    super.onBackPressed();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}



