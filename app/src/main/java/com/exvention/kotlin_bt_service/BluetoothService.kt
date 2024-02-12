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
import android.content.Context
import android.util.Log
import java.util.UUID


class BluetoothService {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val devices = mutableListOf<BluetoothDevice>()
 init {
     Log.d("Init BluetoothService", "")
     bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
     bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
 }
    @SuppressLint("MissingPermission")
    fun scanLeDevice(enable: Boolean) {
        if (enable) {
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (!devices.contains(result.device)) {
                devices.add(result.device)
                Log.d("ScanCallback", "onScanResult: ${result.device}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(context: Context, device: BluetoothDevice) {
        device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                // Here you can get the services and characteristics for the device
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun sendCharacteristic(
        gatt: BluetoothGatt,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        value: ByteArray
    ) {
        val service = gatt.getService(serviceUUID)
        val characteristic = service.getCharacteristic(characteristicUUID)
        characteristic.value = value
        gatt.writeCharacteristic(characteristic)
    }

}