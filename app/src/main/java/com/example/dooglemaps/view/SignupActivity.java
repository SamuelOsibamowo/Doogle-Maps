package com.example.dooglemaps.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dooglemaps.R;
import com.example.dooglemaps.model.User;
import com.example.dooglemaps.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmailSignup, etPasswordSignup, etName, etUsername;
    private Button btnSignup;
    private TextView tvToLogin;
    private AuthViewModel viewModel;

    private FirebaseDatabase rootNode;
    String email;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(SignupActivity.this.getApplication())).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra("email", email);
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
                String email = etEmailSignup.getText().toString();
                String pass = etPasswordSignup.getText().toString();
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                signUpUser(email, pass, name, username);

            }
        });
    }




    private void signUpUser(String email, String pass, String name, String username) {
        if (!email.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !username.isEmpty()) {
            // SignsUp/Registers the user
            viewModel.register(email, pass);

            // Adds the users information to the database
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("users");
            User user = new User(name, username, email, pass);
            reference.child(username).setValue(user);
        }
    }

    private void goToLoginPage(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}