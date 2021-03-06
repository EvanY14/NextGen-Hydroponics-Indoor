package com.example.hydroponicsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddSystemActivity extends AppCompatActivity {
    private EditText systemID, systemName;
    private Button addSystemBtn;
    private SharedPreferences sharedPref;
    private DatabaseReference database;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.add_system);
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        systemID = findViewById(R.id.systemID_et);
        addSystemBtn = findViewById(R.id.addSystem_btn);
        systemName = findViewById(R.id.systemName_et);
        addSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String systemIDText = systemID.getText().toString();
                String systemNameText = systemName.getText().toString();
                addSystem(systemIDText, systemNameText);

            }
        });
    }

    private void addSystem(String systemID, String systemName) {
        System newSystem = new System(systemID, systemName, 0.0, 0.0, 0.0, 0.0);
        database.child(systemID).setValue(newSystem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System newSystem = new System(systemID, systemName, 0, 0, 0, 0);
                db.collection("systemIDs").document(newSystem.getSystemID())
                        .set(newSystem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Set<String> systemIDs = sharedPref.getStringSet("systemIDs", null);
                                systemIDs.add(systemID);
                                Users newUser = new Users(sharedPref.getString(getString(R.string.saved_username_key), getString(R.string.saved_username_default_key)),
                                        sharedPref.getString(getString(R.string.password_key), getString(R.string.password_default_key)), systemIDs);
                                sharedPref.edit().putStringSet(getString(R.string.systemIDs), systemIDs).commit();
                                db.collection("users").document(newUser.getUser())
                                        .update("systemIDs", newUser.getSystemIDs())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(AddSystemActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Error", "Error writing document", e);
                                            }
                                        });

                                Intent intent = new Intent(AddSystemActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error writing document", e);
                    }
                });
            }
        /*db.collection("systemIDs")
                .whereEqualTo("systemID", systemIDText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().isEmpty()){
                            System newSystem = new System(systemIDText, 0,0,0,0);
                            db.collection("systemIDs").document(newSystem.getSystemID())
                                    .set(newSystem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Set<String> systemIDs = sharedPref.getStringSet("systemIDs", null);
                                            systemIDs.add(systemIDText);
                                            Users newUser = new Users(sharedPref.getString(getString(R.string.saved_username_key), getString(R.string.saved_username_default_key)),
                                                    sharedPref.getString(getString(R.string.password_key), getString(R.string.password_default_key)), systemIDs);
                                            sharedPref.edit().putStringSet(getString(R.string.systemIDs), systemIDs).commit();
                                            db.collection("users").document(newUser.getUser())
                                                    .update("systemIDs", newUser.getSystemIDs())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Intent intent = new Intent(AddSystemActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Error", "Error writing document", e);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Error", "Error writing document", e);
                                        }
                                    });
                        }else{
                            Toast.makeText(AddSystemActivity.this, "System already registered", Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
        });
    }
}

