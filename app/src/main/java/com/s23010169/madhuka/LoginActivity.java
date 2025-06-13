package com.s23010169.madhuka;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;
import android.media.MediaPlayer;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private VideoView backgroundVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        backgroundVideo = findViewById(R.id.backgroundVideo);

        // Setup video background
        setupVideoBackground();

        // Set click listener for login button
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Implement actual login logic here
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupVideoBackground() {
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.login_background;
        Uri uri = Uri.parse(videoPath);
        backgroundVideo.setVideoURI(uri);
        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
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