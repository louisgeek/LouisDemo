package com.louis.mybrvah

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





        val myBaseAdapter = MyBaseAdapter()
//        myBaseAdapter.setStateViewLayout(this, R.layout.list_item_01)
        val stateView =  LayoutInflater.from(this).inflate(R.layout.list_item_01, FrameLayout(this), false)
        stateView.findViewById<TextView>(R.id.tv01).text = "空列表"
        myBaseAdapter.stateView = stateView

//        myBaseAdapter.animationEnable = true
        myBaseAdapter.isStateViewEnable = true

        myBaseAdapter.setOnItemClickListener { adapter, view, position ->
            Toast.makeText(
                this,
                "position=$position dat=" + adapter.getItem(position),
                Toast.LENGTH_SHORT
            ).show()
        }
        val rv01: RecyclerView = findViewById(R.id.rv01)
        rv01.layoutManager = LinearLayoutManager(this)
        rv01.adapter = myBaseAdapter


        val itemList: ArrayList<String> = arrayListOf()
        for (i in 0 until 10) {
            itemList.add("test_" + (i + 1))
        }
        myBaseAdapter.submitList(itemList)



        val tvTest :TextView =  findViewById(R.id.tvTest)
        tvTest.setOnClickListener {
            myBaseAdapter.add("test0002")
            //        myBaseAdapter[1] = "new data"
        }
        tvTest.setOnLongClickListener {
            myBaseAdapter.submitList(listOf())
            true
        }

    }


}