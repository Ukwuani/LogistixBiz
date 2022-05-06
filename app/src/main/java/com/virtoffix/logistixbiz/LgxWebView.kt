package com.virtoffix.logistixbiz

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebView

class LgxWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val inputConnection = BaseInputConnection(this, false)
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE
        return inputConnection
    }
}