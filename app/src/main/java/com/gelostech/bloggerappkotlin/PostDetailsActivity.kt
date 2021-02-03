package com.gelostech.bloggerappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gelostech.bloggerappkotlin.adapter.AdapterComment
import com.gelostech.bloggerappkotlin.adapter.AdapterLabel
import com.gelostech.bloggerappkotlin.model.ModelComment
import com.gelostech.bloggerappkotlin.model.ModelLabel
import com.gelostech.bloggerappkotlin.utils.Constants
import kotlinx.android.synthetic.main.activity_post_details.*
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import javax.xml.transform.OutputKeys

class PostDetailsActivity : AppCompatActivity() {

    private var postId: String? =
        null // will get from intent, was passed in intent from AdapterPost
    private val TAG = "POST_DETAILS_TAG"
    private val TAG_COMMENT = "POST_COMMENT_TAG"

    //actionBar
    private lateinit var actionBar: ActionBar

    private lateinit var labelArrayList: ArrayList<ModelLabel>
    private lateinit var adapterLabel: AdapterLabel

    private lateinit var commentArrayList: ArrayList<ModelComment>
    private lateinit var adapterComment: AdapterComment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        //init actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Post Details"
        //add back  button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //get  the psot id from intent
        postId = intent.getStringExtra("postId")
        Log.d(TAG, "onCreate: $postId")

        // setup webView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        loadPostDetails()
    }

    private fun loadPostDetails() {
        val url =
            ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts/$postId?key=${Constants.API_KEY}")
        //("https://www.googleapis.com/blogger/v3/blogs/3332972326669427275/posts/7997270624975786034?key=AIzaSyD1rsXSTmCUMFBHQksBeS76cAlct9JPht8")

        Log.d(TAG, "loadPostDetails: $url")
        //request  api
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            //successfully received response
            Log.d(TAG, "loadPostDetails: $response")

            //Response  is in JSON Object form
            try {
                val jsonObject = JSONObject(response)

                //GET DATA
                val title = jsonObject.getString("title")
                val published = jsonObject.getString("published")
                val content = jsonObject.getString("content")
                val url = jsonObject.getString("url")
                val displayName = jsonObject.getJSONObject("author").getString("displayName")

                //convert GMT time to proper format
                //format date
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")//convert from e.g.2020-1027T14:12:00-07
                val dateFormat2 =
                    SimpleDateFormat("dd/MM/yyyy K:mm a")//convert to 27/10/2020 02:07 PM
                var formattedDate = ""
                try {
                    val date = dateFormat.parse(published)
                    formattedDate = dateFormat2.format(date)
                } catch (e: Exception) {
                    // in case of  exception , set the same we got from api
                    formattedDate = published
                    e.printStackTrace()
                }

                //SET data
                actionBar.subtitle = title
                titleTv.text = title
                publishInfoTV.text = " By$displayName $formattedDate" // e.g
                //convert contains web page  like html , so load in webview
                webView.loadDataWithBaseURL(null, content, " text/html", OutputKeys.ENCODING, null)

                //get labels, response of post detail  - specific post api  also contains array of labels of that post
                try {
                    //init and clear list before adding data
                    labelArrayList = ArrayList()
                    labelArrayList.clear()
                    val jsonArray = jsonObject.getJSONArray("labels")
                    for (i in 0 until jsonArray.length()) {
                        // get label
                        val label = jsonArray.getString(i)
                        // add data to model
                        val modelLabel =
                            ModelLabel(label)
                        //add model to list
                        labelArrayList.add(modelLabel)
                    }
                    //setup adapater
                    adapterLabel =
                        AdapterLabel(
                            this@PostDetailsActivity,
                            labelArrayList
                        )
                    // set adapter
                    labelsRv.adapter = adapterLabel

                } catch (e: Exception) {
                    Log.d(TAG, "loadPostDetails: ${e.message}")
                }
                // after loading post details, load comments
                loadComments()


            } catch (e: Exception) {
                Log.d(TAG, "loadPostDetails: ${e.message}")
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }) { error ->
            // failed receiving response show error message
            Log.d(TAG, "loadPostDetails: ${error.message}")
            Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
        }

        //add request  to queue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)// comments  loaded but not images
    }

    private fun loadComments(){
        val url = "https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts/$postId/comments?key=${Constants.API_KEY}"
        Log.d(TAG_COMMENT, "loadComment: $url")

        //volley string request
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            //response  of api received
            Log.d(TAG_COMMENT, "loadComment:$response ")

            //init  and clear list before adding data into it
            commentArrayList = ArrayList()
            commentArrayList.clear()

            try {
                //we have JSoN  object response
                val jsonObject = JSONObject(response)
                //get array  of comment
                val jsonArrayItems = jsonObject.getJSONArray("items")
                for (i in 0 until jsonArrayItems.length()) {
                    //get specific comment  json object
                    val jsonObjectComment = jsonArrayItems.getJSONObject(i)
                    // get data
                    val id = jsonObjectComment.getString("id")
                    val published = jsonObjectComment.getString("published")
                    val content = jsonObjectComment.getString("content")
                    val displayName = jsonObjectComment.getJSONObject("author").getString("content")
                    val profileImage ="http:" + jsonObjectComment.getJSONObject("author").getJSONObject("image").getString("url")

                    //add data in mode
                    val modelComment =
                        ModelComment(
                            "$id",
                            "$published",
                            "$content",
                            "$displayName",
                            "$profileImage"
                        )
                    //add mode  in list
                    commentArrayList.add(modelComment)
                }
                //setup  adapter
                adapterComment = AdapterComment(this@PostDetailsActivity, commentArrayList)
                //set adapter to recyclerView
                commentsRv.adapter = adapterComment
            } catch (e: Exception) {
                Log.d(TAG_COMMENT, "loadComment: ${e.message}")
        }
        })
        {error ->
            //failed receiving api response 
            Log.d(TAG_COMMENT, "loadComment: ${error.message}")
        }
        //add request to queue
        val requestQueue  = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // get to previous activity from where we came,
        return super.onSupportNavigateUp()
    }
}