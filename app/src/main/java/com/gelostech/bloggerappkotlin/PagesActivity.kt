package com.gelostech.bloggerappkotlin

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gelostech.bloggerappkotlin.adapter.AdapterPage
import com.gelostech.bloggerappkotlin.model.ModelPage
import com.gelostech.bloggerappkotlin.utils.Constants
import kotlinx.android.synthetic.main.activity_pages.*
import org.json.JSONObject
import java.lang.Exception

class PagesActivity : AppCompatActivity() {

    private val TAG = "PAGE_TAG"

    private lateinit var pageArrayList: ArrayList<ModelPage>

    private lateinit var adapterPage: AdapterPage

    private lateinit var progressDialog: ProgressDialog

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        //actionBar, title
        actionBar = supportActionBar!!
        actionBar.title = "Android Tutorials"
        actionBar.subtitle = "Pages"
        //back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        loadPages()
    }

    private fun loadPages() {
        // setup progressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Loading Pages..")
        progressDialog.show()

        //url  of api
        val url = "https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/pages?key=${Constants.API_KEY}"
        Log.d(TAG, "loadPages:  URL $url")

        //request  api call
        val stringRequest = StringRequest(Request.Method.GET, url, 
            Response.Listener {response ->
                //response  is received
                Log.d(TAG, "loadPages: $response")

                //response  is in JSON Object form
                val jsonObject = JSONObject(response)
                //get array  of pages from above  object
                val jsonArrayPages = jsonObject.getJSONArray("items")

                //init and clear list before adding data  into it
                pageArrayList = ArrayList()
                pageArrayList.clear()

                //get all pages
                for (i in 0 until jsonArrayPages.length()){
                    try {
                        //each recordly page in JsonArrayPages is in Json Object form
                        val jsonObjectPage = jsonArrayPages.getJSONObject(i)

                        //get data from jsonObjectPage
                        val  id  = jsonObjectPage.getString("id")
                        val  title  = jsonObjectPage.getString("title")
                        val  content  = jsonObjectPage.getString("content")
                        val  published  = jsonObjectPage.getString("published")
                        val  updated  = jsonObjectPage.getString("updated")
                        val  url  = jsonObjectPage.getString("url")
                        val  selfLink  = jsonObjectPage.getString("selfLink")

                        //display name  is in  jsonObject Page  > author > displayName
                        val displayName = jsonObjectPage.getJSONObject("author").getString("displayName")
                        //profile image  is in  jsonObject Page  > author > image>url
                        val image  = jsonObjectPage.getJSONObject("author").getJSONObject("image").getString("url")

                      //set  data

                        val modelPage =
                            ModelPage(
                                "$displayName",
                                "$content",
                                "$id",
                                "$published",
                                "$selfLink",
                                "$title",
                                "$updated",
                                "$url"
                            )

                        //add model to list
                        pageArrayList.add(modelPage)

                    }catch (e: Exception){
                        Log.d(TAG, "loadPages: ${e.message}")
                    }
                }

                // setup adapter
                adapterPage  = AdapterPage(this@PagesActivity, pageArrayList)
                pagesRv.adapter = adapterPage
        }, Response.ErrorListener {error ->
                //failed
                 progressDialog.dismiss()
                Log.d(TAG, "loadPages: ${error.message}")
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        })
        progressDialog.dismiss()
        //add request  to queue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}