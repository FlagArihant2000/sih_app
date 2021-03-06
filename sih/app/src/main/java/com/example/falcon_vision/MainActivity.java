package com.example.falcon_vision;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static int DEKHO ;

    TextView name, title, email;
    String userID;
    ImageView img, notif, scanner;

    private DrawerLayout drawer;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    SharedPreferences sp;

    Map<String,Object> user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        user = (Map<String, Object>) getIntent().getSerializableExtra("key");

        userID = firebaseAuth.getCurrentUser().getUid();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        name   = (TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_name);
        email = navigationView.getHeaderView(0).findViewById(R.id.drawer_email);
        img = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_img);

        scanner =findViewById(R.id.scanner_today);
        notif = findViewById(R.id.notif_shortcut);
        title = findViewById(R.id.title_top);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.dashboard);
        }


        final DocumentReference documentReference = fstore.collection("reg_users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.d("Errororooror","Error:"+e.getMessage());
                }
                else {
                    name.setText(documentSnapshot.getString("name"));
                    email.setText(documentSnapshot.getString("email"));
                }

            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShowProFragment()).commit();

                title.setText("Profile");
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder notif = new AlertDialog.Builder(MainActivity.this);
                String l1 = "ALERT MESSAGE";
                String l2 = "Your vehicle had passed through :";

                notif.setMessage(l1 + "\n" + "\n" + l2 );

                notif.setNegativeButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // call police
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "100"));
                        startActivity(intent);
                    }
                });

                notif.setPositiveButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ignore

                    }
                });

                notif.create().show();
            }
        });

        scanner.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Scanner.class));
            }
        });

        StorageReference profileRef = FirebaseStorage.getInstance().getReference("profilepics/"+ userID + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(img);
            }
        });





    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DashboardFragment()).commit();
                title.setText("Dashboard");
                break;
            case R.id.payments:
                startActivity(new Intent(MainActivity.this, PaymentsActivity.class));
                break;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                title.setText("Settings");

                break;

            case R.id.notice:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NoticeFragment()).commit();
                title.setText("Notices");

                break;
            case R.id.logout:

//                setTextColorForMenuItem(item, R.color.blue);

                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Are you sure?");

                alert.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                });

                alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                alert.create().show();

                break;



        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


//    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
//        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
//        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
//        menuItem.setTitle(spanString);
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("Do you want to exit?");

            alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
            });

            alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // close the dialog
                }
            });

            alert.create().show();

        }

    }
}
