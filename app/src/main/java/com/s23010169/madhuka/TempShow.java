package com.s23010169.madhuka;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;
import android.util.Log;

public class TempShow extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "TempShow";
    
    // Constants
    private static final int TEMP_THRESHOLD = 69;
    private static final int INITIAL_TEMP = 25;
    private static final int MIN_TEMP = 0;
    private static final int MAX_TEMP = 100;
    private static final String TEMP_FORMAT = "%.1f°";
    
    // UI Components
    private TextView currentTempLabel;
    private TextView currentTemperatureValue;
    private TextView sliderValue;
    private TextView minTemp;
    private TextView maxTemp;
    private SeekBar temperatureSlider;
    private Button dryButton;
    private Button coolButton;
    private Button fanButton;
    private Button heatButton;
    
    // System Components
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor ambientTempSensor;
    
    // State Variables
    private float currentTemperature = INITIAL_TEMP;
    private boolean isAlarmPlaying = false;
    private boolean isUsingSensor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_show);
        
        initializeComponents();
        setupTemperatureSensor();
        setupUI();
    }

    private void initializeComponents() {
        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);
        mediaPlayer.setLooping(true);

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
    }

    private void setupTemperatureSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            ambientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            if (ambientTempSensor != null) {
                isUsingSensor = true;
                Log.d(TAG, "Temperature sensor found: " + ambientTempSensor.getName());
                showToast("Using device temperature sensor");
            } else {
                Log.d(TAG, "Temperature sensor not found");
                showToast("Temperature sensor not available. Using manual control.");
            }
        }
    }

    private void setupUI() {
        // Set initial values
        updateTemperatureDisplay(INITIAL_TEMP);
        minTemp.setText(String.valueOf(MIN_TEMP));
        maxTemp.setText(String.valueOf(MAX_TEMP));

        // Configure SeekBar
        temperatureSlider.setMax(MAX_TEMP);
        temperatureSlider.setProgress(INITIAL_TEMP);
        temperatureSlider.setMin(MIN_TEMP);

        setupButtonListeners();
        setupSeekBarListener();
    }

    private void setupButtonListeners() {
        Button[] modeButtons = {dryButton, coolButton, fanButton, heatButton};
        String[] modes = {"Dry", "Cool", "Fan", "Heat"};

        for (int i = 0; i < modeButtons.length; i++) {
            final Button button = modeButtons[i];
            final String mode = modes[i];
            button.setOnClickListener(v -> {
                resetButtonColors();
                button.setBackgroundResource(R.drawable.circle_button_background_blue);
                showToast(mode + " mode activated");
            });
        }
    }

    private void setupSeekBarListener() {
        temperatureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isUsingSensor || fromUser) {
                    updateTemperature(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isUsingSensor) {
                    isUsingSensor = false;
                    showToast("Switched to manual control");
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not used
            }
        });
    }

    private void resetButtonColors() {
        int[] buttonIds = {R.id.dryButton, R.id.coolButton, R.id.fanButton, R.id.heatButton};
        for (int id : buttonIds) {
            findViewById(id).setBackgroundResource(R.drawable.circle_button_background_gray);
        }
    }

    private void updateTemperature(float temperature) {
        currentTemperature = temperature;
        updateTemperatureDisplay(temperature);
        checkTemperatureThreshold(temperature);
    }

    private void updateTemperatureDisplay(float temperature) {
        currentTemperatureValue.setText(String.format(TEMP_FORMAT, temperature));
        sliderValue.setText(String.format("%.1f", temperature));
    }

    private void checkTemperatureThreshold(float temperature) {
        if (temperature >= TEMP_THRESHOLD && !isAlarmPlaying) {
            playAlarm();
            Log.d(TAG, "Temperature threshold exceeded: " + temperature + "°");
            showToast("Temperature warning: " + temperature + "°");
        } else if (temperature < TEMP_THRESHOLD && isAlarmPlaying) {
            stopAlarm();
            Log.d(TAG, "Temperature below threshold: " + temperature + "°");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ambientTempSensor != null && isUsingSensor) {
            sensorManager.registerListener(this, ambientTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Sensor listener registered");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Log.d(TAG, "Sensor listener unregistered");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE && isUsingSensor) {
            float temp = event.values[0];
            Log.d(TAG, "Sensor temperature: " + temp + "°");
            updateTemperature(temp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Sensor accuracy changed: " + accuracy);
    }

    private void playAlarm() {
        if (mediaPlayer != null && !isAlarmPlaying) {
            mediaPlayer.start();
            isAlarmPlaying = true;
            Log.d(TAG, "Alarm started");
        }
    }

    private void stopAlarm() {
        if (mediaPlayer != null && isAlarmPlaying) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isAlarmPlaying = false;
            Log.d(TAG, "Alarm stopped");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "MediaPlayer released");
        }
        sensorManager.unregisterListener(this);
    }
} 