package com.example.healthring.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.healthring.R

class SampleCompoundView: ConstraintLayout {

    private val button : Button;
    private val textView: TextView;

    //Constructors
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init{
        inflate(context, R.layout.sample_compound_view, this)

        button = findViewById(R.id.sampleButton)
        textView = findViewById(R.id.sampleText)

        button.setOnClickListener {
            Log.i("SampleCompoundView", "I did something!")
            android.widget.Toast.makeText(context, "Boom shaka laka", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}