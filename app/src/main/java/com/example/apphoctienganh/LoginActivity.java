package com.example.apphoctienganh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView textView;
    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;
    TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mapping();
        signUp();
        signIn();
        setForgotPassWord();

    }
    public void signUp(){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void signIn(){

        mAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            firebaseSetUp();
            }
        });
    }
    public void firebaseSetUp(){
        String email = editEmail.getText().toString();
        String pass = editPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,LayoutActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Xác thực không thành công",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void setForgotPassWord(){
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showForgotPasswordDialog();
            }
        });
    }
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forgot, null);
        builder.setView(dialogView);

        EditText emailEditText = dialogView.findViewById(R.id.editTextEmail);
        Button reset = dialogView.findViewById(R.id.buttonResetPassword);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = emailEditText.getText().toString();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Vui lòng kiểm tra email của bạn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Hủy Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void mapping(){
        editEmail =findViewById(R.id.editTextTaiKhoan);
        editPassword = findViewById(R.id.editTextMatKhau);
        textView = findViewById(R.id.textView_register);
        btnLogin = findViewById(R.id.buttonDangNhap);
        forgot = findViewById(R.id.textView_forgotPassword);
    }

}