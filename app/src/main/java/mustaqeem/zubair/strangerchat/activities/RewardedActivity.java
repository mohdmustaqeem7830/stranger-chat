package mustaqeem.zubair.strangerchat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import mustaqeem.zubair.strangerchat.R;
import mustaqeem.zubair.strangerchat.databinding.ActivityRewardedBinding;

public class RewardedActivity extends AppCompatActivity {
    private RewardedAd rewardedAd;

    ActivityRewardedBinding binding ;

    FirebaseDatabase firebaseDatabase;
    String currentUid;

    int coins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRewardedBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            firebaseDatabase= FirebaseDatabase.getInstance();

            currentUid = FirebaseAuth.getInstance().getUid();

            loadAd();

            firebaseDatabase.getReference().child("profile")
                            .child(currentUid)
                                    .child("coins")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       coins = snapshot.getValue(Integer.class);
                                       binding.coins.setText(String.valueOf(coins));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

            binding.video1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rewardedAd != null) {
                        Activity activityContext = RewardedActivity.this;
                        rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                loadAd();
                                 coins = coins+200;
                                firebaseDatabase.getReference().child("profile")
                                        .child(currentUid).child("coins")
                                        .setValue(coins);

                                binding.video1Icon.setImageResource(R.drawable.check);
                                int rewardAmount = rewardItem.getAmount();
                                String rewardType = rewardItem.getType();
                            }
                        });
                    } else {

                    }
                }
            });



            return insets;
        });
    }

    public void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;

                    }
                });
    }
}