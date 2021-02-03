package com.gelostech.bloggerappkotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gelostech.bloggerappkotlin.model.ModelComment
import com.gelostech.bloggerappkotlin.R
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat

class AdapterComment(
    var context: Context,
    var commentArrayList: ArrayList<ModelComment>
) : RecyclerView.Adapter<AdapterComment.HolderComment>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
        val view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false)
        return HolderComment(view)
    }

    override fun getItemCount(): Int {
        return commentArrayList.size  // return size of list of records
    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {
        //get data
        val modelComment = commentArrayList[position]

        val id = modelComment.id
        val name = modelComment.name
        val published = modelComment.published
        val comment = modelComment.comment
        val profileImage = modelComment.profileImage

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

        //set data
        holder.nameTv.text = name
        holder.dateTv.text = formattedDate
        holder.commentTv.text = comment

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv)
        }
        catch (e:Exception){
            holder.profileIv.setImageResource(R.drawable.ic_person_gray)
        }
    }


    inner class HolderComment(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //init  UI  Views of row_comment.xml
        var profileIv: ImageView = itemView.findViewById(R.id.profileIv)
        var nameTv: TextView = itemView.findViewById(R.id.nameTv)
        var dateTv: TextView = itemView.findViewById(R.id.dateTv)
        var commentTv: TextView = itemView.findViewById(R.id.commentTv)
    }

}