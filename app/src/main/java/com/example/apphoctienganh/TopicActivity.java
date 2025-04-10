package com.example.apphoctienganh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity {
    private ListView listView;
    private TopicAdapter adapter;
    private List<TopicModel> list;
    private VocabularySqlite vocabularySqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        listView = findViewById(R.id.listview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back

        list = new ArrayList<>();
        vocabularySqlite = new VocabularySqlite(TopicActivity.this);

        // Khởi tạo dữ liệu ban đầu
        vocabularySqlite.addVocabulary(new Vocabulary("aaaaaa", "https://th.bing.com/th/id/OIP.WAS7V8C4sT0piQFCANTJkAHaHa?rs=1&pid=ImgDetMain", "aaaaa", "Môi trường"));


        list.add(new TopicModel(R.drawable.sport, "Thể thao"));
        list.add(new TopicModel(R.drawable.enviroment, "Môi trường"));
        list.add(new TopicModel(R.drawable.ic_dollar, "Tiền bạc"));
        list.add(new TopicModel(R.drawable.animal,"Động vật"));
        list.add(new TopicModel(R.drawable.education,"Giáo dục"));
        adapter = new TopicAdapter(TopicActivity.this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(TopicActivity.this, VocabularyActivity.class);
                    intent.putExtra("topic", "Thể thao");
                    startActivity(intent);
                } else if (i == 1) {
                    Intent intent = new Intent(TopicActivity.this, VocabularyActivity.class);
                    intent.putExtra("topic", "Môi trường");
                    startActivity(intent);
                } else if (i == 2) {
                    Intent intent = new Intent(TopicActivity.this, VocabularyActivity.class);
                    intent.putExtra("topic", "Tiền bạc");
                    startActivity(intent);
                } else if (i == 3) {
                    Intent intent = new Intent(TopicActivity.this, VocabularyActivity.class);
                    intent.putExtra("topic", "Động vật");
                    startActivity(intent);
                }else if (i == 4) {
                    Intent intent = new Intent(TopicActivity.this, VocabularyActivity.class);
                    intent.putExtra("topic", "Giáo dục");
                    startActivity(intent);
                }

            }
        });
    }

    // Thêm menu vào Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic, menu);
        return true;
    }

    // Xử lý sự kiện khi người dùng chọn mục trong menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(TopicActivity.this, LayoutActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_add_vocabulary) {
            // Hiển thị dialog để thêm từ vựng
            showAddVocabularyDialog();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // Hiển thị dialog để người dùng nhập từ vựng mới
    private void showAddVocabularyDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_vocabulary);
        dialog.setTitle("Thêm từ vựng");

        EditText editWord = dialog.findViewById(R.id.edit_word);
        EditText editImageUrl = dialog.findViewById(R.id.edit_image_url);
        EditText editMeaning = dialog.findViewById(R.id.edit_meaning);
        Spinner spinnerTopic = dialog.findViewById(R.id.spinner_topic);
        Button btnAdd = dialog.findViewById(R.id.btn_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Thiết lập Spinner với danh sách chủ đề
        String[] topics = {"Thể thao", "Môi trường", "Tiền bạc", "Động vật","Giáo dục"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, topics);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopic.setAdapter(spinnerAdapter);

        btnAdd.setOnClickListener(v -> {
            String word = editWord.getText().toString().trim();
            String imageUrl = editImageUrl.getText().toString().trim();
            String meaning = editMeaning.getText().toString().trim();
            String topic = spinnerTopic.getSelectedItem().toString();

            if (word.isEmpty() || meaning.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(TopicActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm từ vựng vào cơ sở dữ liệu và kiểm tra kết quả
            Vocabulary vocabulary = new Vocabulary(word, imageUrl.isEmpty() ? "" : imageUrl, meaning, topic);
            boolean isAdded = vocabularySqlite.addVocabulary(vocabulary);

            if (isAdded) {
                Toast.makeText(TopicActivity.this, "Thêm từ thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(TopicActivity.this, "Từ vựng đã có trong hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Hiển thị dialog để người dùng nhập chủ đề mới
    private void showAddTopicDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_topic);
        dialog.setTitle("Thêm chủ đề");

        EditText editTopicName = dialog.findViewById(R.id.edit_topic_name);
        Button btnAdd = dialog.findViewById(R.id.btn_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(v -> {
            String topicName = editTopicName.getText().toString().trim();

            if (topicName.isEmpty()) {
                Toast.makeText(TopicActivity.this, "Vui lòng nhập tên chủ đề", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem chủ đề đã tồn tại chưa
            for (TopicModel topic : list) {
                if (topic.getTopic().equalsIgnoreCase(topicName)) {
                    Toast.makeText(TopicActivity.this, "Chủ đề đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Thêm chủ đề mới vào danh sách (sử dụng một icon mặc định)
            list.add(new TopicModel(R.drawable.ic_default_topic, topicName)); // Bạn cần tạo một icon mặc định
            adapter.notifyDataSetChanged(); // Cập nhật giao diện
            Toast.makeText(TopicActivity.this, "Thêm chủ đề thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}