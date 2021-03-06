package com.example.dooglemaps.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.User;
import com.example.dooglemaps.viewModel.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignupActivity extends AppCompatActivity {


    private final String TAG = "SignupActivity";
    private EditText etEmailSignup, etPasswordSignup, etName, etUsername;
    private Button btnSignup;
    private TextView tvToLogin;
    private AuthViewModel viewModel;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String email, pass, name, username, token;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // grab the unique token given to the device
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                token = task.getResult();
                Log.i(TAG, "TAG: " + token);
            }
        });

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(SignupActivity.this.getApplication())).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", pass);
                    intent.putExtra("name", name);
                    intent.putExtra("username", username);
                    intent.putExtra("token", token);

                    startActivity(intent);
                }
            }
        });


        tvToLogin = findViewById(R.id.tvToLogin);
        etEmailSignup = findViewById(R.id.etEmailSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        btnSignup = findViewById(R.id.btnSignup);

        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();

            }
        });
        
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmailSignup.getText().toString();
                pass = etPasswordSignup.getText().toString();
                name = etName.getText().toString();
                username = etUsername.getText().toString();
                signUpUser(email, pass);

            }
        });
    }




    private void signUpUser(String email, String pass) {
        if (!email.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !username.isEmpty()) {
            // SignsUp/Registers the user
            viewModel.register(email, pass);

        }
    }

    private void goToLoginPage(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}