package com.tuca.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.Editable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import com.google.android.material.appbar.MaterialToolbar


class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private var searchText: String = ""

    companion object {
        private const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarTop)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        editTextSearch = findViewById(R.id.editTextSearch)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""

                val showClear = searchText.isNotEmpty()
                editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this@SearchActivity, R.drawable.ic_search),
                    null,
                    if (showClear)
                        ContextCompat.getDrawable(
                            this@SearchActivity,
                            R.drawable.ic_cleartext
                        )
                    else null,
                    null
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // обработка клика по крестику
        editTextSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = editTextSearch.compoundDrawables[2]
                if (drawableEnd != null) {
                    val clearButtonStart = editTextSearch.width -
                            editTextSearch.paddingRight -
                            drawableEnd.intrinsicWidth
                    if (event.x >= clearButtonStart) {
                        editTextSearch.setText("")
                        editTextSearch.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restored = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        searchText = restored
        editTextSearch.setText(restored)
    }
}
