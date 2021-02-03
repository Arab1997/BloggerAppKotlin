package com.gelostech.bloggerappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gelostech.bloggerappkotlin.utils.Constants
import kotlinx.android.synthetic.main.activity_pages_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import javax.xml.transform.OutputKeys

class PageDetailsActivity : AppCompatActivity() {

    private lateinit var pageId: String
    private lateinit var actionBar:ActionBar
    private val TAG = "PAGE_DETAILS_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_details)

        actionBar = supportActionBar!!
        actionBar.title = "Android Tutorials"
        actionBar.title = "Page Details"

        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        pageId = intent.getStringExtra("pageId")!!
        Log.d(TAG, "onCreate: ID:$pageId")

        loadPageDetails()
    }

    private fun loadPageDetails() {
        val url = ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/pages/$pageId?key=${Constants.API_KEY}")
        Log.d(TAG, "loadPageDetails: URL: $url")
        // create API request
        val stringRequest = StringRequest(Request.Method.GET, url, {response ->
            //get api response
            Log.d(TAG, "loadPageDetails: $response")
            try {

                val jsonObject = JSONObject(response)
                val title = jsonObject.getString("title")
                val published = jsonObject.getString("published")
                val content = jsonObject.getString("content")
                val url = jsonObject.getString("url")
                val id = jsonObject.getString("id")
                val displayName = jsonObject.getJSONObject("author").getString("displayName")

                //format date
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")//convert from e.g.2020-1027T14:12:00-07
                val dateFormat2 =
                    SimpleDateFormat("dd/MM/yyyy K:mm a")//convert to 27/10/2020 02:07 PM
                var formattedDate = ""
                try {
                    val date = dateFormat.parse(published)
                    formattedDate = dateFormat2.format(date)
                } catch (e: java.lang.Exception) {
                    // in case of  exception , set the same we got from api
                    formattedDate = published
                    e.printStackTrace()
                }
                //SET data
                titleTv.text = title
                publishInfoTV.text = " By$displayName $formattedDate" // e.g
                //convert contains web page  like html , so load in webview
                webView.loadDataWithBaseURL(null, content, " text/html", OutputKeys.ENCODING, null)
            }
            catch (e:Exception){
                Log.d(TAG, "loadPageDetails: ${e.message}")
            }
        })
        {error ->
            Log.d(TAG, "loadPageDetails: ${error.message}")
            Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
        }


        //add request to queue
        val requestQueue  = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //
        return super.onSupportNavigateUp()
    }
}