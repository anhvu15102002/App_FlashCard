package com.example.apphoctienganh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class VocabularyActivity extends AppCompatActivity {
    private ListView listView;
    private LinearLayout flashcardLayout;
    private ImageView flashcardImage;
    private TextView flashcardText;
    private Button prevButton, flipButton, nextButton;
    private List<Vocabulary> list;
    private VocabularyAdapter adapter;
    private VocabularySqlite vocabularySqlite;
    private String topic;
    private int currentFlashcardIndex = 0;
    private boolean isFrontShowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        // Khởi tạo các view
        listView = findViewById(R.id.listView);
        flashcardLayout = findViewById(R.id.flashcardLayout);
        flashcardImage = findViewById(R.id.flashcardImage);
        flashcardText = findViewById(R.id.flashcardText);
        prevButton = findViewById(R.id.prevButton);
        flipButton = findViewById(R.id.flipButton);
        nextButton = findViewById(R.id.nextButton);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        topic = intent.getStringExtra("topic");
        toolbar.setTitle("Chủ đề: " + topic);

        vocabularySqlite = new VocabularySqlite(VocabularyActivity.this);
        if (topic != null) {
            loadVocabularies(); // Mặc định hiển thị ListView
        } else {
            Toast.makeText(this, "Topic is null", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện cho các nút flashcard
        setupFlashcardButtons();
    }

    // Tạo menu 3 chấm
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vocabulary_menu, menu);
        return true;
    }

    // Xử lý khi chọn item trong menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(VocabularyActivity.this, TopicActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.actionlist) {
            showListView();
            return true;
        } else if (id == R.id.actionflashcard) {
            showFlashcardView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadVocabularies() {
        list = vocabularySqlite.getVocabulariesByTopic(topic);
        adapter = new VocabularyAdapter(VocabularyActivity.this, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showListView() {
        listView.setVisibility(View.VISIBLE);
        flashcardLayout.setVisibility(View.GONE);
        loadVocabularies();
    }

    private void showFlashcardView() {
        listView.setVisibility(View.GONE);
        flashcardLayout.setVisibility(View.VISIBLE);
        list = vocabularySqlite.getVocabulariesByTopic(topic);
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "No vocabulary available", Toast.LENGTH_SHORT).show();
            return;
        }
        currentFlashcardIndex = 0;
        isFrontShowing = true;

        // Reset both views
        flashcardImage.setVisibility(View.VISIBLE);
        flashcardText.setVisibility(View.GONE);
        flashcardImage.setRotationY(0f);
        flashcardText.setRotationY(0f);

        // Load initial image
        Glide.with(this)
                .load(list.get(currentFlashcardIndex).getImage())
                .into(flashcardImage);



    }

    private void setupFlashcardButtons() {
        prevButton.setOnClickListener(v -> {
            if (currentFlashcardIndex > 0) {
                currentFlashcardIndex--;
                isFrontShowing = true;
                updateFlashcard(false); // Không cần animation khi chuyển từ
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentFlashcardIndex < list.size() - 1) {
                currentFlashcardIndex++;
                isFrontShowing = true;
                updateFlashcard(false); // Không cần animation khi chuyển từ
            }
        });

        flipButton.setOnClickListener(v -> {
            isFrontShowing = !isFrontShowing; // Toggle trạng thái
            updateFlashcard(true); // Luôn dùng animation khi lật thẻ
        });
    }

    private void updateFlashcard(boolean withAnimation) {
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "No vocabulary available", Toast.LENGTH_SHORT).show();
            return;
        }

        Vocabulary currentVocab = list.get(currentFlashcardIndex);
        System.out.println("updateFlashcard called: isFrontShowing = " + isFrontShowing);

        if (withAnimation) {
            if (!isFrontShowing) { // Nếu đang hiển thị mặt trước (hình ảnh) và cần lật sang mặt sau (văn bản)
                // Lật từ hình ảnh sang chữ
                ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(flashcardImage, "rotationY", 0f, 90f);
                flipAnimator.setDuration(250);

                flipAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flashcardImage.setVisibility(View.GONE);
                        flashcardImage.setRotationY(0f); // Reset rotation
                        flashcardText.setVisibility(View.VISIBLE);
                        flashcardText.setRotationY(-90f); // Chuẩn bị chữ từ -90 độ
                        flashcardText.setText(currentVocab.getWords() + "\n" + currentVocab.getAnswer());

                        ObjectAnimator textAnimator = ObjectAnimator.ofFloat(flashcardText, "rotationY", -90f, 0f);
                        textAnimator.setDuration(250);
                        textAnimator.start();
                    }
                });
                flipAnimator.start();
            } else { // Nếu đang hiển thị mặt sau (văn bản) và cần lật sang mặt trước (hình ảnh)
                // Lật từ chữ sang hình
                ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(flashcardText, "rotationY", 0f, 90f);
                flipAnimator.setDuration(250);

                flipAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flashcardText.setVisibility(View.GONE);
                        flashcardText.setRotationY(0f); // Reset rotation
                        flashcardImage.setVisibility(View.VISIBLE);
                        flashcardImage.setRotationY(-90f); // Chuẩn bị hình từ -90 độ

                        Glide.with(VocabularyActivity.this)
                                .load(currentVocab.getImage())
                                .into(flashcardImage);
                        ObjectAnimator imageAnimator = ObjectAnimator.ofFloat(flashcardImage, "rotationY", -90f, 0f);
                        imageAnimator.setDuration(250);
                        imageAnimator.start();
                    }
                });
                flipAnimator.start();
            }
        } else {
            // Không animation, chỉ cập nhật nội dung
            if (isFrontShowing) {
                flashcardImage.setVisibility(View.VISIBLE);
                flashcardText.setVisibility(View.GONE);
                Glide.with(this)
                        .load(currentVocab.getImage())
                        .into(flashcardImage);
            } else {
                flashcardImage.setVisibility(View.GONE);
                flashcardText.setVisibility(View.VISIBLE);
                flashcardText.setText(currentVocab.getWords() + "\n" + currentVocab.getAnswer());
            }
            flashcardImage.setRotationY(0f);
            flashcardText.setRotationY(0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (topic != null) {
            if (listView.getVisibility() == View.VISIBLE) {
                loadVocabularies();
            } else {
                updateFlashcard(false);
            }
        }
    }
}