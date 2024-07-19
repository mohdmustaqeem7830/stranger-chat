package mustaqeem.zubair.strangerchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import mustaqeem.zubair.strangerchat.R;
import mustaqeem.zubair.strangerchat.databinding.ActivityConnectingBinding;

public class ConnectingActivity extends AppCompatActivity {
    ActivityConnectingBinding binding ;

    FirebaseDatabase database;
    FirebaseAuth auth;
    boolean isOkay = false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            String profile = getIntent().getStringExtra("profile");

            String username = auth.getUid();
            database.getReference().child("users")
                            .orderByChild("status")
                                    .equalTo(0)
                                            .limitToFirst(1)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.getChildrenCount()>0) {
                                                                isOkay = true;
                                                                //Room Available  if got status 0
                                                                for (DataSnapshot childsnapshot : snapshot.getChildren()){
                                                                    database.getReference()
                                                                            .child("users")
                                                                            .child(childsnapshot.getKey())
                                                                            .child("incoming")
                                                                            .setValue(username);
                                                                    database.getReference()
                                                                            .child("users")
                                                                            .child(childsnapshot.getKey())
                                                                            .child("status")
                                                                            .setValue(1);

                                                                    Intent intent = new Intent(ConnectingActivity.this,CallActivity.class);
                                                                    String incoming = childsnapshot.child("incoming").getValue(String.class);
                                                                    String createdBy = childsnapshot.child("createdBy").getValue(String.class);
                                                                    boolean isAvailable = childsnapshot.child("isAvailable").getValue(boolean.class);
                                                                    intent.putExtra("incoming",incoming);
                                                                    intent.putExtra("createdBy",createdBy);
                                                                    intent.putExtra("isAvailable",isAvailable);
                                                                    intent.putExtra("username",username);

                                                                    startActivity(intent);
                                                                    finish();

                                                                }
                                                            }
                                                            else{

                                                            }
                                                            HashMap<String, Object>  room = new HashMap<>();
                                                            room.put("incoming",username);
                                                            room.put("createdBy",username);
                                                            room.put("isAvailable",true);
                                                            room.put("status",0);

                                                            database.getReference().child("users")
                                                                    .child(username)
                                                                    .setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            database.getReference().child("users")
                                                                                    .child(username).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if(snapshot.child("status").exists()){
                                                                                                if(snapshot.child("status").getValue(Integer.class)==1){
                                                                                                    if(isOkay){
                                                                                                        return;
                                                                                                    }
                                                                                                    isOkay = true;

                                                                                                    Intent intent = new Intent(ConnectingActivity.this,CallActivity.class);
                                                                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                                                                    boolean isAvailable = snapshot.child("isAvailable").getValue(boolean.class);
                                                                                                    intent.putExtra("incoming",incoming);
                                                                                                    intent.putExtra("createdBy",createdBy);
                                                                                                    intent.putExtra("isAvailable",isAvailable);
                                                                                                    intent.putExtra("username",username);

                                                                                                    startActivity(intent);
                                                                                                    finish();


                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });

                                                                        }
                                                                    })  ;
                                                            }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                         //Room Not available if status become 1
                                                            //Now creating the room

                                                        }
                                                    });

            Glide.with(this).load(profile).into(binding.circleImage);


            return insets;
        });
    }
}