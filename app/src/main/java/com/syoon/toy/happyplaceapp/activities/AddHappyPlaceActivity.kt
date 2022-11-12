package com.syoon.toy.happyplaceapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.syoon.toy.happyplaceapp.R
import com.syoon.toy.happyplaceapp.database.DatabaseHandler
import com.syoon.toy.happyplaceapp.models.HappyPlaceModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity() {

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var placeImage : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        val tbrAddPlace = findViewById<Toolbar>(R.id.tbr_add_place)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val edtTitle = findViewById<EditText>(R.id.edt_title)
        val edtDescription = findViewById<EditText>(R.id.edt_description)
        val edtDate = findViewById<EditText>(R.id.edt_date)
        val edtLocation = findViewById<EditText>(R.id.edt_location)
        var tvAddImage = findViewById<TextView>(R.id.tv_add_image)
        placeImage = findViewById(R.id.iv_place_image)


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

        edtDate.setOnClickListener {
            DatePickerDialog(this@AddHappyPlaceActivity, dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        tvAddImage.setOnClickListener{
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select photo from Gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                when(which){
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }

        btnSave.setOnClickListener {
            when {
                edtTitle.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                }
                edtDescription.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
                }
                edtLocation.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
                }
                saveImageToInternalStorage == null -> {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val happyPlaceModel = HappyPlaceModel(
                        0,
                        edtTitle.text.toString(),
                        saveImageToInternalStorage.toString(),
                        edtDescription.text.toString(),
                        edtDate.text.toString(),
                        edtLocation.text.toString(),
                        mLatitude,
                        mLongitude
                    )
                    val dbHandler = DatabaseHandler(this)
                    val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                    if(addHappyPlace > 0) {
                        Toast.makeText(
                            this,
                            "The happy place details are inserted successfully.",
                        Toast.LENGTH_SHORT
                        ).show()
                        finish() // Activity 활동 끝내기 -> MainActivity로 이동
                    }
                }

            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == AddHappyPlaceActivity.GALLERY){
                if(data != null){
                    val contentURI = data.data
                    try{
                        //getBitmap() : 곧 사라질 기능인데 아직 대안이 없다고 함.
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        placeImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed to load the Image from Gallery!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else if(requestCode == AddHappyPlaceActivity.CAMERA){
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
                placeImage?.setImageBitmap(thumbnail)
            }
        }
    }

    private fun takePhotoFromCamera(){
        //.withActivity : Deprecated -> .withContext
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent, AddHappyPlaceActivity.CAMERA)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    // 갤러리 권한 요청 (복수개)
    private fun choosePhotoFromGallery() {
        //.withActivity : Deprecated -> .withContext
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, AddHappyPlaceActivity.GALLERY)
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

    // 기기에 사진 저장
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE) // Directory 생성
        file = File(file, "${UUID.randomUUID()}.jpg") // Directory 에 들어갈 파일 생성
        // "${UUID.randomUUID()}.jpg" : 무작위 고유 식별자 ID, jpg 형식으로 저장

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) // 압축
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath) // 파일 경로를 uri로 제공
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"  // 휴대폰 내 이미지 저장할 폴더
    }
}