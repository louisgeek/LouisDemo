package com.louis.myview

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.R

class MyEditText(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle //defStyleAttr!!!
) : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) { //AppCompatEditText
}