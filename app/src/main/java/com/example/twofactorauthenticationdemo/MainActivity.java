package com.example.twofactorauthenticationdemo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText otpInput;
    private Button sendOtpButton;
    private Button verifyOtpButton;
    private TextView statusText;

    private String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.emailInput);
        otpInput = findViewById(R.id.otpInput);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        statusText = findViewById(R.id.statusText);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (isValidEmail(email)) {
                    sendOtp(email);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = otpInput.getText().toString().trim();
                verifyOtp(enteredOtp);
            }
        });

        otpInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyOtpButton.setEnabled(s.length() == 6);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendOtp(String email) {
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }


        generatedOtp = String.format("%06d", new Random().nextInt(999999));


        String subject = "Your OTP Code";
        String message = "Your OTP is: " + generatedOtp;


        new Thread(() -> {
            try {
                GmailSender sender = new GmailSender("dielli.doci@gmail.com", "uhto majn lqrx tage");
                sender.sendEmail(email, subject, message);

                runOnUiThread(() -> {
                    Toast.makeText(this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();
                    statusText.setText("OTP sent. Please check your email.");
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }



    private void verifyOtp(String enteredOtp) {
        if (enteredOtp.equals(generatedOtp)) {
            statusText.setText("Verification successful!");
        } else {
            statusText.setText("Invalid OTP. Please try again.");
        }
    }
}
