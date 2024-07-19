package mustaqeem.zubair.strangerchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import mustaqeem.zubair.strangerchat.R;
import mustaqeem.zubair.strangerchat.databinding.ActivityWelcomeScreenBinding;

public class WelcomeScreen extends AppCompatActivity {
    ActivityWelcomeScreenBinding binding ;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityWelcomeScreenBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            auth = FirebaseAuth.getInstance();
            if(auth.getCurrentUser()!=null){
                startActivity(new Intent(WelcomeScreen.this,MainActivity.class));
                finish();
            }
            binding.startedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WelcomeScreen.this,LogInScreen.class));
                    finish();
                }
            });
            return insets;
        });
    }
}