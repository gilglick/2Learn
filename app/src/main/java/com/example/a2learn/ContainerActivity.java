package com.example.a2learn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "";
    FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle anActionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private String userId;
    private FragmentMatch fragmentMatch = new FragmentMatch();
    private FragmentProfile fragmentProfile = new FragmentProfile();

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
        Toast.makeText(this, "on create", Toast.LENGTH_SHORT).show();

        firebaseFirestore.collection(FireStoreDatabase.STUDENT_STORAGE).document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Student stud = documentSnapshot.toObject(Student.class);
                fragmentTransaction.replace(R.id.container_fragment,new FragmentProfile(stud));
                fragmentTransaction.commit();
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this, "onNavigation active", Toast.LENGTH_SHORT).show();
        fragmentManager.popBackStack();
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Student student = documentSnapshot.toObject(Student.class);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentHome()).commit();
                        break;
                    case R.id.nav_profile:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentProfile(student)).commit();
                        break;
                    case R.id.proficiency:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentProficiency(student)).commit();
                        break;
                    case R.id.nav_chat:
                        fragmentTransaction.replace(R.id.container_fragment, new FragmentMatch()).commit();
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

}



