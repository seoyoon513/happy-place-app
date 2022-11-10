package com.syoon.toy.happyplaceapp.activities

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.syoon.toy.happyplaceapp.R
import com.syoon.toy.happyplaceapp.database.DatabaseHandler
import com.syoon.toy.happyplaceapp.models.HappyPlaceModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity() {

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        val tbrAddPlace = findViewById<Toolbar>(R.id.tbr_add_place)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val edtTitle = findViewById<EditText>(R.id.edt_title)
        val edtDescription = findViewById<EditText>(R.id.edt_description)
        val edtDate = findViewById<EditText>(R.id.edt_date)
        val edtLocation = findViewById<EditText>(R.id.edt_location)

        setSupportActionBar(tbrAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // toolbar 뒤로가기
        tbrAddPlace.setNavigationOnClickListener {
            onBackPressed()
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
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"  // 휴대폰 내 이미지 저장할 폴더
    }
}