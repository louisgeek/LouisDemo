package com.louis.myview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.R
import com.google.android.material.textfield.TextInputEditText

class MyTextInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle  //defStyleAttr!!!
) : TextInputEditText(context, attrs, defStyleAttr) {
}