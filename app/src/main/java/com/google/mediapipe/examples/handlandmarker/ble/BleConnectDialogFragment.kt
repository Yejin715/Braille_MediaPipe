package com.google.mediapipe.examples.handlandmarker.ble

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.handlandmarker.databinding.BleConnectBinding
import com.google.mediapipe.examples.handlandmarker.R
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.scan.ScanResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.app.Activity
import android.widget.Button
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.DialogInterface
import androidx.activity.result.ActivityResultLauncher

class BleConnectDialogFragment(private val bleViewModel: BleViewModel) : DialogFragment() {

    private var adapter: BleListAdapter? = null
    private var requestEnableBluetooth = false

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val PERMISSIONS_S_ABOVE = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 요청 런처 초기화
        requestPermissionsLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permissions must be granted", Toast.LENGTH_SHORT).show()
                requestPermissionsAgain()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<BleConnectBinding>(
            inflater,
            R.layout.ble_connect,
            container,
            false
        )
        binding.viewModel = bleViewModel

        binding.rvBleList.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvBleList.layoutManager = layoutManager

        adapter = BleListAdapter()
        binding.rvBleList.adapter = adapter
        adapter?.setItemClickListener(object : BleListAdapter.ItemClickListener {
            override fun onClick(view: View, scanResult: ScanResult?) {
                val device = scanResult?.bleDevice
                if (device != null) {
                    bleViewModel.connectDevice(device)
                }
            }
        })

        // 안드로이드 버전에 따른 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions(requireContext(), PERMISSIONS_S_ABOVE)) {
                requestPermissionsLauncher.launch(PERMISSIONS_S_ABOVE)
            }
        } else {
            if (!hasPermissions(requireContext(), PERMISSIONS)) {
                requestPermissionsLauncher.launch(PERMISSIONS)
            }
        }

        //  val btnSend = binding.root.findViewById<Button>(R.id.btn_send)
        //  btnSend.setOnClickListener {
        //         onClickWrite(it)
        //     }

        initObserver(binding)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.setOnShowListener {
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            dialog.window?.setLayout(width, height)
        }
        return dialog
    }

    private fun initObserver(binding: BleConnectBinding) {
        bleViewModel.apply {
            bleException.observe(viewLifecycleOwner, Observer {
                it.getContentIfNotHandled()?.let { reason ->
                    bleViewModel.stopScan()
                    bleThrowable(reason)
                }
            })

            listUpdate.observe(viewLifecycleOwner, Observer {
                it.getContentIfNotHandled()?.let { scanResults ->
                    adapter?.setItem(scanResults)
                }
            })
        }
    }

    private fun bleThrowable(reason: Int) {
        when (reason) {
            BleScanException.BLUETOOTH_DISABLED -> {
                requestEnableBluetooth = true
                requestEnableBLE()
            }
            BleScanException.LOCATION_PERMISSION_MISSING -> {
                requestPermissionsLauncher.launch(PERMISSIONS)
            }
            else -> {
                Util.showNotification(bleScanExceptionReasonDescription(reason))
            }
        }
    }

    private fun bleScanExceptionReasonDescription(reason: Int): String {
        return when (reason) {
            BleScanException.BLUETOOTH_CANNOT_START -> "Bluetooth cannot start"
            BleScanException.BLUETOOTH_DISABLED -> "Bluetooth disabled"
            BleScanException.BLUETOOTH_NOT_AVAILABLE -> "Bluetooth not available"
            BleScanException.LOCATION_SERVICES_DISABLED -> "Location Services disabled"
            BleScanException.SCAN_FAILED_ALREADY_STARTED -> "Scan failed because it has already started"
            BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Scan failed because application registration failed"
            BleScanException.SCAN_FAILED_INTERNAL_ERROR -> "Scan failed because of an internal error"
            BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED -> "Scan failed because feature unsupported"
            BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "Scan failed because out of hardware resources"
            BleScanException.UNDOCUMENTED_SCAN_THROTTLE -> "Undocumented scan throttle"
            BleScanException.UNKNOWN_ERROR_CODE -> "Unknown error"
            else -> "Unknown error"
        }
    }

    override fun onResume() {
        super.onResume()
        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            dismiss()
        }
    }

    fun onClickWrite(view: View) {
        val writeDialog = WriteDialog(requireContext(), object : WriteDialog.WriteDialogListener {
            override fun onClickSend(data: String, type: String) {
                bleViewModel.writeData(data, type)
            }
        })
        writeDialog.show()
    }

    private val requestEnableBleResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Util.showNotification("Bluetooth 기능을 허용하였습니다.")
                bleViewModel.startScan()
            } else {
                Util.showNotification("Bluetooth 기능을 켜주세요.")
                bleViewModel.stopScan()
            }
            requestEnableBluetooth = false
        }

    /**
     * BLE 활성화 요청
     */
    private fun requestEnableBLE() {
        val bleEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestEnableBleResult.launch(bleEnableIntent)
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, permission) }
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissionsAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionsLauncher.launch(PERMISSIONS_S_ABOVE)
        } else {
            requestPermissionsLauncher.launch(PERMISSIONS)
        }
    }
}
