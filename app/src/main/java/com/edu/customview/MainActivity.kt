package com.edu.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewLinear = findViewById<CustomLinearLayout>(R.id.customList)
        textViewLinear.addItem("One")
        textViewLinear.addItem("Two")
        textViewLinear.addItem("Three")
        textViewLinear.addItem("Four")
        textViewLinear.addItem("Five")
        textViewLinear.addItem("6")
        textViewLinear.addItem("7")
        textViewLinear.addItem("8")
        textViewLinear.addItem("9")
        textViewLinear.addItem("10")
        textViewLinear.addItem("11")
        textViewLinear.addItem("12")
        textViewLinear.addItem("13")
        textViewLinear.addItem("14")
        textViewLinear.addItem("15")


    }
}