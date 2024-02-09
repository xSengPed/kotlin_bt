package com.exvention.kotlin_bt_service
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID


class BleManager(context: Context) {
    private val TAG = "BleManager"
    private val SERVICE_UUID = UUID.fromString(UUID.randomUUID().toString())
    private val CHARACTERISTIC_UUID = UUID.fromString(UUID.randomUUID().toString())
    private val TARGET_DEVICE_ADDRESS = "40:4C:CA:4A:C9:42"

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt?.discoverServices()
            }
        }


        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            val service = gatt?.getService(SERVICE_UUID)
            val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)
            characteristic?.value = "your data".toByteArray()


            gatt?.writeCharacteristic(characteristic)


            Log.d(TAG, "onServicesDiscovered: $service")
        }
    }

  @SuppressLint("MissingPermission")
  fun sendCharacteristic() {
    val service = bluetoothGatt?.getService(SERVICE_UUID)
//      Log.d(TAG, "sendCharacteristic s: $service")
    val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)
//      Log.d(TAG, "sendCharacteristic c: $characteristic")
    characteristic?.value = "your data test".toByteArray()
//      Log.d(TAG, "sendCharacteristic v: ${characteristic?.value}")
    bluetoothGatt?.writeCharacteristic(characteristic)
//      Log.d(TAG, "sendCharacteristic w: ${characteristic?.value}")
  }
    @SuppressLint("MissingPermission")
    fun connect(context: Context) {
        val device = bluetoothAdapter?.getRemoteDevice(TARGET_DEVICE_ADDRESS)
        bluetoothGatt = device?.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }
}
class MainActivity : AppCompatActivity() {

    private val REQUEST_ACCESS_FINE_LOCATION = 1
    private val REQUEST_ACCESS_BLUETOOTH_CONNECT = 1
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                } else {
                    // Permission denied
                }
                return
            }
            REQUEST_ACCESS_BLUETOOTH_CONNECT -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                } else {
                    // Permission denied
                }
                return
            }
            else -> {
                // Ignore all other requests
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val macAddr : String = "40:4C:CA:4A:C9:42"

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission Location", "Permission not granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
        } else {
            Log.d("Permission Location", "Permission granted")
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        Log.d("Permission Bluetooth", "Permission not granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ACCESS_BLUETOOTH_CONNECT)
        } else {
            Log.d("Permission Bluetooth", "Permission granted")

        }

        var connectBtn : Button = findViewById(R.id.connect_bnt)
        connectBtn.setOnClickListener {

            val bleManager = BleManager(this)
            bleManager.connect(this)
        }

        var sendBtn : Button = findViewById(R.id.sending_test)
        sendBtn.setOnClickListener {
            val bleManager = BleManager(this)
            bleManager.sendCharacteristic()
        }
//        handler = Handler(Looper.getMainLooper())
//        listView = findViewById(R.id.listView)
//        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
//        listView.adapter = listAdapter
//
//        val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
//
//        scanLeDevice(true)

    }


}