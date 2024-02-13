package com.zack83.EstherChang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.zack83.EstherChang.databinding.ScanActivityBinding


class ScanActivity : AppCompatActivity() {
    private var barcodeResultView: TextView? = null
    var binding: ScanActivityBinding? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.home_activity)
//        barcodeResultView = findViewById(R.id.barcode_result_view)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ScanActivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        startScan()
    }

    private fun bottomDialog(response: String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_dialog, null)
        val btn = view.findViewById<Button>(R.id.idBtnDismiss)
        val changeData = view.findViewById<TextView>(R.id.idTVCourseName)
        val ProgressBar = findViewById<ProgressBar>(R.id.progressBar)

        ProgressBar.visibility = View.GONE
        changeData.text = response
        btn.setOnClickListener {
            val intent: Intent = Intent(
                this@ScanActivity,
                ScanActivity::class.java
            )
            startActivity(intent)
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun startScan() {
        val optionsBuilder = GmsBarcodeScannerOptions.Builder()
        val ProgressBar = findViewById<ProgressBar>(R.id.progressBar)
        val gmsBarcodeScanner = GmsBarcodeScanning.getClient(this, optionsBuilder.build())
        gmsBarcodeScanner
            .startScan()
            .addOnSuccessListener { barcode: Barcode ->
                sendData(barcode.rawValue)
            }
            .addOnFailureListener { e: Exception ->
                barcodeResultView!!.text = getErrorMessage(e)
            }
            .addOnCanceledListener {
                barcodeResultView!!.text = getString(R.string.error_scanner_cancelled)
            }

        ProgressBar.visibility = View.VISIBLE
        }

        private fun sendData(rawValue: String?) {
            var url =
                "https://script.google.com/macros/s/AKfycbz8frvbNMxrV9uMfTJhfe9F5Pypqrgyl-TFLBHYZomI2OTkeklYqOObbX4ldDIK0vaP4w/exec?"
            url = url + "action=get&id=" + rawValue
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
//                    Toast.makeText(this@ScanActivity, response, Toast.LENGTH_SHORT).show()
                    bottomDialog(response)
                }
            ) {}
            val queue = Volley.newRequestQueue(this)
            queue.add(stringRequest)
        }

        private fun getErrorMessage(e: Exception): String? {
            return if (e is MlKitException) {
                when (e.errorCode) {
                    MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED ->
                        getString(R.string.error_camera_permission_not_granted)

                    MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE ->
                        getString(R.string.error_app_name_unavailable)

                    else -> getString(R.string.error_default_message, e)
                }
            } else {
                e.message
            }
        }
    }