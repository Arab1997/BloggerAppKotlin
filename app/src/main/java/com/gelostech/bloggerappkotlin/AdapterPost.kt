package com.gelostech.bloggerappkotlin

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.lang.Exception
import java.text.SimpleDateFormat

class AdapterPost(
    private val context: Context,
    private val postArrayList: ArrayList<ModelPost>

) : RecyclerView.Adapter<AdapterPost.HolderPost>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPost {

        val view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false)
        return HolderPost(view)
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPost, position: Int) {
        val model = postArrayList[position]// get data at specific position/ index of list
        //get data
        val authorName = model.authorName
        val content = model.content // its in HTMLformat , we will convert text JSOUP
        val id = model.id
        val published = model.published // date published, need to format
        val selfLink = model.selfLink
        val title = model.title
        val updated = model.updated// date edited/updated
        val url = model.url

        //convert HTML Content to simple text
        val document = Jsoup.parse(content)
        try {
          //get image , there may be multiple or no  in a post , try to get the first
            val elements = document.select("img")
            val image = elements[0].attr("src")
            //set image
           // Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(image)

        } catch (e: Exception) {
            //exeption while getting image, may be due to no image
          //  holder.imageTv.setImageResource(R.drawable.ic_image_black)
        }

        //format date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")//convert from e.g.2020-1027T14:12:00-07
        val dateFormat2 = SimpleDateFormat("dd/MM/yyyy K:mm a")//convert to 27/10/2020 02:07 PM
        var formattedDate = ""
        try {
            val date = dateFormat.parse(published)
            formattedDate = dateFormat2.format(date)
        } catch (e: Exception) {
            // in case of  exception , set the same we got from api
            formattedDate = published
            e.printStackTrace()
        }
        holder.titleTv.text = title
        holder.descriptorTv.text = document.text()
        holder.publishedInfoTv.text = "By $authorName $formattedDate"  // e.g By
    }

    inner class HolderPost(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var moreBtn: ImageButton = itemView.findViewById(R.id.moreBtn)
        var titleTv: TextView = itemView.findViewById(R.id.titleTv)
        var publishedInfoTv: TextView = itemView.findViewById(R.id.publishInfoTV)
        var imageTv: TextView = itemView.findViewById(R.id.imageIv)
        var descriptorTv: TextView = itemView.findViewById(R.id.descriptionTv)
    }

}
