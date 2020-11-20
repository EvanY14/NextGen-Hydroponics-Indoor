package com.example.hydroponicsapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hydroponicsapp.AddSystemActivity;
import com.example.hydroponicsapp.MainActivity;
import com.example.hydroponicsapp.R;
import com.example.hydroponicsapp.System;
import com.example.hydroponicsapp.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    private static Set<String> systemIDs;
    SharedPreferences sharedPref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private HomeViewModel homeViewModel;
    private Button update_btn;
    private FloatingActionButton addSystemBtn;
    private Object QuerySnapshot;
    private TextView ph_tv;
    private TextView EC_tv;
    private TextView temp_tv;
    private Switch celciusSwitch;
    private double ph, ec, tempC, tempF;
    private boolean celcius = false;
    private String name;
    private static SeekBar seekBar;
    private int currentSystem = 0;
    private ArrayList<String> systemIDsList = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPref = getActivity().getSharedPreferences( getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        name = sharedPref.getString(getString(R.string.saved_username_key), getString(R.string.saved_username_default_key));
        Log.d("Name", name);
        Log.d("Default name", sharedPref.getString("username_default", "username_default"));
        final TextView textView = root.findViewById(R.id.text_home);
        update_btn = root.findViewById(R.id.update_btn);
        ph_tv = root.findViewById(R.id.ph_tv_out);
        EC_tv = root.findViewById(R.id.EC_tv_out);
        temp_tv = root.findViewById(R.id.temp_tv_out);
        seekBar = root.findViewById(R.id.seekBar);
        addSystemBtn = root.findViewById(R.id.addSystemBtn);
        systemIDs = sharedPref.getStringSet(getString(R.string.systemIDs), new HashSet<String>());
        for(String s:systemIDs){
            systemIDsList.add(s);
        }
        /*if(systemIDs.isEmpty()){
            db.collection("users")
                    .whereEqualTo("user", sharedPref.getString(getString(R.string.saved_username_key), getString(R.string.saved_username_default_key)))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                Users user = new Users(task.getResult());
                                systemIDsList = new ArrayList<String>(user.getSystemIDs());
                                if(systemIDsList.size() <= 2)
                                    systemIDs.clear();
                                    systemIDsList.add("123456789");
                            }
                        }
                    });
        }else{
            systemIDsList = new ArrayList<>(systemIDs);
        }*/
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentSystem = seekBar.getProgress();
                if(seekBar.getMax() != 0) {
                    HomeFragment.seekBar.setVisibility(View.VISIBLE);
                    update_btn.performClick();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        update_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HomeFragment.systemIDs = sharedPref.getStringSet(getString(R.string.systemIDs), new HashSet<>());
                for(String s:systemIDs){
                    systemIDsList.add(s);
                }
                db.collection("systemIDs")
                        .whereEqualTo("systemID", systemIDsList.get(currentSystem))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    QuerySnapshot document = task.getResult();
                                    Users user = new Users(document);
                                    System system = new System(document);
                                    ph = system.getPh();
                                    ec = system.getEc();
                                    tempC = system.getTempC();
                                    tempF = system.getTempF();
                                    if(ph == -1.0){
                                        Toast noData = Toast.makeText(getContext(), "No data present", Toast.LENGTH_LONG);
                                        noData.show();
                                    }else {
                                        ph_tv.setText("" + ph);
                                        EC_tv.setText("" + ec);
                                        temp_tv.setText(celcius ? "" + tempC + "C" : "" + tempF + "F");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                    Toast.makeText(getContext(), "Unable to retrieve data", Toast.LENGTH_LONG);
                                }
                            }
                        });
            }

        });
        if(!name.equals(sharedPref.getString("username_default", "username_default"))) {
            Log.d("System IDs Default", systemIDsList.toString());
            db.collection("systemIDs")
                    .whereEqualTo("systemID", systemIDsList.get(currentSystem))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                QuerySnapshot document = task.getResult();
                                //Users user = new Users(document);
                                System system = new System(document);
                                ph = system.getPh();
                                ec = system.getEc();
                                tempC = system.getTempC();
                                tempF = system.getTempF();
                                ph_tv.setText("" + ph);
                                EC_tv.setText("" + ec);
                                celcius = sharedPref.getBoolean(getString(R.string.celcius_key), false);
                                temp_tv.setText(celcius ? "" + tempC + "C" : "" + tempF + "F");
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                                Toast.makeText(getContext(), "Unable to retrieve data", Toast.LENGTH_LONG);
                            }
                        }
                    });
            seekBar.setMax(systemIDsList.size()-1);
        }else{
            seekBar.setMax(systemIDsList.size());
            seekBar.setVisibility(View.VISIBLE);
        }
        addSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddSystemActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}