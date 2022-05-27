package com.example.ekraproject;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
import static android.bluetooth.BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;
import static android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.IntProperty;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.bluetooth.le.*;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.HttpCookie;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ServerActivity extends AppCompatActivity {

    private UUID SERVICE_UUID = UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");
    private UUID CHARACTERISTIC_COUNTER_UUID = UUID.fromString("31517c58-66bf-470c-b662-e352a6c80cba");
    private UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("0b89d2d4-0ea6-4141-86bb-0c5fb91ab14a");
    private UUID DESCRIPTOR_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");



    private Button vvod;
    private EditText tok;
    private Switch indicat;
    private TextView timer;
    private TextView tconn, iset, i, sost;
    int sss=0;

    private BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private BluetoothGatt mGatt;

    int conditionValue=0;
    int currentCounterValue = 0;


    BluetoothGattServer mGattServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 154, 132)));
        getSupportActionBar().setTitle("Сервер");
        setContentView(R.layout.activity_server);
        checkBluetooth();

        vvod=findViewById(R.id.vvod);
        tok=findViewById(R.id.tok);
        indicat=findViewById(R.id.indicat);
        timer=findViewById(R.id.timer);
        i=findViewById(R.id.i);
        iset=findViewById(R.id.iset);
        tconn=findViewById(R.id.tconn);
        sost=findViewById(R.id.sost);
        vvod.setBackgroundColor(Color.rgb(0, 154, 132));
        vvod.setTextColor(Color.WHITE);


        BluetoothLeAdvertiser mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
        BluetoothManager mBluetoothManager=(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);;
        mGattServer = mBluetoothManager.openGattServer(this, gattServerCallback);
        mGattServer.addService(createService());


        vvod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indicat.isChecked()) {
                    conditionValue=1;
                }
                if (!(tok.getText().toString()).equals("")) {
                    currentCounterValue = Integer.parseInt(tok.getText().toString());
                    String s= String.valueOf(conditionValue);
                    String d=String.valueOf(currentCounterValue);
                    s+=d;
                    currentCounterValue=Integer.parseInt(s);
                }


                notifyRegisteredDevices();
                sost.setText("Состояние индикации = "+ conditionValue);
                i.setText("Напряжение в линии  = " + tok.getText().toString());
                iset.setText("Допустимое напряжение = " +"27000");
                tconn.setText("Интервал рекламы = "+ 5+ " мин.");

            }
        });

    }


    AdvertiseSettings settings = new AdvertiseSettings.Builder()
            .setConnectable(true)
            .build();

    AdvertiseData data = new AdvertiseData.Builder()
            .setIncludeDeviceName(true)
//            .setIncludeTxPowerLevel(true)
            .build();

    AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("TAG", "BLE advertisement added successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e("TAG", "Failed to add BLE advertisement, reason: " + errorCode);
        }
    };

    public void notifyRegisteredDevices() {
        BluetoothGattCharacteristic characteristic = mGattServer
                .getService(SERVICE_UUID)
                .getCharacteristic(CHARACTERISTIC_COUNTER_UUID);
        for (BluetoothDevice device : mRegisteredDevices) {
            byte[] value = BigInteger.valueOf(currentCounterValue).toByteArray();
            counterCharacteristic.setValue(value);
            mGattServer.notifyCharacteristicChanged(device, characteristic, false);
        }
    }
    List<BluetoothDevice> mRegisteredDevices = new ArrayList<>();
    BluetoothGattCharacteristic counterCharacteristic=null;

    BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            if (CHARACTERISTIC_COUNTER_UUID.equals(characteristic.getUuid())) {
                counterCharacteristic = characteristic;
                byte[] value = BigInteger.valueOf(currentCounterValue).toByteArray();
                    Log.i("TAG", "Read counter");
                    Log.i("TAG", String.valueOf(currentCounterValue));
                    mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
            } else{
                Log.w("TAG", "Invalid Characteristic Read: " + characteristic.getUuid());
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId, BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            if (CHARACTERISTIC_INTERACTOR_UUID.equals(characteristic.getUuid())) {
                BigInteger ad=new BigInteger(value);
                Log.i("TAG", characteristic.getValue()+" ");

            }
        }
        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device,
                                             int requestId, BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            if (DESCRIPTOR_CONFIG_UUID.equals(descriptor.getUuid())) {
                if (Arrays.equals(ENABLE_NOTIFICATION_VALUE, value)) {
                    mRegisteredDevices.add(device);
                } else if (Arrays.equals(DISABLE_NOTIFICATION_VALUE, value)) {
                    mRegisteredDevices.remove(device);
                }

                if (responseNeeded) {
                    mGattServer.sendResponse(device, requestId, GATT_SUCCESS, 0, null);
                }
            }
        }



        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("TAG", "BluetoothDevice CONNECTED: " + device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("TAG", "BluetoothDevice DISCONNECTED: " + device);
                mRegisteredDevices.remove(device);
            }
        }

    };
    private BluetoothGattService createService() {
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic counter = new BluetoothGattCharacteristic(CHARACTERISTIC_COUNTER_UUID, PROPERTY_READ | PROPERTY_NOTIFY, PERMISSION_READ|PERMISSION_WRITE);
        BluetoothGattDescriptor counterConfig = new BluetoothGattDescriptor(DESCRIPTOR_CONFIG_UUID, PERMISSION_READ | PERMISSION_WRITE);
        counter.addDescriptor(counterConfig);

        BluetoothGattCharacteristic interactor = new BluetoothGattCharacteristic(CHARACTERISTIC_INTERACTOR_UUID, PROPERTY_WRITE_NO_RESPONSE, PERMISSION_WRITE);
        service.addCharacteristic(counter);
        service.addCharacteristic(interactor);
        return service;
    }

    private void checkBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void timeAd(int time){
        time*=60000;
        new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timer.setText( f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                timer.setText("00:00");
            }
        }.start();
    }
}