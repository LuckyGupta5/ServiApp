package com.example.servivet.utils

import android.text.InputFilter
import android.text.Spanned

class NoLeadingSpaceInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // Check if the source contains a space as the first character
        if (dstart == 0 && source != null && source.startsWith(" ")) {
            // Replace the space with an empty string
            return source.subSequence(1, source.length)
        }
        // If no space at the beginning, allow the input
        return null
    }
}