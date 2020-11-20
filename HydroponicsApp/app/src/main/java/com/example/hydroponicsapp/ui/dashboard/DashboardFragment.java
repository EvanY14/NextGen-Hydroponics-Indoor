package com.example.hydroponicsapp.ui.dashboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hydroponicsapp.MainActivity;
import com.example.hydroponicsapp.R;
import com.example.hydroponicsapp.ui.home.HomeFragment;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private Button logOutBtn;
    private Switch celciusSwitch;
    private Boolean celcius;
    private TextView user;
    SharedPreferences prefs;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs =  getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        logOutBtn = root.findViewById(R.id.logOutBtn);
        celciusSwitch = root.findViewById(R.id.celcius_switch);
        user = root.findViewById(R.id.userTv);
        user.setText(prefs.getString(getString(R.string.saved_username_key), getString(R.string.saved_username_default_key)));
        celcius = prefs.getBoolean(getString(R.string.celcius_key), false);
        celciusSwitch.setChecked(celcius);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().clear().commit();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent); //restart main activity with login screen
            }
        });

        celciusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                celcius = celciusSwitch.isChecked();
                prefs.edit().putBoolean(getString(R.string.celcius_key), celcius).apply();
            }
        });
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}