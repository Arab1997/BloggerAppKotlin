package com.gelostech.bloggerappkotlin.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gelostech.bloggerappkotlin.model.ModelLabel
import com.gelostech.bloggerappkotlin.R

class AdapterLabel(
    var context: Context,
    var labelArrayList:ArrayList<ModelLabel>
): RecyclerView.Adapter<AdapterLabel.HolderLabel>()

{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLabel {
        val view = LayoutInflater.from(context).inflate(R.layout.row_label, parent, false)
        return HolderLabel(view)
    }

    override fun getItemCount(): Int {
        return labelArrayList.size  // return number of recordlists
    }

    override fun onBindViewHolder(holder: HolderLabel, position: Int) {
        val modelLabel = labelArrayList[position]

        val label  = modelLabel.label

        // set data
        holder.labelTv.text = label
    }


    inner class HolderLabel(itemView: View) : RecyclerView.ViewHolder(itemView){

        //init  UI  Views of row_label.xml
        var labelTv:TextView = itemView.findViewById(R.id.labelTv)
    }

}