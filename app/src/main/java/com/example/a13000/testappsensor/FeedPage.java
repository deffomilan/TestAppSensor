package com.example.a13000.testappsensor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FeedPage extends AppCompatActivity {

    private RecyclerView listComplaints;
    private DatabaseReference databaseReference, databaseReferenceForUsers, databaseReferenceLikes;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private boolean like_val = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        listComplaints = (RecyclerView) findViewById(R.id.listComplaints);
        listComplaints.setHasFixedSize(true);
        listComplaints.setLayoutManager(new LinearLayoutManager(this));
        PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // Checks if the user is present in the database or not ...
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent in = new Intent(FeedPage.this, MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    finish();
                }
            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ComplaintHead");
        //birrsyo yessle k garrxa vanera ...
        //Figure out later ...
        databaseReferenceForUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Likes");

        // Firebase offline capabilities ...
        databaseReferenceForUsers.keepSynced(true);
        databaseReference.keepSynced(true);
        databaseReferenceLikes.keepSynced(true);

        int callingActivity = getIntent().getIntExtra("flag", 1);
        if (callingActivity == 0) {
            Toast.makeText(this, "Thank you! Your complaint has been posted and will soon be monitored and rectified", Toast.LENGTH_LONG).show();
        }

        // Swipe down to refresh hunney part ...
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Think for this some time later ...
            }
        });
        layout.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);

        // Inflating recycler view ... hard to explain ... maile bujhhya xaina :D
        FirebaseRecyclerAdapter<Complaints, ComplaintsHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Complaints, ComplaintsHolder>(
                        Complaints.class,
                        R.layout.list_card_view,
                        ComplaintsHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(ComplaintsHolder viewHolder, Complaints model, int position) {
                        final String key_id = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setLike_button(key_id);
                        viewHolder.setImage(getApplicationContext(), model.getImage());

                        // Like button onClickListener ...
                        viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                like_val = true;
                                databaseReferenceLikes.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (like_val) {
                                            if (dataSnapshot.child(key_id).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                                databaseReferenceLikes.child(key_id)
                                                        .child(firebaseAuth.getCurrentUser().getUid())
                                                        .removeValue();
                                                like_val = false;
                                            } else {
                                                databaseReferenceLikes.child(key_id)
                                                        .child(firebaseAuth.getCurrentUser().getUid())
                                                        .setValue("Username here");
                                                like_val = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(FeedPage.this, "Error liking the post", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent in = new Intent(FeedPage.this,SingleFeed.class);
                                in.putExtra("key_id",key_id);
                                startActivity(in);
                            }
                        });
                    }
                };
        listComplaints.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ComplaintsHolder extends RecyclerView.ViewHolder {
        View view;
        ImageButton like_button;
        ImageButton comment_button;


        private FirebaseAuth auth_like;
        private DatabaseReference db_ref_like;

        public ComplaintsHolder(View itemView) {
            super(itemView);
            view = itemView;
            like_button = (ImageButton) view.findViewById(R.id.likeButton);
            db_ref_like = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth_like = FirebaseAuth.getInstance();
            db_ref_like.keepSynced(true);
        }

        public void setTitle(String title) {
            TextView feedTitleView = (TextView) view.findViewById(R.id.feedTitleView);
            feedTitleView.setText(title);
        }

        public void setDesc(String desc) {
            TextView feedDescView = (TextView) view.findViewById(R.id.feedDescView);
            feedDescView.setText(desc);
        }

        public void setUsername(String username){
            TextView feedUsername = (TextView) view.findViewById(R.id.username);
            feedUsername.setText(username);
        }

        public void setImage(Context context, String image) {
            ImageView feedImageView = (ImageView) view.findViewById(R.id.feedImageView);
            Picasso.with(context).load(image).into(feedImageView);
        }

        public void setLike_button(final String key_id) {
            db_ref_like.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key_id).hasChild(auth_like.getCurrentUser().getUid())) {
                        like_button.setImageResource(R.mipmap.ic_thumb_down_black_24dp);
                    } else {
                        like_button.setImageResource(R.mipmap.ic_thumb_up_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setTitle("Complaints");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addPost) {
            startActivity(new Intent(FeedPage.this, PostDo.class));
        }
        else if (item.getItemId() == R.id.logOut){
            firebaseAuth.signOut();
            Intent in = new Intent(FeedPage.this,MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }

}


