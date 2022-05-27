package com.example.ekraproject;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE;
import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterButton;

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
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class KSActivity extends AppCompatActivity {
    private Button button;
    private ImageFilterButton update;
    private TextView nameDevice, iSet, condition;
    private EditText maxVal, showAdd;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mGatt;
    private UUID SERVICE_UUID = UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");
    private UUID CHARACTERISTIC_COUNTER_UUID = UUID.fromString("31517c58-66bf-470c-b662-e352a6c80cba");
    private UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("0b89d2d4-0ea6-4141-86bb-0c5fb91ab14a");
    private UUID DESCRIPTOR_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    int adView=1;
    int maxValue=1;

    int indicat=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 154, 132)));
        getSupportActionBar().setTitle("Клиент");
        setContentView(R.layout.activity_ksactivity);
        button = findViewById(R.id.button2);
        nameDevice = findViewById(R.id.nameDevice);
        update = findViewById(R.id.update);
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

        BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(deviceName);
        Toast.makeText(getBaseContext(), deviceName, Toast.LENGTH_SHORT).show();
        mGatt = device.connectGatt(this, false, gattCallback);

        if (!(showAdd.getText().toString()).equals("")) {
            adView = Integer.parseInt(showAdd.getText().toString());
        }


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGatt.close();
                mGatt = device.connectGatt(getBaseContext(), false, gattCallback);
                if (!(maxVal.getText().toString()).equals("")){
                        int val = Integer.parseInt(maxVal.getText().toString());
                    byte[] value=BigInteger.valueOf(val).toByteArray();
                    BluetoothGattCharacteristic characteristic=mGatt.getService((SERVICE_UUID))
                            .getCharacteristic(CHARACTERISTIC_INTERACTOR_UUID);
                    characteristic.setValue(value);
                    mGatt.writeCharacteristic();
                }

            }
        });
    }



    public  void writeInteractorCharacteristic(int value){
        BluetoothGattCharacteristic characteristic= mGatt.getService
                (SERVICE_UUID).getCharacteristic(CHARACTERISTIC_INTERACTOR_UUID);
        byte[] val=BigInteger.valueOf(value).toByteArray();
        characteristic.setValue(val);
        mGatt.writeCharacteristic(characteristic);
    }




    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(KSActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i("TAG", "Connected to GATT client. Attempting to start service discovery");
                gatt.discoverServices();
                readCounterCharacteristic(gatt.getService((SERVICE_UUID)).getCharacteristic(CHARACTERISTIC_COUNTER_UUID));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("TAG", "Disconnected from GATT client");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }

            BluetoothGattCharacteristic characteristic = gatt
                    .getService(SERVICE_UUID)
                    .getCharacteristic(CHARACTERISTIC_COUNTER_UUID);

            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor =
                    characteristic.getDescriptor(DESCRIPTOR_CONFIG_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            if (DESCRIPTOR_CONFIG_UUID.equals(descriptor.getUuid())) {
                BluetoothGattCharacteristic characteristic = gatt
                        .getService(SERVICE_UUID)
                        .getCharacteristic(CHARACTERISTIC_COUNTER_UUID);
                gatt.readCharacteristic(characteristic);
            }
        }



        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            readCounterCharacteristic(characteristic);
        }

        private void readCounterCharacteristic(BluetoothGattCharacteristic
                                                       characteristic) {
            if (CHARACTERISTIC_COUNTER_UUID.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                BigInteger value = new BigInteger(data);
                String s= value.toString();
                int cond=Integer.parseInt(s.substring(s.length()-1, s.length()));
                int val=Integer.parseInt(s.substring(0, s.length()-1));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iSet.setText("U = " +val+ " В");
                        if (cond==1){
                            button.setBackgroundColor(Color.RED);
                        }
                        if (cond==0){
                            button.setBackgroundColor(Color.GREEN);
                        }

                    }
                });
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            readCounterCharacteristic(characteristic);
        }
    };

}