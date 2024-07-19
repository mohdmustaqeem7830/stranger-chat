package mustaqeem.zubair.strangerchat.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mustaqeem.zubair.strangerchat.R;
import mustaqeem.zubair.strangerchat.databinding.ActivityMainBinding;
import mustaqeem.zubair.strangerchat.models.Users;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    Users users;
    FirebaseDatabase database;
    long coins = 0;
    int PERMISSION_REQUEST_CODE = 25;
    ProgressBar progressBar;

    String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            new Thread(
                    () -> {
                        // Initialize the Google Mobile Ads SDK on a background thread.
                        MobileAds.initialize(this, initializationStatus -> {});
                    })
                    .start();



            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();


            database.getReference().child("profile").child(currentUser.getUid()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            users = snapshot.getValue(Users.class);
                            coins = users.getCoins();
                            binding.coins.setText("You have : "+coins);
                            Glide.with(MainActivity.this).load(users.getProfile()).into(binding.profilePicture);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }
            );
            //find button working

            binding.findBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isPermissionGranted()) {
                        if (coins > 5) {
                            coins = coins-5;
                            database.getReference().child("profile")
                                    .child(currentUser.getUid())
                                    .child("coins")
                                    .setValue(coins);
                            Intent intent = new Intent(MainActivity.this,ConnectingActivity.class);
                            intent.putExtra("profile",users.getProfile());

                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You have not suffiecient coin", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        askPermissions();
                    }
                }
            });


            binding.rewardCoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,RewardedActivity.class));
                }
            });






            return insets;
        });
    }



    public void askPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this,permissions,PERMISSION_REQUEST_CODE);
    }
    private boolean isPermissionGranted(){
        for (String permission : permissions){

            if(ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
                return false;
            }

        }
        return true;
    }
}