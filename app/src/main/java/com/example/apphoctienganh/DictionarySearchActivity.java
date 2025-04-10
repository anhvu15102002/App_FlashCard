package com.example.apphoctienganh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class DictionarySearchActivity extends AppCompatActivity {

    private Translator englishToVietnameseTranslator;
    private Translator vietnameseToEnglishTranslator;
    private boolean isEnglishToVietnameseModelReady = false;
    private boolean isVietnameseToEnglishModelReady = false;
    private TextView titleTextView;
    private EditText inputTextEdit;
    private TextView translatedTextView;
    private Button translateButton;
    private RadioGroup translationDirectionGroup;
    private RadioButton engToVietRadio;
    private RadioButton vietToEngRadio;
    private boolean isEnglishToVietnamese = true; // Mặc định dịch từ tiếng Anh sang tiếng Việt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_search);

        // Khởi tạo và thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Thiết lập sự kiện click cho toolbar để điều hướng về màn hình ActivityLayout
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để điều hướng về màn hình ActivityLayout
                Intent intent = new Intent(DictionarySearchActivity.this, LayoutActivity.class);
                // Xóa stack các activity khác và đặt activity ActivityLayout làm activity khởi đầu
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Đóng activity hiện tại
            }
        });

        // Khởi tạo các thành phần UI
        titleTextView = findViewById(R.id.tv_title);
        inputTextEdit = findViewById(R.id.edt_enter_text);
        translatedTextView = findViewById(R.id.tv_translated);
        translateButton = findViewById(R.id.btn_language_translation);
        translationDirectionGroup = findViewById(R.id.radio_group_translation);
        engToVietRadio = findViewById(R.id.radio_eng_to_viet);
        vietToEngRadio = findViewById(R.id.radio_viet_to_eng);

        // Khởi tạo translator cho cả hai hướng
        initializeTranslators();

        // Tải mô hình dịch
        downloadTranslationModels();

        // Thiết lập listener cho RadioGroup để xác định hướng dịch
        translationDirectionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isEnglishToVietnamese = checkedId == R.id.radio_eng_to_viet;
                updateUIForTranslationDirection();
            }
        });

        // Thiết lập listener cho nút dịch
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToTranslate = inputTextEdit.getText().toString().trim();
                if (textToTranslate.isEmpty()) {
                    Toast.makeText(DictionarySearchActivity.this, "Vui lòng nhập văn bản để dịch.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEnglishToVietnamese) {
                    if (isEnglishToVietnameseModelReady) {
                        translateEnglishToVietnamese(textToTranslate);
                    } else {
                        Toast.makeText(DictionarySearchActivity.this, "Đang tải mô hình dịch Anh-Việt. Vui lòng đợi.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isVietnameseToEnglishModelReady) {
                        translateVietnameseToEnglish(textToTranslate);
                    } else {
                        Toast.makeText(DictionarySearchActivity.this, "Đang tải mô hình dịch Việt-Anh. Vui lòng đợi.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Mặc định chọn Anh-Việt
        engToVietRadio.setChecked(true);
        updateUIForTranslationDirection();
    }

    // Cập nhật giao diện dựa trên hướng dịch
    private void updateUIForTranslationDirection() {
        if (isEnglishToVietnamese) {
            titleTextView.setText("Dịch từ tiếng Anh sang tiếng Việt");
            inputTextEdit.setHint("Điền từ tiếng anh cần dịch");
            translatedTextView.setText("Từ ngữ dịch thành công sẽ hiển thị ở đây");
        } else {
            titleTextView.setText("Dịch từ tiếng Việt sang tiếng Anh");
            inputTextEdit.setHint("Điền từ tiếng việt cần dịch");

        }
    }

    // Khởi tạo translators cho cả hai hướng
    private void initializeTranslators() {
        // Khởi tạo translator Anh-Việt
        TranslatorOptions engToVietOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                .build();
        englishToVietnameseTranslator = Translation.getClient(engToVietOptions);

        // Khởi tạo translator Việt-Anh
        TranslatorOptions vietToEngOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        vietnameseToEnglishTranslator = Translation.getClient(vietToEngOptions);
    }

    // Tải xuống cả hai mô hình dịch
    private void downloadTranslationModels() {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()  // Yêu cầu kết nối Wi-Fi
                .build();

        // Tải mô hình Anh-Việt
        englishToVietnameseTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isEnglishToVietnameseModelReady = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DictionarySearchActivity.this, "Không thể tải mô hình Anh-Việt.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Tải mô hình Việt-Anh
        vietnameseToEnglishTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isVietnameseToEnglishModelReady = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DictionarySearchActivity.this, "Không thể tải mô hình Việt-Anh.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức dịch từ tiếng Anh sang tiếng Việt
    private void translateEnglishToVietnamese(String text) {
        englishToVietnameseTranslator.translate(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        translatedTextView.setText(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        translatedTextView.setText("Dịch thất bại.");
                        Toast.makeText(DictionarySearchActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức dịch từ tiếng Việt sang tiếng Anh
    private void translateVietnameseToEnglish(String text) {
        vietnameseToEnglishTranslator.translate(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        translatedTextView.setText(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        translatedTextView.setText("Translation failed.");
                        Toast.makeText(DictionarySearchActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng tài nguyên của translators khi hoạt động bị hủy
        if (englishToVietnameseTranslator != null) {
            englishToVietnameseTranslator.close();
        }
        if (vietnameseToEnglishTranslator != null) {
            vietnameseToEnglishTranslator.close();
        }
    }
}
