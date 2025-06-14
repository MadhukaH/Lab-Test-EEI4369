package com.s23010169.madhuka;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private VideoView backgroundVideo;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        backgroundVideo = findViewById(R.id.backgroundVideo);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        setupVideoBackground();

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save login info into database
            boolean success = databaseHelper.insertData(username, password);
            if (success) {
                Toast.makeText(this, "Login data saved successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save login data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupVideoBackground() {
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.login_background;
        Uri uri = Uri.parse(videoPath);
        backgroundVideo.setVideoURI(uri);
        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f); // Mute
        });
        backgroundVideo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundVideo.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideo.start();
    }
}
