package com.example.robotskaruka;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //99% ID za sve HC-05 elemente
    private static final String DEVICE_ADDRESS = "00:23:09:01:80:22"; // MAC adresa vašeg HC-05

    private TextView statusText;
    private Button connectBtn;
    private Button btnUp, btnDown, btnLeft, btnRight, btnGrab, btnRelease,btnElbowUp, btnElbowDown;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 

        statusText = findViewById(R.id.statusText);
        connectBtn = findViewById(R.id.connectBtn);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnGrab = findViewById(R.id.btnGrab);
        btnRelease = findViewById(R.id.btnRelease);
        btnElbowUp = findViewById(R.id.btnElbowUp);
        btnElbowDown = findViewById(R.id.btnElbowDown);

        //Zahtjevi za dozvole
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1);
        }

        connectBtn.setOnClickListener(view -> connectToBluetooth());

        //Kontrole tipkih na dugmadima
        btnUp.setOnClickListener(view -> sendData("U"));       // U = Gore
        btnDown.setOnClickListener(view -> sendData("D"));     // D = Dole
        btnLeft.setOnClickListener(view -> sendData("L"));     // L = Lijevo
        btnRight.setOnClickListener(view -> sendData("R"));    // R = Desno
        btnGrab.setOnClickListener(view -> sendData("G"));     // G = Hvatanje
        btnRelease.setOnClickListener(view -> sendData("S"));  // S = Puštanje
        btnElbowUp.setOnClickListener(view -> sendData("E"));   // E = savij lakat
        btnElbowDown.setOnClickListener(view -> sendData("B")); // B = ispruži lakat
    }

    private void connectToBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return; 
              //izbacuje error jer traži od korisnika PERMISSION CHECK
            }
            startActivityForResult(enableBtIntent, 0);
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            isConnected = true;
            statusText.setText("Status: Povezano");
            Toast.makeText(this, "Uspješno povezano!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            statusText.setText("Status: Greška prilikom povezivanja");
            Toast.makeText(this, "Greška prilikom povezivanja", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendData(String command) {
        if (isConnected && outputStream != null) {
            try {
                outputStream.write(command.getBytes());
                Toast.makeText(this, " " + command, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Greška pri slanju podataka", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Nema veze s uređajem!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Dozvole su odobrene!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Dozvole nisu odobrene!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
