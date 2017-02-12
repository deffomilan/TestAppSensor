package com.example.a13000.testappsensor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleFeed extends AppCompatActivity {

    String key_id = null;

    private ImageView imageView;
    private TextView title,desc,by;
    private Button remove_button;

    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_feed);

        imageView = (ImageView) findViewById(R.id.imageView);
        title = (TextView) findViewById(R.id.title_place);
        desc = (TextView) findViewById(R.id.description_place);
        by = (TextView) findViewById(R.id.username);
        remove_button = (Button) findViewById(R.id.remove_post);

        key_id = getIntent().getStringExtra("key_id");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("ComplaintHead");
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef.keepSynced(true);

        databaseRef.child(key_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title_value = (String) dataSnapshot.child("title").getValue();
                String description_value = (String) dataSnapshot.child("desc").getValue();
                String username_value = (String) dataSnapshot.child("username").getValue();
                String image_url = (String)dataSnapshot.child("image").getValue();
                String user_id = (String) dataSnapshot.child("UID").getValue();

                title.setText(title_value);
                desc.setText(description_value);
                by.setText(username_value);

                Picasso.with(SingleFeed.this).load(image_url).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Making button visible for user that is logged in ...
        if(firebaseAuth.getCurrentUser().getUid().equals(key_id)){
            remove_button.setVisibility(View.VISIBLE);
        }

        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
