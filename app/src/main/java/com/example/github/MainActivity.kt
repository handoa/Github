package com.example.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.github.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    lateinit var my_toolbar: androidx.appcompat.widget.Toolbar
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        my_toolbar = findViewById(R.id.my_toolbar)
        my_toolbar.setTitle(R.string.app_name)
        setSupportActionBar(my_toolbar)

        //supportActionBar!!.setDisplayShowTitleEnabled(true)
    }


    fun OnCreateOptionsMenu(menu: Menu?): Boolean{
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            R.id.toolbar_myPage->{
                Snackbar.make(my_toolbar,"myPage", Snackbar.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
