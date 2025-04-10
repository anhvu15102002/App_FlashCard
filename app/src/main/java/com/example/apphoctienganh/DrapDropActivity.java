package com.example.apphoctienganh;
import android.content.Intent;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrapDropActivity extends AppCompatActivity {
    private static final String TAG = "DrapDropActivity";
    private List<VocabularyItem> vocabularyItems = new ArrayList<>();
    private NewVocabularyAdapter adapter;

    // Game components
    private LinearLayout gameContainer;
    private LinearLayout wordContainer;
    private LinearLayout imageContainer;
    private View vocabularyManagementContainer;

    // Add vocabulary components
    private EditText etEnglishWord;
    private EditText etVietnameseMeaning;
    private EditText etImageUrl;
    private Button btnAdd;
    private RecyclerView rvVocabularyList;
    private Button btnStartGame;

    private VocabularyDbHelper dbHelper;

    private ImageButton btnBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_drapdrop);

            // Khởi tạo database helper
            dbHelper = new VocabularyDbHelper(this);

            initViews();
            setupListeners();
            setupRecyclerView();

            // Tải dữ liệu từ database thay vì addSampleData()
            loadVocabularyFromDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo ứng dụng", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if initialization fails
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(DrapDropActivity.this, LayoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    private void loadVocabularyFromDatabase() {
        try {
            // Xóa danh sách hiện tại
            vocabularyItems.clear();

            // Lấy dữ liệu từ database
            List<VocabularyItem> items = dbHelper.getAllVocabulary();

            // Thêm vào danh sách hiện tại
            vocabularyItems.addAll(items);

            // Kiểm tra nếu không có dữ liệu thì thêm dữ liệu mẫu
            if (vocabularyItems.isEmpty()) {
                addSampleDataToDatabase();
                vocabularyItems.addAll(dbHelper.getAllVocabulary());
            }

            // Cập nhật adapter
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading vocabulary from database: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi tải dữ liệu từ vựng", Toast.LENGTH_SHORT).show();
        }
    }
    private void addSampleDataToDatabase() {
        try {
            VocabularyItem dog = new VocabularyItem("dog", "con chó", "https://images.pexels.com/photos/1805164/pexels-photo-1805164.jpeg");
            VocabularyItem banana = new VocabularyItem("banana", "quả chuối", "https://images.pexels.com/photos/5966630/pexels-photo-5966630.jpeg");
            VocabularyItem car = new VocabularyItem("car", "xe hơi", "https://images.pexels.com/photos/170811/pexels-photo-170811.jpeg");

            dbHelper.addVocabulary(dog);
            dbHelper.addVocabulary(banana);
            dbHelper.addVocabulary(car);

            Log.d(TAG, "Sample data added to database");
        } catch (Exception e) {
            Log.e(TAG, "Error adding sample data to database: " + e.getMessage(), e);
        }
    }

    private void initViews() {
        try {
            // Game views
            gameContainer = findViewById(R.id.gameContainer);
            wordContainer = findViewById(R.id.wordContainer);
            imageContainer = findViewById(R.id.imageContainer);
            vocabularyManagementContainer = findViewById(R.id.vocabularyManagementContainer);

            // Vocabulary management views
            etEnglishWord = findViewById(R.id.etEnglishWord);
            etVietnameseMeaning = findViewById(R.id.etVietnameseMeaning);
            etImageUrl = findViewById(R.id.etImageUrl);
            btnAdd = findViewById(R.id.btnAdd);
            rvVocabularyList = findViewById(R.id.rvVocabularyList);
            btnStartGame = findViewById(R.id.btnStartGame);
            btnBack= findViewById(R.id.btnBack);

            // Validate critical views
            if (gameContainer == null || wordContainer == null || imageContainer == null ||
                    vocabularyManagementContainer == null) {
                throw new IllegalStateException("Critical layout components are missing");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage(), e);
            throw e; // Rethrow to be caught in onCreate
        }
    }

    private void setupRecyclerView() {
        try {
            if (rvVocabularyList == null) {
                Log.e(TAG, "RecyclerView not found");
                return;
            }

            adapter = new NewVocabularyAdapter(vocabularyItems, dbHelper);
            rvVocabularyList.setLayoutManager(new LinearLayoutManager(this));
            rvVocabularyList.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error in setupRecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupListeners() {
        try {
            if (btnAdd != null) {
                btnAdd.setOnClickListener(v -> addVocabularyItem());
            }

            if (btnStartGame != null) {
                btnStartGame.setOnClickListener(v -> startGame());
            }

            // Add OnClickListener for btnBack
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    Intent intent = new Intent(DrapDropActivity.this, LayoutActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupListeners: " + e.getMessage(), e);
        }
    }

    private void addSampleData() {
        try {
            vocabularyItems.add(new VocabularyItem("dog", "con chó", "https://images.pexels.com/photos/1805164/pexels-photo-1805164.jpeg"));
            vocabularyItems.add(new VocabularyItem("banana", "quả chuối", "https://images.pexels.com/photos/5966630/pexels-photo-5966630.jpeg"));
            vocabularyItems.add(new VocabularyItem("car", "xe hơi", "https://images.pexels.com/photos/170811/pexels-photo-170811.jpeg"));

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in addSampleData: " + e.getMessage(), e);
        }
    }

    private void addVocabularyItem() {
        try {
            if (etEnglishWord == null || etVietnameseMeaning == null || etImageUrl == null) {
                Log.e(TAG, "Input fields are null");
                return;
            }

            String englishWord = etEnglishWord.getText().toString().trim();
            String vietnameseMeaning = etVietnameseMeaning.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (englishWord.isEmpty() || vietnameseMeaning.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Basic URL validation
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                Toast.makeText(this, "URL hình ảnh phải bắt đầu bằng http:// hoặc https://", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem từ vựng đã tồn tại chưa
            if (dbHelper.isVocabularyExists(englishWord)) {
                Toast.makeText(this, "Từ vựng này đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng từ vựng mới
            VocabularyItem newItem = new VocabularyItem(englishWord, vietnameseMeaning, imageUrl);

            // Thêm vào database
            long id = dbHelper.addVocabulary(newItem);

            if (id != -1) {
                // Thêm vào danh sách hiện tại
                vocabularyItems.add(newItem);

                if (adapter != null) {
                    adapter.notifyItemInserted(vocabularyItems.size() - 1);

                    if (rvVocabularyList != null) {
                        rvVocabularyList.scrollToPosition(vocabularyItems.size() - 1);
                    }
                }

                // Clear input fields
                etEnglishWord.setText("");
                etVietnameseMeaning.setText("");
                etImageUrl.setText("");

                Toast.makeText(this, "Đã thêm từ vựng mới", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm từ vựng vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in addVocabularyItem: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi thêm từ vựng", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame() {
        try {
            if (vocabularyItems.size() < 3) {
                Toast.makeText(this, "Cần ít nhất 3 từ vựng để bắt đầu trò chơi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hide vocabulary management and show game area
            if (vocabularyManagementContainer != null) {
                vocabularyManagementContainer.setVisibility(View.GONE);
            }

            if (gameContainer != null) {
                gameContainer.setVisibility(View.VISIBLE);
            }

            // Select random words for the game (max 3)
            List<VocabularyItem> gameItems = new ArrayList<>(vocabularyItems);
            Collections.shuffle(gameItems);
            int itemCount = Math.min(3, gameItems.size());
            List<VocabularyItem> selectedItems = gameItems.subList(0, itemCount);

            Log.d(TAG, "Starting game with " + itemCount + " items");
            setupGameUI(selectedItems);
        } catch (Exception e) {
            Log.e(TAG, "Error in startGame: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi bắt đầu trò chơi", Toast.LENGTH_SHORT).show();

            // Safely return to vocabulary management
            if (gameContainer != null) {
                gameContainer.setVisibility(View.GONE);
            }

            if (vocabularyManagementContainer != null) {
                vocabularyManagementContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupGameUI(List<VocabularyItem> items) {
        try {
            if (imageContainer == null || wordContainer == null) {
                Log.e(TAG, "Game containers are null");
                return;
            }

            // Clear old game data
            imageContainer.removeAllViews();
            wordContainer.removeAllViews();

            // Create randomized word list for dragging
            List<VocabularyItem> shuffledWords = new ArrayList<>(items);
            Collections.shuffle(shuffledWords);

            // Create image views (drop targets)
            LayoutInflater inflater = LayoutInflater.from(this);
            for (VocabularyItem item : items) {
                View imageViewLayout = inflater.inflate(R.layout.item_target_image, imageContainer, false);
                if (imageViewLayout == null) {
                    Log.e(TAG, "Failed to inflate item_target_image layout");
                    continue;
                }

                ImageView iv = imageViewLayout.findViewById(R.id.targetImage);
                TextView targetLabel = imageViewLayout.findViewById(R.id.targetLabel);

                if (iv == null || targetLabel == null) {
                    Log.e(TAG, "Target image or label view not found in layout");
                    continue;
                }

                // Load image from URL using Glide with error handling
                Glide.with(getApplicationContext())
                        .load(item.getImageUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Failed to load image: " + item.getImageUrl(), e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(iv);

                // Save word info (used as tag for checking when dropping)
                targetLabel.setText(item.getEnglishWord());
                targetLabel.setTag(item.getEnglishWord());

                imageContainer.addView(imageViewLayout);
            }

            // Create word views (draggable words)
            for (VocabularyItem item : shuffledWords) {
                View wordView = inflater.inflate(R.layout.item_draggable_word, wordContainer, false);
                if (wordView == null) {
                    Log.e(TAG, "Failed to inflate item_draggable_word layout");
                    continue;
                }

                TextView tv = wordView.findViewById(R.id.tvWord);
                if (tv == null) {
                    Log.e(TAG, "Word TextView not found in layout");
                    continue;
                }

                tv.setText(item.getEnglishWord());
                tv.setTag(item.getEnglishWord());

                // Initialize drag when long-pressed
                tv.setOnLongClickListener(v -> {
                    try {
                        ClipData data = ClipData.newPlainText("word", item.getEnglishWord());
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDragAndDrop(data, shadowBuilder, v, 0);
                        return true;
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting drag: " + e.getMessage(), e);
                        return false;
                    }
                });

                wordContainer.addView(wordView);
            }

            // Assign drop listener to each image container (drop target)
            for (int i = 0; i < imageContainer.getChildCount(); i++) {
                View targetView = imageContainer.getChildAt(i);
                if (targetView != null) {
                    targetView.setOnDragListener(new ImageDropListener());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupGameUI: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi thiết lập trò chơi", Toast.LENGTH_SHORT).show();

            // Safely return to vocabulary management
            if (gameContainer != null) {
                gameContainer.setVisibility(View.GONE);
            }

            if (vocabularyManagementContainer != null) {
                vocabularyManagementContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ImageDropListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            try {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        try {
                            v.setBackgroundResource(R.drawable.highlight_border);
                        } catch (Exception e) {
                            Log.e(TAG, "Error setting highlight border: " + e.getMessage(), e);
                            // Fallback to continue even if highlighting fails
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackground(null);
                        return true;

                    case DragEvent.ACTION_DROP:
                        v.setBackground(null);

                        // Get dragged view safely
                        View draggedView = (View) event.getLocalState();
                        if (draggedView == null) {
                            Log.e(TAG, "Dragged view is null");
                            return false;
                        }

                        // Find the TextView in the dragged view
                        TextView tvWord = draggedView.findViewById(R.id.tvWord);
                        if (tvWord == null) {
                            Log.e(TAG, "Word TextView not found in dragged view");
                            return false;
                        }

                        String draggedText = tvWord.getText().toString();

                        // Get target label safely
                        TextView targetLabel = v.findViewById(R.id.targetLabel);
                        if (targetLabel == null) {
                            Log.e(TAG, "Target label not found");
                            return false;
                        }

                        Object tagObj = targetLabel.getTag();
                        if (tagObj == null) {
                            Log.e(TAG, "Target tag is null");
                            return false;
                        }

                        String correctText = tagObj.toString();

                        if (draggedText.equals(correctText)) {
                            Toast.makeText(DrapDropActivity.this, "Đúng!", Toast.LENGTH_SHORT).show();
                            draggedView.setVisibility(View.INVISIBLE);

                            // Ẩn hoàn toàn view chứa hình ảnh đích (item_target_image)
                            v.setVisibility(View.INVISIBLE);

                            checkGameCompletion();
                        } else {
                            Toast.makeText(DrapDropActivity.this, "Sai! Hãy thử lại", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackground(null);
                        return true;

                    default:
                        return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in drag listener: " + e.getMessage(), e);
                return false;
            }
        }
    }

    private void checkGameCompletion() {
        try {
            if (wordContainer == null) {
                Log.e(TAG, "Word container is null when checking completion");
                return;
            }

            boolean allMatched = true;
            for (int i = 0; i < wordContainer.getChildCount(); i++) {
                View child = wordContainer.getChildAt(i);
                if (child != null && child.getVisibility() == View.VISIBLE) {
                    allMatched = false;
                    break;
                }
            }

            if (allMatched) {
                Toast.makeText(this, "Chúc mừng! Bạn đã hoàn thành trò chơi!", Toast.LENGTH_LONG).show();

                // After 2 seconds, return to vocabulary management
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        if (gameContainer != null) {
                            gameContainer.setVisibility(View.GONE);
                        }

                        if (vocabularyManagementContainer != null) {
                            vocabularyManagementContainer.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error transitioning back to vocabulary UI: " + e.getMessage(), e);
                    }
                }, 2000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in checkGameCompletion: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        // Đóng kết nối cơ sở dữ liệu khi hủy Activity
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}