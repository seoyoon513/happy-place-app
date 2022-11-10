package com.syoon.toy.happyplaceapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        val tbrAddPlace = findViewById<Toolbar>(R.id.tbr_add_place)
        var edtDate = findViewById<EditText>(R.id.edt_date)
        var tvAddImage = findViewById<TextView>(R.id.tv_add_image)

        setSupportActionBar(tbrAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // toolbar 뒤로가기
        tbrAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        edtDate.setOnClickListener(this)
        tvAddImage.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.edt_date -> {
                DatePickerDialog(this@AddHappyPlaceActivity, dateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                        _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this@AddHappyPlaceActivity, "Camera selection coming soon...", Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDialog.show()

            }
        }
    }

    // 갤러리 권한 요청 (복수개)
    private fun choosePhotoFromGallery() {
        //.withActivity : Deprecated
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHappyPlaceActivity,
                    "Storage READ/WRITE permission are granted. Now you can select an image from GALLERY", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
            }).onSameThread().check()
    }

    //권한 거절 되었을 경우
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permission required for this feature."
                    +"It can be enabled under the Applications Settings.")
            .setPositiveButton("GO TO SETTINGS"){
                        _, _ ->
                            try {
                                //앱 설정 화면으로 넘어가서 사용자 권한 바꿀 수 있게 함.
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }catch(e: ActivityNotFoundException){
                                e.printStackTrace()
                            }
            }
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault()) // 휴대폰 위치따라 현지 기본 날짜 포맷 사용
        findViewById<EditText>(R.id.edt_date).setText(sdf.format(cal.time).toString()) // 캘린더 상의 시간 사용
    }

}