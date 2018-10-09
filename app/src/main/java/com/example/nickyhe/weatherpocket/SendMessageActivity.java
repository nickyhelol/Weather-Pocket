package com.example.nickyhe.weatherpocket;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageActivity extends AppCompatActivity {

    EditText phoneText;
    TextView messageText;
    Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        if(!checkPermission(Manifest.permission.SEND_SMS))
        {
             ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1);
        }

        phoneText = findViewById(R.id.phoneEditText);
        messageText = findViewById(R.id.messageTextView);
        sendBtn = findViewById(R.id.sendBtn);

        Intent intent = getIntent();
        final String msg = intent.getStringExtra("weather");

        messageText.setText(msg);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneText.getText().toString();
                String text = msg;
                if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(text))
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, text, null, null);
                    Toast.makeText(SendMessageActivity.this, "Sent!", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(SendMessageActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean checkPermission(String permission)
    {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);

        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPermission(Manifest.permission.SEND_SMS)){
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }
        }
    }
}
