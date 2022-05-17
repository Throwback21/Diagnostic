package com.example.ekraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class KSActivity extends AppCompatActivity {
    private Button button;
    private TextView nameDevice, iSet, condition;
    private EditText maxVal, showAdd;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothGatt mGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                (Color.rgb(0, 154, 132)));
        getSupportActionBar().setTitle("Клиент");
        setContentView(R.layout.activity_ksactivity);
        button = findViewById(R.id.button2);
        nameDevice = findViewById(R.id.nameDevice);
        iSet = findViewById(R.id.condition);
        maxVal = findViewById(R.id.editText);
        condition = findViewById(R.id.condition);
        showAdd = findViewById(R.id.showAdd);
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("nameDevice");
        nameDevice.setText(deviceName);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        condition.setText("Состояние");
        button.setBackgroundColor(Color.WHITE);
        BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(deviceName);


    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        String uuid="00001101-0000-1000-8000-00805F9B34FB";

    };

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mGatt == null) return null;
        return mGatt.getServices();
    }
}