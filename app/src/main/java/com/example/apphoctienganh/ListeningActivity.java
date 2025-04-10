package com.example.apphoctienganh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class ListeningActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button startButton;
    private Button stopButton;
    private TextView textViewStart;
    private TextView textViewEnd;
    private SeekBar seekBar;
    private Button nextButton;
    private Button outButton;
    private ImageView imageView;
    private RadioButton aRadioButton;
    private RadioButton bRadioButton;
    private RadioButton cRadioButton;
    private RadioButton dRadioButton;
    private int count = 0;
    private int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        initializeViews();
        initializeMediaPlayer();
        setupStartButton();
        setupSeekBar();
        setupStopButton();
        setupNextButton();
        setupAnswerRadioGroup();
        setUpOut();
    }
    public void setUpOut(){
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListeningActivity.this,LayoutActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initializeViews() {
        startButton = findViewById(R.id.start);
        stopButton = findViewById(R.id.stop);
        textViewStart = findViewById(R.id.textViewStart);
        textViewEnd = findViewById(R.id.textViewEnd);
        seekBar = findViewById(R.id.seekBar);
        nextButton = findViewById(R.id.next);
        outButton = findViewById(R.id.out);
        imageView = findViewById(R.id.image);
        aRadioButton = findViewById(R.id.aRadioButton);
        bRadioButton = findViewById(R.id.bRadioButton);
        cRadioButton = findViewById(R.id.cRadioButton);
        dRadioButton = findViewById(R.id.dRadioButton);
        Picasso.get()
                .load("https://study4.com/media/tez_media/img/ets_toeic_2018_test_3_ets_toeic_2018_test_3_1.png")
                .into(imageView);
    }
    private void setupAnswerRadioGroup() {
        RadioGroup answerRadioGroup = findViewById(R.id.answerRadioGroup);
        answerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.aRadioButton && check == 0) {
                    Toast.makeText(ListeningActivity.this, "Đáp án đúng", Toast.LENGTH_SHORT).show();
                }
                else if(checkedId == R.id.cRadioButton && check > 0){
                    Toast.makeText(ListeningActivity.this, "Đáp án đúng", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ListeningActivity.this, "Đáp án sai", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ets_toeic_2018_test_3_1);
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                setTimeTotal();
                updateTime();
            }
        });
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void setupStopButton() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    startButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                Picasso.get()
                        .load("https://study4.com/media/tez_media/img/ets_toeic_2018_test_3_ets_toeic_2018_test_3_3.png")
                        .into(imageView);

                mediaPlayer = MediaPlayer.create(ListeningActivity.this, R.raw.ets_toeic_2018_test_3_3);
                setTimeTotal();
                count++;
                check++;
                if(count>=2){
                    Toast.makeText(ListeningActivity.this, "Bộ câu hỏi đã hết đợi chúng tôi cập nhật thêm", Toast.LENGTH_SHORT).show();
                }
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            }
        });
    }

    private void setTimeTotal() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        textViewEnd.setText(dateFormat.format(mediaPlayer.getDuration()) +"");
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void updateTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                textViewStart.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this,500);
            }
        },100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
