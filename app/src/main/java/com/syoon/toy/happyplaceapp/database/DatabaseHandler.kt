package com.syoon.toy.happyplaceapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.syoon.toy.happyplaceapp.models.HappyPlaceModel

class DatabaseHandler(context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "HappyPlacesTable"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable" // Table name

        // All the Columns name -> 데이터 객체를 생성할 때마다 데이터베이스 안에 생성
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "_title"
        private const val KEY_IMAGE = "_image"
        private const val KEY_DESCRIPTION = "_description"
        private const val KEY_DATE = "_date"
        private const val KEY_LOCATION = "_location"
        private const val KEY_LATITUDE = "_latitude"
        private const val KEY_LONGITUDE = "_longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + "TEXT,"
                + KEY_IMAGE + "TEXT,"
                + KEY_DESCRIPTION + "TEXT,"
                + KEY_DATE + "TEXT,"
                + KEY_LOCATION + "TEXT,"
                + KEY_LATITUDE + "TEXT,"
                + KEY_LONGITUDE + "TEXT)"
                )
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    // 이미 존재하는 표가 있다면 onCreate 메서드로 새로운 표 생성
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!. execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase // 쓰기 가능한 db -> 정보를 넣을 수 있음

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        // Inserting Row (저장) - long 타입으로 반환
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

        db.close()
        return result
    }

}