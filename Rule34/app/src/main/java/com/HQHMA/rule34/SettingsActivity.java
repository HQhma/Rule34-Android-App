package com.HQHMA.rule34;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat highResSwitch;
    Spinner sortSpinner;
    ImageButton backButton;

    TextView downloadLimitTV;
    SeekBar downloadLimitSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Rule34",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        highResSwitch = findViewById(R.id.highResSwitch);
        downloadLimitSeekBar = findViewById(R.id.downloadLimitSeekBar);
        downloadLimitTV = findViewById(R.id.downloadLimitTV);

        boolean highResCB_Bool = sharedPreferences.getBoolean("highResCB_Bool",true);
        highResSwitch.setChecked(highResCB_Bool);
        downloadLimitSeekBar.setEnabled(highResCB_Bool);

        int downloadLimitSeekBarProgress = sharedPreferences.getInt("downloadLimitSeekBarProgress",4);
        downloadLimitSeekBar.setProgress(downloadLimitSeekBarProgress);
        downloadLimitTV.setText("Max size for high-res download: " + (downloadLimitSeekBarProgress+1) + "MB");
        if (downloadLimitSeekBarProgress == 10)
            downloadLimitTV.setText("Max size for high-res download: Unlimited");

        highResSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("highResCB_Bool",isChecked);
                editor.commit();
                downloadLimitSeekBar.setEnabled(isChecked);
            }
        });

        downloadLimitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editor.putInt("downloadLimitSeekBarProgress",i);
                editor.commit();
                downloadLimitTV.setText("Max size for high-res download: " + (i+1) + "MB");
                if (i == 10)
                    downloadLimitTV.setText("Max size for high-res download: Unlimited");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sortSpinner = findViewById(R.id.sortSpinner);
        String[] spinnerItems = getResources().getStringArray(R.array.sort_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        int sortSpinnerItem_Index = sharedPreferences.getInt("sortSpinnerItem_Index",0);
        sortSpinner.setSelection(sortSpinnerItem_Index);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("sortSpinnerItem_Index",i);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}