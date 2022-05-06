package com.virtoffix.logistixbiz

import Onboarding
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.virtoffix.logistixbiz.databinding.ActivityFullscreenBinding
import java.net.URI


class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var webView: WebView
    private lateinit var progressDialog: ProgressDialog
    private val sharedPref by lazy { getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    var linkHomeList = emptyArray<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog)
        progressDialog.setCancelable(false)
        webView = binding.webView



        if (sharedPref.contains(ALIAS)) {
            BUSINESS_ALIAS = sharedPref.getString(ALIAS, "").toString()
            loadWebView("https://${BUSINESS_ALIAS}.logistix.africa/")
            progressDialog.show()
            progressDialog.setMessage("Starting ${BUSINESS_ALIAS}")
        } else {
            progressDialog.dismiss()
            Onboarding.newInstance{
                object: Onboarding.UrlListener {
                    override fun register() {
                        loadWebView("https://logistix.africa/#join")
                    }

                    override fun enteredUrl(data: String) {
                        val alias = data.lowercase()
                        loadWebView("https://${alias}.logistix.africa/staff/mobile/login")
                        progressDialog.show()
                        progressDialog.setMessage("Setting Up ${alias.replaceFirstChar { 
                            it.titlecaseChar()
                        }}")
                        sharedPref.edit().putString(ALIAS, alias).apply()
                    }

                }
            }.show(supportFragmentManager, null)
        }

        linkHomeList = arrayOf("https://${BUSINESS_ALIAS}.logistix.africa/".toUri(), "https://${BUSINESS_ALIAS}.logistix.africa".toUri())
    }

    fun loadWebView(url: String) {
        webView.run {
            loadUrl(url)
            webViewClient = LgxWebViewClient()
            webChromeClient = LgxChromeClient()
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.loadsImagesAutomatically = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
        }
    }


    inner class LgxChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            Log.d("WEBVIEW_LGX", "onProgressChanged ${view.url.toString()} ")
            Log.d("WEBVIEW_LGX", "onProgressChanged 1 https://${BUSINESS_ALIAS}.logistix.africa/login ")

            if (view.url == "https://${sharedPref.getString(ALIAS, "").toString()}.logistix.africa/staff/web/login") {
                webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")
                sharedPref.edit().clear().apply()
                Onboarding.newInstance{
                    object: Onboarding.UrlListener {
                        override fun enteredUrl(data: String) {
                            val alias = data.lowercase()
                            loadWebView("https://${alias}.logistix.africa/staff/mobile/login")
                            progressDialog.show()
                            progressDialog.setMessage("Setting Up ${alias.replaceFirstChar {
                                it.titlecaseChar()
                            }}")
                            sharedPref.edit().putString(ALIAS, alias).apply()
                        }

                    }
                }.show(supportFragmentManager, null)
            } else {
                super.onProgressChanged(view, newProgress)
            }
        }
    }


    inner class LgxWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            progressDialog.dismiss()
            if (url == "https://${BUSINESS_ALIAS}.logistix.africa") {
                webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")
            }
            super.onPageFinished(view, url)

        }


        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest
        ): Boolean {
            BUSINESS_ALIAS = sharedPref.getString(ALIAS, "").toString()

            Log.d("WEBVIEW_LGX", "shouldOverride ${request.url} ")
            Log.d("WEBVIEW_LGX", "shouldOverride ${"https://midas.logistix.africa/".toUri()} ")


            return when (request.url) {
                in linkHomeList-> {
                    webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")
                    false
                }
                "https://${BUSINESS_ALIAS}.logistix.africa/staff/login".toUri() -> {
                    webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")
                    false
                }
                else -> {
                    Log.d("WEBVIEW_LGX", "shouldOverride ${"https://midas.logistix.africa/".toUri()} ")
                    return super.shouldOverrideUrlLoading(view, request)

                }

            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

            when (url) {
                in arrayOf("https://${BUSINESS_ALIAS}.logistix.africa/", "https://${BUSINESS_ALIAS}.logistix.africa", "/") -> {
                    webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")
                }
                "https://${BUSINESS_ALIAS}.logistix.africa/staff/login" -> {
                    webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")

                }
                else -> {
                    super.onPageStarted(view, url, favicon)
                }

            }
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            webView.visibility = View.VISIBLE

        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)

//            Toast.makeText(this@FullscreenActivity, "llllllld" ,Toast.LENGTH_LONG).show();

        }


        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            when (request?.url) {
                in arrayOf("https://${BUSINESS_ALIAS}.logistix.africa/".toUri(), "https://${BUSINESS_ALIAS}.logistix.africa".toUri()) -> {

                    runOnUiThread {

                        webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/login") }
                }

                "https://${BUSINESS_ALIAS}.logistix.africa/logout".toUri() -> {

                    runOnUiThread {

                        webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/login") }
                }
            }
            return super.shouldInterceptRequest(view, request)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            Toast.makeText(this@FullscreenActivity, "${error?.errorCode} ${error?.description}" ,Toast.LENGTH_LONG).show();

            super.onReceivedError(view, request, error)

        }
    }

    override fun onResume() {
        super.onResume()
        BUSINESS_ALIAS = sharedPref.getString(ALIAS, "").toString()

    }


    companion object {

        const val ALIAS = "alias"
        const val PREF_NAME = "lgx"
        var BUSINESS_ALIAS = ""
    }

    override fun onBackPressed() {
                    Toast.makeText(this@FullscreenActivity, webView.url ,Toast.LENGTH_LONG).show();

        when {
            webView.canGoBack() -> {
                webView.goBack();
            }
            webView.url == "https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login" -> {

                webView.loadUrl("https://${BUSINESS_ALIAS}.logistix.africa/staff/mobile/login")
                sharedPref.edit().clear().apply()
                Onboarding.newInstance{
                    object: Onboarding.UrlListener {
                        override fun enteredUrl(data: String) {
                            val alias = data.lowercase()
                            loadWebView("https://${alias}.logistix.africa/staff/mobile/login")
                            progressDialog.show()
                            progressDialog.setMessage("Setting Up ${alias.replaceFirstChar {
                                it.titlecaseChar()
                            }}")
                            sharedPref.edit().putString(ALIAS, alias).apply()
                        }

                    }
                }.show(supportFragmentManager, null)
            }
            else -> {
                super.onBackPressed();
            }
        }
    }
}