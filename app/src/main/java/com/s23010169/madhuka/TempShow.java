package com.s23010169.madhuka;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TempShow extends AppCompatActivity {
    private TextView temperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_show);

        temperatureTextView = findViewById(R.id.temperatureTextView);
        
        // Here you can add code to fetch and display actual temperature data
        // For now, we'll just show a placeholder
        temperatureTextView.setText("Current Temperature: 25°C");
    }
} 