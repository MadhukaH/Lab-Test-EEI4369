package com.s23010169.madhuka;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TempShow extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "TempShow";
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
    
    private SensorManager sensorManager;
    private Sensor ambientTempSensor;
    private MediaPlayer mediaPlayer;
    private static final float TEMP_THRESHOLD = 69.0f;
    private boolean isAlarmPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_show);

        // Initialize sensor manager and temperature sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ambientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        
        if (ambientTempSensor == null) {
            Toast.makeText(this, "Temperature sensor not available", Toast.LENGTH_LONG).show();
        }

        // Initialize MediaPlayer
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: what=" + what + " extra=" + extra);
                    Toast.makeText(TempShow.this, "Error playing alarm sound", Toast.LENGTH_SHORT).show();
                    return false;
                });
            } else {
                Log.e(TAG, "Failed to create MediaPlayer");
                Toast.makeText(this, "Failed to initialize alarm sound", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MediaPlayer", e);
            Toast.makeText(this, "Error initializing alarm sound: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
        minTemp.setText("0");
        maxTemp.setText("100");

        // Configure SeekBar
        temperatureSlider.setMax(100);
        temperatureSlider.setProgress(40);
        temperatureSlider.setMin(0);

        temperatureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTemperatureValue.setText(progress + "°");
                sliderValue.setText(String.valueOf(progress));
                
                // Check if temperature exceeds threshold
                if (progress > TEMP_THRESHOLD && !isAlarmPlaying) {
                    playAlarm();
                } else if (progress <= TEMP_THRESHOLD && isAlarmPlaying) {
                    stopAlarm();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ambientTempSensor != null) {
            sensorManager.registerListener(this, ambientTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (isAlarmPlaying) {
            stopAlarm();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            currentTemperatureValue.setText(String.format("%.1f°", temperature));
            
            if (temperature > TEMP_THRESHOLD && !isAlarmPlaying) {
                playAlarm();
            } else if (temperature <= TEMP_THRESHOLD && isAlarmPlaying) {
                stopAlarm();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void playAlarm() {
        if (mediaPlayer != null && !isAlarmPlaying) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    isAlarmPlaying = true;
                    Toast.makeText(this, "Temperature threshold exceeded!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Alarm started playing");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error playing alarm", e);
                Toast.makeText(this, "Error playing alarm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "Cannot play alarm: mediaPlayer=" + (mediaPlayer != null) + ", isAlarmPlaying=" + isAlarmPlaying);
        }
    }

    private void stopAlarm() {
        if (mediaPlayer != null && isAlarmPlaying) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    isAlarmPlaying = false;
                    Log.d(TAG, "Alarm stopped");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping alarm", e);
                Toast.makeText(this, "Error stopping alarm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "MediaPlayer released");
            } catch (Exception e) {
                Log.e(TAG, "Error releasing MediaPlayer", e);
            }
        }
    }
} 