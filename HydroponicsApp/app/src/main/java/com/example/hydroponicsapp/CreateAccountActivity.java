package com.example.hydroponicsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText username, password, systemID;
    private Button activateAcctBtn;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        setContentView(R.layout.create_account);
        username = findViewById(R.id.usernameCreateET);
        password = findViewById(R.id.passwordCreateET);
        systemID = findViewById(R.id.systemIDCreateET);
        activateAcctBtn = findViewById(R.id.activateAcctBtn);
        Context context = CreateAccountActivity.this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        activateAcctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                String systemIDText = systemID.getText().toString();
                ArrayList<String> systemIDs = new ArrayList<String>();
                systemIDs.add(systemIDText);
                Users newUser = new Users(usernameText, passwordText, systemIDs);
                db.collection("users")
                        .whereEqualTo("user", usernameText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && task.getResult().isEmpty()){ //If there are no usernames that are the same, then add the user
                                    db.collection("users").document(newUser.getUser())
                                            .set(newUser)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sharedPref.edit().putString(getString(R.string.saved_username_key), usernameText).commit();
                                                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Error", "Error writing document", e);
                                                }
                                            });
                                }else{
                                    Toast usernameTaken = Toast.makeText(CreateAccountActivity.this, "Username is already taken", Toast.LENGTH_LONG);
                                    usernameTaken.show();
                                }
                            }
                        });


            }
        });
    }
}
