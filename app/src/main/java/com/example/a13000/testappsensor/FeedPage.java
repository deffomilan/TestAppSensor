package com.example.a13000.testappsensor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FeedPage extends AppCompatActivity {

    private RecyclerView listComplaints;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        listComplaints = (RecyclerView) findViewById(R.id.listComplaints);
        listComplaints.setHasFixedSize(true);
        listComplaints.setLayoutManager(new LinearLayoutManager(this));
        PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ComplaintHead");

        int callingActivity = getIntent().getIntExtra("flag", 1);
        if (callingActivity == 0) {
            Toast.makeText(this, "Thank you! Your complaint has been posted and will soon be monitored and rectified", Toast.LENGTH_LONG).show();
        }

        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();
            }
        });
        layout.setRefreshing(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Complaints, ComplaintsHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Complaints, ComplaintsHolder>(
                        Complaints.class,
                        R.layout.list_card_view,
                        ComplaintsHolder.class,
                        databaseReference
        ){
            @Override
            protected void populateViewHolder(ComplaintsHolder viewHolder, Complaints model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
            }
        };
        listComplaints.setAdapter(firebaseRecyclerAdapter);
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
        return super.onOptionsItemSelected(item);
    }


    public static class ComplaintsHolder extends RecyclerView.ViewHolder {
        View view;

        public ComplaintsHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setTitle(String title) {
            TextView feedTitleView = (TextView) view.findViewById(R.id.feedTitleView);
            feedTitleView.setText(title);
        }

        public void setDesc(String desc) {
            TextView feedDescView = (TextView) view.findViewById(R.id.feedDescView);
            feedDescView.setText(desc);
        }

        public void setImage(Context context,String image){
            ImageView feedImageView = (ImageView) view.findViewById(R.id.feedImageView);
            Picasso.with(context).load(image).into(feedImageView);
        }
    }
}
