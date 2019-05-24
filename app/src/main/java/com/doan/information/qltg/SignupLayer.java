package com.doan.information.qltg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupLayer extends AppCompatActivity implements View.OnClickListener {


    private Button buttonRegister;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewsign;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_layer);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonSignup);

        editTextUsername = (EditText) findViewById(R.id.signupUsername);
        editTextEmail = (EditText) findViewById(R.id.signupEmail);
        editTextPassword = (EditText) findViewById(R.id.signupPassword);

        textViewsign = (TextView) findViewById(R.id.signAh);

        buttonRegister.setOnClickListener(this);
        textViewsign.setOnClickListener(this);
        mRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String date = "";
        final String event = "";
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            //username is empty
            Toast.makeText(this, "Please Enter Username !! ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter your password !!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User ....... ");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupLayer.this, "Registered Successfully, Please Login", Toast.LENGTH_SHORT).show();
                            finish();
                            //Đẩy thông tin lên database

                            Intent i = new Intent(getApplicationContext(), LoginLayer.class);
                            startActivity(i);
                            firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser user2 = firebaseAuth.getCurrentUser();
                            String id = user2.getUid();
                            User user = new User(username, email, id);
                            mRef.child(id).setValue(user);


                        } else {
                            Toast.makeText(SignupLayer.this, "Could not register !!, Please Try Again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        }

        if (view == textViewsign) {
            //open sign
            finish();
            Intent i = new Intent(getApplicationContext(), LoginLayer.class);
            startActivity(i);
        }
    }
}
