package mustaqeem.zubair.strangerchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import mustaqeem.zubair.strangerchat.R;
import mustaqeem.zubair.strangerchat.databinding.ActivityLogInScreenBinding;
import mustaqeem.zubair.strangerchat.models.Users;

public class LogInScreen extends AppCompatActivity {
    ActivityLogInScreenBinding binding;
    GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth mAuth ;

    int RC_SIGN_IN = 11;


    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityLogInScreenBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


            mAuth = FirebaseAuth.getInstance();
            database =FirebaseDatabase.getInstance();


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

            binding.signBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent,RC_SIGN_IN);
                }
            });

            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult();
            authWithGoogle(account.getIdToken());


        }
    }

    void authWithGoogle(String idToken){

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = mAuth.getCurrentUser();
                Users firebaseuser = new Users(user.getUid(),user.getDisplayName(),user.getPhotoUrl().toString(),"-",500);

                database.getReference().child("profile").child(user.getUid()).setValue(firebaseuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if ((task.isSuccessful())){
                            startActivity(new Intent(LogInScreen.this,MainActivity.class));
                            finishAffinity();
                        }else{
                            Toast.makeText(LogInScreen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                Toast.makeText(LogInScreen.this, user.getEmail().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}