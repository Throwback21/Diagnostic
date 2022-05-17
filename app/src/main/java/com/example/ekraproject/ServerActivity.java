package com.example.ekraproject;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.bluetooth.le.*;

import java.util.List;
import java.util.UUID;


public class ServerActivity extends AppCompatActivity {
    private Button vvod;
    private EditText tok;
    private Switch indicat;
    private TextView timer;
    private TextView tconn, iset, i, sost;
    int sss=0;
    private final String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";


    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private BluetoothGatt mGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 154, 132)));
        getSupportActionBar().setTitle("Сервер");
        setContentView(R.layout.activity_server);
        vvod=findViewById(R.id.vvod);
        tok=findViewById(R.id.tok);
        indicat=findViewById(R.id.indicat);
        timer=findViewById(R.id.timer);
        i=findViewById(R.id.i);
        iset=findViewById(R.id.iset);
        tconn=findViewById(R.id.tconn);
        sost=findViewById(R.id.sost);
        timer.setText("04:28");
        vvod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indicat.isChecked()) sss=1;
                sost.setText("Состояние индикации = "+ sss);
                i.setText("Ток в линии  = " + tok.getText().toString());
                iset.setText("Допустимый ток = " +"27000");
                tconn.setText("Интервал рекламы = "+ 5+ " мин.");

            }
        });
    }



    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {


    };

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mGatt == null) return null;
        return mGatt.getServices();
    }
}