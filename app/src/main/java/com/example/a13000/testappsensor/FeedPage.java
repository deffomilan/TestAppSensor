package com.example.a13000.testappsensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FeedPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        int callingActivity = getIntent().getIntExtra("flag", 1);
        if(callingActivity == 0){
            Toast.makeText(this, "Thank you! Your complaint has been posted and will soon be monitored and rectified", Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }
}
