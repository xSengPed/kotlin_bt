package com.exvention.kotlin_bt_service
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    private val SCAN_PERIOD: Long = 10000
    private var mScanning: Boolean = false
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var handler: Handler
    private lateinit var listView: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private val deviceList = ArrayList<String>()

    @SuppressLint("MissingPermission")
    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                handler.postDelayed({
                    mScanning = false
                    bluetoothLeScanner.stopScan(leScanCallback)
                }, SCAN_PERIOD)

                mScanning = true
                bluetoothLeScanner.startScan(leScanCallback)
            }
            else -> {
                mScanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }
        }
    }
    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            addDevice(result.device)
        }
    }
    @SuppressLint("MissingPermission")
    private fun addDevice(device: BluetoothDevice) {
        val deviceName = device.name ?: "Unknown Device"
        val deviceAddress = device.address
        val deviceString = "$deviceName - $deviceAddress"
        if (!deviceList.contains(deviceString)) {
            deviceList.add(deviceString)
            listAdapter.notifyDataSetChanged()
        }
    }
    private lateinit var bluetoothGatt: BluetoothGatt

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt.close()
                }
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(Looper.getMainLooper())
        listView = findViewById(R.id.listView)
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView.adapter = listAdapter

        val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        scanLeDevice(true)

    }


}