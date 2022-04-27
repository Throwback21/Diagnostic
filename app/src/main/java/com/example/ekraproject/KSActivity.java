package com.example.ekraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class KSActivity extends AppCompatActivity {
    private Button button;
    private TextView nameDevice, iSet, condition;
    private EditText editText, showAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                (Color.rgb(0, 154, 132)));
        getSupportActionBar().setTitle("Клиент");
        setContentView(R.layout.activity_ksactivity);
        button=findViewById(R.id.button2);
        nameDevice=findViewById(R.id.nameDevice);
        iSet=findViewById(R.id.condition);
        editText=findViewById(R.id.editText);
        condition=findViewById(R.id.condition);
        showAdd=findViewById(R.id.showAdd);
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("nameDevice");
        nameDevice.setText(deviceName);




        condition.setText("Состояние");
        button.setBackgroundColor(Color.WHITE);
    }
}