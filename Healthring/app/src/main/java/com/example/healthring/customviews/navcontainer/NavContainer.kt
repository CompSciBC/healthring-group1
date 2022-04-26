package com.example.healthring.customviews.navcontainer

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.navigation.findNavController
import com.example.healthring.R

//Class for bottom nav bar compound view
//The bottom nav bar appears on the bottom of the main three screens and
//enables navigation between them via clicking of the buttons.
class NavContainer : FrameLayout{

    //view constructors -- all three are needed
    constructor(context: Context) : super(context) {
        init(null, 0)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }



    private fun init(attrs: AttributeSet?, defStyle: Int) {
        //inflate bottom nav bar
        inflate(context, R.layout.nav_container, this)

    }

}