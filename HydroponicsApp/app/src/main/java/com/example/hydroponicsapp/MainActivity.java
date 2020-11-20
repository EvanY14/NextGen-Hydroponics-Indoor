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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        EditText username = findViewById(R.id.username_et);
        EditText password = findViewById(R.id.password_et);
        Button loginBtn = findViewById(R.id.login_btn);
        Button createAcctBtn = findViewById(R.id.createAcctBtn);
        View nav_host = findViewById(R.id.nav_host_fragment);
        username.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE);
        nav_host.setVisibility(View.GONE);
        createAcctBtn.setVisibility(View.GONE);
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        /*String defaultValue = getResources().getString(R.string.saved_username_default_key);
        String username = sharedPref.getString(getString(R.string.saved_username_key), defaultValue);*/
        Log.d("Pref", ""+!sharedPref.contains(getString(R.string.saved_username_key)));
        if(!sharedPref.contains(getString(R.string.saved_username_key))){
            navView.setVisibility(View.GONE);
            username.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            createAcctBtn.setVisibility(View.VISIBLE);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //First check credentials of user by checking the username/password combo in the database
                    String userText = username.getText().toString();
                    String passwordText = password.getText().toString();
                    db.collection("users")
                            .whereEqualTo("user", userText)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        db.collection("users")
                                                .whereEqualTo("password", passwordText)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                                                            sharedPref.edit()
                                                                    .putString(getString(R.string.saved_username_key), userText)
                                                                    .commit();//Add the username to the SharedPreferences so the user stays logged in
                                                            sharedPref.edit()
                                                                    .putString(getString(R.string.password_key), passwordText)
                                                                    .commit();
                                                            username.setVisibility(View.GONE);
                                                            password.setVisibility(View.GONE);
                                                            loginBtn.setVisibility(View.GONE);
                                                            createAcctBtn.setVisibility(View.GONE);
                                                            navView.setVisibility(View.VISIBLE);
                                                            nav_host.setVisibility(View.VISIBLE);
                                                        }else{
                                                            Toast invalidPassword = Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_LONG);
                                                            invalidPassword.show(); //Show Toast saying that the password is incorrect
                                                        }
                                                    }
                                                });
                                    }else{
                                        Toast invalidUser = Toast.makeText(MainActivity.this, "Invalid Username", Toast.LENGTH_LONG);
                                        invalidUser.show(); //Show Toast saying the username is incorrect
                                    }
                                }
                            });

                    //Second get all the System IDs for all of the systems a user owns and store it in a SharedPreferences object
                    db.collection("users")
                            .whereEqualTo("user", userText)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot document = task.getResult();
                                        Users user = new Users(document);
                                        Set<String> systemIDSet = new HashSet<>();
                                        for(String s:user.getSystemIDs()) {
                                            systemIDSet.add(s);
                                        }
                                        Log.d("System IDs", systemIDSet.toString());
                                        sharedPref.edit()
                                                .putStringSet(getString(R.string.systemIDs), systemIDSet)
                                                .commit();
                                    }
                                }
                            });
                }
            });

            createAcctBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            navView.setVisibility(View.VISIBLE);
            nav_host.setVisibility(View.VISIBLE);
            String userText = sharedPref.getString(getString(R.string.saved_username_key), getString(R.string.saved_username_default_key));
            db.collection("users").whereEqualTo("user", userText).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    Users user = new Users(queryDocumentSnapshots);
                    Set<String> systemIDSet = new HashSet<>();
                    for(String s:user.getSystemIDs()){
                        systemIDSet.add(s);
                    }
                    sharedPref.edit()
                            .putStringSet(getString(R.string.systemIDs), systemIDSet)
                            .commit();
                }
            });

        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

}