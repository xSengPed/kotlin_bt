package com.exvention.kotlin_bt_service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.util.UUID


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bluetoothService: BluetoothService = BluetoothService()


        var scanButton: Button = findViewById<Button>(R.id.scan_btn)
        scanButton.setOnClickListener {
            bluetoothService.scanLeDevice(true)
        }

        var connectButton: Button = findViewById<Button>(R.id.connect_btn)
        connectButton.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val deviceAddress = "40:4C:CA:4A:C9:42"
            var device: BluetoothDevice? = null

            if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
                device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            }

            if (device != null) {
                bluetoothService.connectToDevice(this, device)
            }
        }

        var sendButton: Button = findViewById<Button>(R.id.send_btn)
        sendButton.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val deviceAddress = "40:4C:CA:4A:C9:42"
            var device: BluetoothDevice? = null

            if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
                device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            }
            Log.d("BTN", "ON SEND")

            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val state = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT)
            Log.d("state", "$state")
            if (state == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BT", "CONNECTED")
            }
            Log.d("BTN", "Sending Characteristic")
            bluetoothService.sendCharacteristic("TESTSEND".toByteArray())


        }

    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb")
        val DATA: ByteArray = "TEST_DATA".toByteArray()
    }
}