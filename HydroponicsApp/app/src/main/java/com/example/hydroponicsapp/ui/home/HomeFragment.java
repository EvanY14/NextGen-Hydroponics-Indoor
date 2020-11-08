package com.example.hydroponicsapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hydroponicsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String test_name = "1RqKx6NGcXJxvggX33Hw";
    private HomeViewModel homeViewModel;
    private Button update_btn;
    private Object QuerySnapshot;
    private TextView ph_tv;
    private TextView EC_tv;
    private TextView temp_tv;
    private Switch celciusSwitch;
    private double ph, ec, tempC, tempF;
    private boolean celcius = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        update_btn = root.findViewById(R.id.update_btn);
        ph_tv = root.findViewById(R.id.ph_tv_out);
        EC_tv = root.findViewById(R.id.EC_tv_out);
        temp_tv = root.findViewById(R.id.temp_tv_out);
        celciusSwitch = root.findViewById(R.id.temp_switch);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DocumentReference docRef = db.collection("users").document(test_name);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                            Map<String, Object> data = document.getData();
                            if(ph == (double) data.get("pH")){
                                Toast no_updates = Toast.makeText(getContext(),"No new updates", Toast.LENGTH_LONG);
                                no_updates.show();
                            }else {
                                ph = (double) document.getData().get("pH");
                                Log.d(TAG, "Assigned ph");
                                ec = (double) document.getData().get("EC");
                                Log.d(TAG, "Assigned EC");
                                tempC = (double) document.getData().get("temp_c");
                                Log.d(TAG, "Assigned tempC");
                                tempF = (double) document.getData().get("temp_f");
                                Log.d(TAG, "Assigned tempF");
                                Log.d(TAG, "Assigned variables");
                                ph_tv.setText("" + ph);
                                EC_tv.setText("" + ec);
                                temp_tv.setText(celcius ? "" + tempC + "C":"" + tempF + "F");
                                Log.d(TAG, "Set TextViews");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            Toast.makeText(getContext(), "Unable to retrieve data", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
        celciusSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(celciusSwitch.isChecked()){
                    celcius = true;
                    celciusSwitch.setText("Celcius");
                    temp_tv.setText("" + tempC + "C");
                }else{
                    celcius = false;
                    celciusSwitch.setText("Fahrenheit");
                    temp_tv.setText("" + tempF + "F");
                }
            }
        });
        return root;
    }
}