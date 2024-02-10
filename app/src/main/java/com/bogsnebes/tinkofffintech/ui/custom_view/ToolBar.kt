package com.bogsnebes.tinkofffintech.ui.custom_view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.Gravity
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

    private var _back: Boolean = false

    //  false - не показывать поиск
    // true показывать
    val back: Boolean
        get() = _back

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToolBar, defStyleAttr, 0)

        val titleText = typedArray.getString(R.styleable.ToolBar_titleText) ?: "Популярное"
        val titleTextSize = typedArray.getDimension(R.styleable.ToolBar_titleTextSize, 25f)
        val titleTextColor = typedArray.getColor(
            R.styleable.ToolBar_titleTextColor,
            resources.getColor(R.color.black)
        )
        val hintText = typedArray.getString(R.styleable.ToolBar_hintText) ?: "Поиск"

        typedArray.recycle()

        textView = TextView(context).apply {
            text = SpannableString(titleText).apply {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    titleText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textSize = titleTextSize / resources.displayMetrics.scaledDensity
            setTextColor(titleTextColor)
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
        }

        editText = EditText(context).apply {
            hint = hintText
            visibility = GONE
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
                marginStart = 20
            }
        }

        imageView = ImageView(context).apply {
            setImageResource(R.drawable.ic_search)
        }

        addView(textView)
        addView(imageView)
        addView(editText)
    }

    fun setOnEditTextChangedListener(listener: TextWatcher) {
        editText.addTextChangedListener(listener)
    }

    fun setOnBackClickListener(listener: (ImageView) -> Unit) {
        imageView.setOnClickListener {
            if (!_back) {
                textView.visibility = GONE
                editText.visibility = VISIBLE
                imageView.setImageResource(R.drawable.ic_back)
            } else {
                textView.visibility = VISIBLE
                editText.visibility = GONE
                imageView.setImageResource(R.drawable.ic_search)
                listener(imageView)
            }
            _back = !_back
        }
    }

    fun setupUI(showBack: Boolean, textOfEditText: String) {
        _back = showBack
        if (!_back) {
            textView.visibility = VISIBLE
            editText.visibility = GONE
            imageView.setImageResource(R.drawable.ic_search)
        } else {
            textView.visibility = GONE
            editText.visibility = VISIBLE
            editText.setText(textOfEditText)
            imageView.setImageResource(R.drawable.ic_back)
        }
    }
}
