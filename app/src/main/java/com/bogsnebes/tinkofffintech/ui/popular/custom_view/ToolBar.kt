package com.bogsnebes.tinkofffintech.ui.popular.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bogsnebes.tinkofffintech.R

class ToolBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val textView: TextView
    private val imageView: ImageView
    private val editText: EditText

    private var back: Boolean = false

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        textView = TextView(context).apply {
            text = "Популярное"
            textSize = 25f
            setTextColor(resources.getColor(R.color.black))
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
        }

        editText = EditText(context).apply {
            hint = "Поиск"
            visibility = GONE
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
                marginStart = 20
            }
        }

        imageView = ImageView(context).apply {
            setImageResource(R.drawable.ic_search)
            setOnClickListener {
                if (back) {
                    textView.visibility = GONE
                    editText.visibility = VISIBLE
                    this.setImageResource(R.drawable.ic_back)
                } else {
                    textView.visibility = VISIBLE
                    editText.visibility = GONE
                    this.setImageResource(R.drawable.ic_search)
                }
                back = !back
            }
        }

        addView(textView)
        addView(imageView)
        addView(editText)
    }
}
