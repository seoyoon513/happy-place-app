package com.syoon.toy.happyplaceapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syoon.toy.happyplaceapp.R
import com.syoon.toy.happyplaceapp.adapters.HappyPlacesAdapter
import com.syoon.toy.happyplaceapp.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    private var rvHappyPlacesList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAddHappyPlace = findViewById<View>(R.id.fab_add_happy_place)
        rvHappyPlacesList = findViewById(R.id.rv_happy_places_list)

        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>) {
        rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList?.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        rvHappyPlacesList?.adapter = placesAdapter

    }
}