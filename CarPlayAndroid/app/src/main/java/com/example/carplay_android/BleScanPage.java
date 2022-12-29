package com.example.carplay_android;

import static com.example.carplay_android.ScanBleDeviceUtils.scanLeDevice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public class BleScanPage extends AppCompatActivity {

    private Button buttonScan;
    private Button buttonConnect;
    private ListView bleList;
    private TextView deviceName;
    private TextView deviceAddress;

    private List<BleDevice> resultList;
    private int selected = -1;
    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan_page);

        bleList.setAdapter(leDeviceListAdapter);
        bleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.addressForSingle);
                if(textView.getText() != deviceAddress.getText()){
                    deviceAddress.setText(textView.getText());
                    textView = view.findViewById(R.id.nameForSingle);
                    deviceName.setText(textView.getText());
                    selected = i;
                }
                connectLeDevice(selected);
            }
        });


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultList = scanLeDevice(getApplicationContext());
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectLeDevice(selected);
            }
        });
    }

    private void initial(){
        buttonScan = findViewById(R.id.buttonScan);
        buttonConnect = findViewById(R.id.buttonConnect);
        bleList = findViewById(R.id.deviceList);
        deviceAddress = findViewById(R.id.deviceAddress);
        deviceName = findViewById(R.id.deviceName);
    }
}

