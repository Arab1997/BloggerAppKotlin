package com.gelostech.bloggerappkotlin


import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gelostech.bloggerappkotlin.adapter.AdapterPost
import com.gelostech.bloggerappkotlin.model.ModelPost
import com.gelostech.bloggerappkotlin.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var url = "" //complete url for
    private var nextToken = "" // next page token to load next/more

    private lateinit var postArrayList: ArrayList<ModelPost>
    private lateinit var adapterPost: AdapterPost
    private lateinit var progressDialog: ProgressDialog

    private var isSearch = false
    private val TAG = "MAIN_TAG"

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //actionBar, title
        actionBar = supportActionBar!!
        actionBar.title = "Android Tutorials"
        actionBar.subtitle = "Posts"
        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")

        //init & clear list before adding data into it
        postArrayList = ArrayList()
        postArrayList.clear()
        loadPosts()

        //handle click, load more posts
        loadMoreBtn.setOnClickListener {
            val query = searchEt.text.toString().trim()
            if (TextUtils.isEmpty(query)) {
                loadPosts()
            } else {
                searchPosts(query)
            }
        }
        //search
        searchBtn.setOnClickListener {
            nextToken = ""
            url = ""

            postArrayList = ArrayList()
            postArrayList.clear()
            loadMoreBtn.isEnabled = true
            val query = searchEt.text.toString().trim()
            if (TextUtils.isEmpty(query)) {
                loadPosts()
            } else {
                searchPosts(query)
            }
        }
    }

    private fun loadPosts() {
        isSearch = false
        progressDialog.show()

        url = when (nextToken) {
            "" -> {
                Log.d(TAG, "loadPosts: NextPageToken is empty, more psots")
                ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts?maxResults=${Constants.MAX_RESULTS}&key=${Constants.API_KEY}")

            }
            "end" -> {
                Log.d(TAG, "loadPosts: Next page token is end, no more post i.e loaded all posts")
                Toast.makeText(this, "No more posts", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                return
            }
            else -> {
                Log.d(TAG, "loadPosts: Next page Token: $nextToken")
                ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts?maxResults=${Constants.MAX_RESULTS}&pageToken=$nextToken&key=${Constants.API_KEY}")
            }
        }
        Log.d(TAG, "loadPosts: URL: $url")

        //request data , Method is GET
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            // we get response, so dissmisdialog first
            progressDialog.dismiss()
            Log.d(TAG, "loadPosts: $response")

            //Json data is in response parametr /variable, it may cause exception while parsing/formating, so do it try catch
            try {
                //we have response as JSON Object
                val jsonObject = JSONObject(response)
                try {
                    nextToken = jsonObject.getString("nextPageToken")
                    Log.d(TAG, "loadPosts: NextPageToken: $nextToken")

                } catch (e: Exception) {
                    Toast.makeText(this, "Reached end of the page..", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "loadPosts: NextPageToken: Reached end of the page..")
                    nextToken = "end"
                }
                //get json array data from json object
                val jsonArray = jsonObject.getJSONArray("items")
                // continue getting data untill complated all
                for (i in 0 until jsonArray.length()) {
                    try {
                        val jsonObject01 = jsonArray.getJSONObject(i)

                        val id = jsonObject01.getString("id")
                        val title = jsonObject01.getString("title")
                        val content = jsonObject01.getString("content")
                        val published = jsonObject01.getString("published")
                        val updated = jsonObject01.getString("updated")
                        val url = jsonObject01.getString("url")
                        val selfLink = jsonObject01.getString("selfLink")
                        val authorName =
                            jsonObject01.getJSONObject("author").getString("displayName")
                        val image = jsonObject01.getJSONObject("author").getString("displayName")

                        //set data
                        val modelPost =
                            ModelPost(
                                "$authorName",
                                "$content",
                                "$id",
                                "$published",
                                "$selfLink",
                                "$title",
                                "$updated",
                                "$url"
                            )

                        //add data to list
                        postArrayList.add(modelPost)
                    } catch (e: Exception) {
                        Log.d(TAG, "loadPosts: 1 ${e.message}")
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                //setup  adapter
                adapterPost = AdapterPost(
                    this@MainActivity,
                    postArrayList
                )
                //set adapter  to recycler
                postsRv.adapter = adapterPost
                progressDialog.dismiss()
            } catch (e: Exception) {
                Log.d(TAG, "loadPosts: 2 ${e.message}")
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

        }, { error ->
            Log.d(TAG, "loadPosts: ${error.message}")
            Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
        })
        //add  requset in queue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun searchPosts(query: String) {
        isSearch = true
        progressDialog.show()

        url = when (nextToken) {
            "" -> {
                Log.d(TAG, "searchPosts: NextPageToken is empty, more posts")
                ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts/search?q=$query&key=${Constants.API_KEY}")
            }
            "end" -> {
                Log.d(
                    TAG,
                    "searchPosts: Next page token is end, no more post  i.e loaded all posts"
                )
                Toast.makeText(this, " No more posts", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                return
            }
            else -> {
                Log.d(TAG, "searchPosts: Next page Token: $nextToken")
                ("https://www.googleapis.com/blogger/v3/blogs/${Constants.BLOG_ID}/posts/search?q=$query&pageToken=$nextToken=${Constants.API_KEY}")
            }
        }
        Log.d(TAG, "searchPosts: URL: $url")

        //request data , Method is GET
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            // we get response, so dissmisdialog first
            progressDialog.dismiss()
            Log.d(TAG, "loadPosts: $response")

            //Json data is in response parametr /variable, it may cause exception while parsing/formating, so do it try catch
            try {
                //we have response as JSON Object
                val jsonObject = JSONObject(response)
                try {
                    nextToken = jsonObject.getString("nextPageToken")
                    Log.d(TAG, "searchPosts: NextPageToken: $nextToken")

                } catch (e: Exception) {
                    Toast.makeText(this, "Reached end of the page..", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "searchPosts: NextPageToken: Reached end of the page..")
                    nextToken = "end"
                }
                //get json array data from json object
                val jsonArray = jsonObject.getJSONArray("items")
                // continue getting data untill complated all
                for (i in 0 until jsonArray.length()) {
                    try {
                        val jsonObject01 = jsonArray.getJSONObject(i)

                        val id = jsonObject01.getString("id")
                        val title = jsonObject01.getString("title")
                        val content = jsonObject01.getString("content")
                        val published = jsonObject01.getString("published")
                        val updated = jsonObject01.getString("updated")
                        val url = jsonObject01.getString("url")
                        val selfLink = jsonObject01.getString("selfLink")
                        val authorName =
                            jsonObject01.getJSONObject("author").getString("displayName")
                        val image = jsonObject01.getJSONObject("author").getString("displayName")

                        //set data
                        val modelPost =
                            ModelPost(
                                "$authorName",
                                "$content",
                                "$id",
                                "$published",
                                "$selfLink",
                                "$title",
                                "$updated",
                                "$url"
                            )

                        //add data to list
                        postArrayList.add(modelPost)
                    } catch (e: Exception) {
                        Log.d(TAG, "loadPosts: 1 ${e.message}")
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                //setup  adapter
                adapterPost = AdapterPost(
                    this@MainActivity,
                    postArrayList
                )
                //set adapter  to recycler
                postsRv.adapter = adapterPost
                progressDialog.dismiss()
            } catch (e: Exception) {
                Log.d(TAG, "loadPosts: 2 ${e.message}")
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

        }, { error ->
            Log.d(TAG, "loadPosts: ${error.message}")
            Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
        })
        //add  requset in queue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate  menu_main
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // get  id  of clicked item  on menu
        val id  = item.itemId
        //handle click
        if (id==R.id.action_pages){
            //start  pages  activity
            startActivity(Intent(this, PagesActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}