package com.gelostech.bloggerappkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterPage(
    var context: Context,
    var pagerArrayList: ArrayList<ModelPage>
) : RecyclerView.Adapter<AdapterPage.HolderPage>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPage {
        val view = LayoutInflater.from(context).inflate(R.layout.row_page, parent, false)
        return HolderPage(view)

    }

    override fun getItemCount(): Int {
        return pagerArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPage, position: Int) {
        val model = pagerArrayList[position]

        val authorName = model.authorName
        val content = model.content
        val id = model.id
        val published = model.published
        val selfLink = model.selfLink
        val title = model.title
        val updated = model.updated
        val url = model.url


        val document = Jsoup.parse(content)
        try {
            val elements = document.select("img")
            val image = elements[0].attr("src")

            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv)
        } catch (e: Exeption) {
            e.printStackTrace()
        }

        holder.titleTv.text = title
        holder.descriptionTv.text = document.text()
        holder.publishInfoTv.text = "By $authorName $formattedDate"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PageDetailsActivity::class.java)
            intent.putExtra("pageId", id)
            content.starActivity(intent)
        }

    }

    inner class HolderPage(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val publishInfoTv: TextView = itemView.findViewById(R.id.publishInfoTv)
        val imageTv: TextView = itemView.findViewById(R.id.imageTv)
        val descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)
    }
}