package com.example.apphoctienganh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MultipleChoiceActivity extends AppCompatActivity {
    private TextView txtScore, txtQuestionCount, txtTime, txtQuestion;
    private Button[] answerButtons;
    private Button btnQuit, btnConfirm;
    private DataBaseGramarEnglish database;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private CountDownTimer countdownTimer;
    private int score;
    private int total;
    private int currentQuestionIndexSetText;
    private DataBasePointUser dataBasePointUser;
    private String email;
    private FirebaseAuth mAuth;
    private long remainingTimeInMillis;
    private List<UserPoint> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trac_nghiem);
        initializeViews();
        initializeDatabase();
        setQuestions();
        displayQuestion();
        answerQuestion();
        startCountdownTimer();
        setupSubmitButton();
    }
    private void setQuestionCount(){
        total = questionList.size();
        currentQuestionIndexSetText = currentQuestionIndex + 1;
        txtQuestionCount.setText("Question: " + currentQuestionIndexSetText + "/" + total);
        txtQuestionCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_question, 0);
    }
    private void initializeViews() {
        txtScore = findViewById(R.id.txtscoreDK);
        txtQuestionCount = findViewById(R.id.txtquestcountDK);
        txtTime = findViewById(R.id.txttimeDK);
        txtQuestion = findViewById(R.id.txtquestionDK);
        answerButtons = new Button[] {
                findViewById(R.id.txtanswer1),
                findViewById(R.id.txtanswer2),
                findViewById(R.id.txtanswer3),
                findViewById(R.id.txtanswer4)
        };
        btnQuit = findViewById(R.id.btnQuitDK);
        btnConfirm = findViewById(R.id.btnconfirmDK);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail();
    }

    private void initializeDatabase() {
        database = new DataBaseGramarEnglish(this);
        dataBasePointUser = new DataBasePointUser(MultipleChoiceActivity.this);
    }

    private void setQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("He ____ to the store yesterday.", "went", "went goes go gone"));
        questionList.add(new Question("She ____ her homework every day.", "does", "does do did doing"));
        questionList.add(new Question("I ____ TV when the phone rang.", "was watching", "was watching watch watches"));
        questionList.add(new Question("We usually ____ dinner at 7 PM.", "have", "have has had having"));
        questionList.add(new Question("The cat ____ on the chair right now.", "sitting", "sittin sit sits sitting"));
        questionList.add(new Question("They ____ their car last week.", "sold", "sold sell sells selling"));
        questionList.add(new Question("My sister ____ English very well.", "speaks", "speaks speak spoke speaking"));
        questionList.add(new Question("She ____ her keys in the house.", "forgot", "forgot forgets forgetting forget"));
        questionList.add(new Question("He ____ basketball every Saturday.", "plays", "plays play played playing"));
        questionList.add(new Question("The students ____ to school by bus.", "go", "goes go went going"));

        for (Question question : questionList) {
            database.addQuestion(question);
        }
    }

    private void displayQuestion() {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        String[] choices = currentQuestion.getAllchoice().split(" ");
        for (int i = 0; i < choices.length; i++) {
            answerButtons[i].setText(choices[i]);
        }
        txtQuestion.setText(currentQuestion.getQuestion());
    }

    private void answerQuestion() {
        for (final Button button : answerButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String answer = button.getText().toString();
                    checkAnswer(answer);
                    moveToNextQuestion();
                }
            });
        }
    }

    private void checkAnswer(String selectedAnswer) {
        String correctAnswer = questionList.get(currentQuestionIndex).getAnswer();
        String feedback;
        if (selectedAnswer.equals(correctAnswer)) {
            feedback = "Đáp án đúng";
            score += 10;
            txtScore.setText(String.valueOf("Score: " + score));

        } else {
            feedback = "Đáp án sai";
        }
        setQuestionCount();

        Toast.makeText(MultipleChoiceActivity.this, feedback, Toast.LENGTH_SHORT).show();
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size() ) {
            displayQuestion();
        }
        else if(currentQuestionIndex == questionList.size()) {
            Toast.makeText(MultipleChoiceActivity.this, "Bạn đã hoàn thành tất cả câu hỏi", Toast.LENGTH_SHORT).show();
            for(final  Button button : answerButtons){
                button.setEnabled(false);
            }
        }

    }

    private void startCountdownTimer() {
        countdownTimer = new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                txtTime.setText(String.format("%02d:%02d", minutes, seconds));
                remainingTimeInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                Toast.makeText(MultipleChoiceActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
            }
        };
        countdownTimer.start();
    }
    private int[] getCurrentTime() {
        int[] time = new int[2]; // Array to hold minutes and seconds
        Calendar calendar = Calendar.getInstance();
        time[0] = calendar.get(Calendar.MINUTE); // Get current minute
        time[1] = calendar.get(Calendar.SECOND); // Get current second
        return time;
    }
    private void setupSubmitButton() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestionIndex < questionList.size() - 1) {
                    Toast.makeText(MultipleChoiceActivity.this, "Bạn chưa hoàn thành hết tất cả câu hỏi", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MultipleChoiceActivity.this, String.valueOf(score), Toast.LENGTH_SHORT).show();
                    long totalSeconds = remainingTimeInMillis / 1000;
                    long minutes = totalSeconds / 60;
                    long seconds = totalSeconds % 60;
                    String time = String.format("%02d:%02d", minutes, seconds);
                    dataBasePointUser.addPoints(email,score,time);
                    Intent intent = new Intent(MultipleChoiceActivity.this,LayoutActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MultipleChoiceActivity.this,LayoutActivity.class);
               startActivity(intent);
               finish();
            }
        });
    }


}
