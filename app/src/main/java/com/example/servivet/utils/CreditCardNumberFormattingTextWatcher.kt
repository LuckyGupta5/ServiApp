package com.example.servivet.utils

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText

class CreditCardNumberFormattingTextWatcher : TextWatcher {
    private val MAX_LENGTH = 16
    private val SPACE_OFFSETS = intArrayOf(4, 9, 14)

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        // Remove all space characters
        s?.replace("\\s".toRegex(), "")

        // Insert space character at specified offsets
        for (offset in SPACE_OFFSETS.reversed()) {
            if (offset <= s!!.length) {
                s.insert(offset, " ")
            }
        }
    }
}

fun EditText.addCreditCardNumberFormattingTextWatcher() {
    val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
    this.filters = filters
    this.addTextChangedListener(CreditCardNumberFormattingTextWatcher())
}