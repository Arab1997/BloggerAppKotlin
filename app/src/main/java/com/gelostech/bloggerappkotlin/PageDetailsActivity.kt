package com.gelostech.bloggerappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar

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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //
        return super.onSupportNavigateUp()
    }
}