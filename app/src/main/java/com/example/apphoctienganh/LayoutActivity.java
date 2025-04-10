package com.example.apphoctienganh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LayoutActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView username;
    private ImageView questionQuick;
    private ImageView leaderBoard;
    private ImageView vocabulary;
    private ImageView gmamar;
    private ImageView listening;
    private ImageView timer;
    private ImageView drap;

    private ImageView dictionarySearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        mAuth = FirebaseAuth.getInstance();
        mapping();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            username.setText(email);
        }
        clickToImage();
    }
    public void clickToImage(){
        questionQuick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LayoutActivity.this, MultipleChoiceActivity.class);
                startActivity(intent);
                finish();
            }
        });
        leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LayoutActivity.this, LeaderBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        vocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LayoutActivity.this, TopicActivity.class);
                startActivity(intent);
                finish();
            }
        });
        gmamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LayoutActivity.this, HocNguPhapAcitivty.class);
                startActivity(intent);
                finish();
            }
        });
        listening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LayoutActivity.this, ListeningActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Thêm sự kiện cho phần tra cứu từ vựng
        dictionarySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LayoutActivity.this, DictionarySearchActivity.class); // Thay đổi tên Activity nếu cần
                startActivity(intent);
                finish();
            }
        });
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LayoutActivity.this, StudyReminderActivity.class);
                startActivity(intent);
                finish();
            }
        });
        drap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LayoutActivity.this, DrapDropActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void mapping(){
        username = findViewById(R.id.username);
        questionQuick = findViewById(R.id.questionQuick);
        leaderBoard = findViewById(R.id.leaderBoard);
        vocabulary = findViewById(R.id.image_vocabulary);
        gmamar = findViewById(R.id.grmaar);
        listening = findViewById(R.id.listening);
        dictionarySearch = findViewById(R.id.searchWord);
        timer = findViewById(R.id.timer);
        drap = findViewById(R.id.drap);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(LayoutActivity.this,LoginActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}