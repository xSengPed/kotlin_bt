package com.exvention.kotlin_bt_service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import java.util.UUID


class BluetoothService {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var bluetoothGatt: BluetoothGatt
    private val devices = mutableListOf<BluetoothDevice>()
    private lateinit var SERVICE_UUID: UUID
    private lateinit var CHARACTERISTIC_UUID: UUID

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
    fun connectToDevice(context: Context, device: BluetoothDevice) {
        device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("onConnectionStateChange", "Connected to ${device.address}")
                    bluetoothGatt = gatt
                    val result = gatt.discoverServices()
                    Log.d("discoverServices", "result: $result")
                }
            }
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val targetChannel : Int = 26
                Log.d("Service", "${gatt.services.size} services discovered.")
                gatt.services.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        Log.d("onServicesDiscovered", "service: ${service.uuid}")
                        Log.d("onServicesDiscovered", "characteristic: ${characteristic.uuid}")
                    }
                }
                gatt.services.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        if (characteristic.properties == targetChannel) {
                            setWritableChannel(service.uuid, characteristic.uuid)
                            return
                        }
                    }
                }
            }
        })
    }

    private fun setWritableChannel(serviceUUID: UUID, characteristicUUID: UUID) {
        Log.d("setWritableChannel", "serviceUUID: $serviceUUID | $characteristicUUID")
        SERVICE_UUID = serviceUUID
        CHARACTERISTIC_UUID = characteristicUUID
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission", "WrongConstant")
    fun sendCharacteristic(
        value: ByteArray
    ) {

//        Log.d("sendCharacteristic", "value: $value")
//        Log.d("sendCharacteristic", "serviceUUID: $SERVICE_UUID")
//        Log.d("sendCharacteristic", "characteristicUUID: $CHARACTERISTIC_UUID")
//        Log.d("sendCharacteristic", "service count : ${bluetoothGatt.services.size}")
//        Log.d(
//            "sendCharacteristic",
//            "char count : ${bluetoothGatt.services[0].characteristics.size}"
//        )

        val service = bluetoothGatt.getService(SERVICE_UUID)
        val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID)

        if (characteristic != null) {

            Log.d("sendCharacteristic", "characteristic prop: ${characteristic.properties}")
            characteristic.setValue(value);
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            var result = bluetoothGatt.writeCharacteristic(characteristic)
            Log.d("sendCharacteristic", "result: $result")
        }

    }

}