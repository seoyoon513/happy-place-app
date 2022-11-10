package com.syoon.toy.happyplaceapp

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        val tbrAddPlace = findViewById<Toolbar>(R.id.tbr_add_place)

        setSupportActionBar(tbrAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // toolbar 뒤로가기
        tbrAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // 기기에 사진 저장
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE) // Directory 생성
        file = File(file,"${UUID.randomUUID()}.jpg" ) // Directory 에 들어갈 파일 생성
        // "${UUID.randomUUID()}.jpg" : 무작위 고유 식별자 ID, jpg 형식으로 저장

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) // 압축
            stream.flush()
            stream.close()
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath) // 파일 경로를 uri로 제공
    }

    companion object {
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"  // 휴대폰 내 이미지 저장할 폴더
    }
}