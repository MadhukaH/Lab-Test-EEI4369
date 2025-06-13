package com.s23010169.madhuka;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TempShow extends AppCompatActivity {
    private TextView currentTempLabel;
    private TextView currentTemperatureValue;
    private Button dryButton;
    private Button coolButton;
    private Button fanButton;
    private Button heatButton;
    private TextView sliderValue;
    private TextView minTemp;
    private TextView maxTemp;
    private SeekBar temperatureSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_show);

        // Initialize TextViews
        currentTempLabel = findViewById(R.id.currentTempLabel);
        currentTemperatureValue = findViewById(R.id.currentTemperatureValue);
        sliderValue = findViewById(R.id.sliderValue);
        minTemp = findViewById(R.id.minTemp);
        maxTemp = findViewById(R.id.maxTemp);

        // Initialize Buttons
        dryButton = findViewById(R.id.dryButton);
        coolButton = findViewById(R.id.coolButton);
        fanButton = findViewById(R.id.fanButton);
        heatButton = findViewById(R.id.heatButton);

        // Initialize SeekBar
        temperatureSlider = findViewById(R.id.temperatureSlider);

        // Set initial values
        currentTemperatureValue.setText("40°");
        sliderValue.setText("40");
        minTemp.setText("30");
        maxTemp.setText("50");

        // Configure SeekBar
        temperatureSlider.setMax(50); // Max temperature value
        temperatureSlider.setProgress(40); // Initial temperature value
        temperatureSlider.setMin(30); // Min temperature value (API 26+)

        temperatureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update current temperature and slider value
                currentTemperatureValue.setText(progress + "°");
                sliderValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not used for this implementation
            }
        });
    }
} 