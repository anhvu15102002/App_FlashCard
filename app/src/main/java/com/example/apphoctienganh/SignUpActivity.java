package com.example.apphoctienganh;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText email,password,password2;
    private Button signUp;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mapping();
        signUpMethod();
    }
    private void mapping(){
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        password2 = findViewById(R.id.editTextPasswordAgain);
        signUp = findViewById(R.id.buttonSignUp);
    }
    public void signUpMethod(){
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseSignUp();
            }
        });
    }
    public void firebaseSignUp(){
        if(!password.getText().toString().equals(password2.getText().toString())){
            Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
        }
        else{
            String emailSignUp = email.getText().toString().trim();
            String passwordSignUp = password.getText().toString().trim();
            mAuth.createUserWithEmailAndPassword(emailSignUp, passwordSignUp)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Có lỗi xảy ra, vui lòng thử lại",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}