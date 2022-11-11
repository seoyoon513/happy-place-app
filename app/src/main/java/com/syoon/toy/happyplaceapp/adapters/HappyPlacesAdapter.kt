package com.syoon.toy.happyplaceapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.syoon.toy.happyplaceapp.R
import com.syoon.toy.happyplaceapp.models.HappyPlaceModel

// 상속 받을 수 있는 open class
open class HappyPlacesAdapter(
    private val context: Context,
    private val list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<ImageView>(R.id.iv_place_image).setImageURI(Uri.parse(model.image))
            holder.itemView.findViewById<TextView>(R.id.tv_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_description).text = model.description
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}
