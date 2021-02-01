package com.gelostech.bloggerappkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

class AdapterPage(
    var context: Context,
    var pagerArrayList: ArrayList<ModelPost>
) : RecyclerView.Adapter<AdapterPage.HolderPage>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPage {
        val view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false)
        return HolderPage(view)

    }

    override fun getItemCount(): Int {
        return pagerArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPage, position: Int) {
        //get data , set data  format data, handle click etc.
        val model = pagerArrayList[position]
        //get data
        val authorName = model.authorName
        val content = model.content
        val id = model.id
        val published = model.published
        val selfLink = model.selfLink
        val title = model.title
        val updated = model.updated
        val url = model.url
        //convert html content  to simple text
        val document = Jsoup.parse(content)
        try {
            //get image, there may be multiple
            val elements = document.select("img")
            val image = elements[0].attr("src")
            // set image
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        //format date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")//convert from e.g.2020-1027T14:12:00-07
        val dateFormat2 = SimpleDateFormat("dd/MM/yyyy K:mm a")//convert to 27/10/2020 02:07 PM
        var formattedDate = ""
        try {
            val date = dateFormat.parse(published)
            formattedDate = dateFormat2.format(date)
        } catch (e: java.lang.Exception) {
            // in case of  exception , set the same we got from api
            formattedDate = published
            e.printStackTrace()
        }

        holder.titleTv.text = title
        holder.descriptionTv.text = document.text()
        holder.publishInfoTv.text = "By $authorName $formattedDate"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PageDetailsActivity::class.java)
            intent.putExtra("pageId", id)
          //  content.starActivity(intent)
        }

    }

    inner class HolderPage(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val publishInfoTv: TextView = itemView.findViewById(R.id.publishInfoTV)
        val imageIv: ImageView = itemView.findViewById(R.id.imageIv)
        val descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)
    }
}