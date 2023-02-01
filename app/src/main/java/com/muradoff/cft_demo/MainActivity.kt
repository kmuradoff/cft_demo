package com.muradoff.cft_demo

import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muradoff.cft_demo.`interface`.ApiInterface
import com.muradoff.cft_demo.data.DataItem
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

const val BASE_URL = "https://lookup.binlist.net/"
public var BIN_NUMBER = ""

class MainActivity : AppCompatActivity() {
    private var history: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadSearchHistory()
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, history)
        searchHistory.adapter = arrayAdapter

        submitButton.setOnClickListener(View.OnClickListener {
            history.add(editTextBinNumber.text.toString())
            responseText.text = "Please wait!"
            BIN_NUMBER = editTextBinNumber.text.toString()
            saveSearchHistory()
            getMyData()

        })

        searchHistory.setOnItemClickListener { _, _, i, _ ->
            editTextBinNumber.setText(history[i])
        }
    }

    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getData(BIN_NUMBER)

        retrofitData.enqueue(object : Callback<DataItem> {
            override fun onResponse(call: Call<DataItem>, response: Response<DataItem>) {
                val responseBody = response.body()!!

                val stringBuilder = StringBuilder()
                stringBuilder.append("Scheme / Network: " + responseBody.scheme + "<br>")
                stringBuilder.append("Type: " + responseBody.type + "<br>")
                stringBuilder.append("Brand: " + responseBody.brand + "<br>")
                stringBuilder.append("Prepaid: " + responseBody.prepaid + "<br>")
                stringBuilder.append("Card Number LENGTH: " + responseBody.number.length + "<br>")
                stringBuilder.append("Card Number LUHN: " + responseBody.number.luhn + "<br>")
                stringBuilder.append("Bank URL: " +  "<a href='http://" + responseBody.bank.url + "'>" + responseBody.bank.name + "</a>" + "<br>")
                stringBuilder.append("Bank Phone: " + "<a href='tel:" + responseBody.bank.phone + "'>"+ responseBody.bank.phone + "</a>" + "<br>")
                stringBuilder.append("Location: <a href='https://www.google.com/maps?q=loc:" + responseBody.country.latitude + "," + responseBody.country.longitude + "'>" + responseBody.country.emoji + " " + responseBody.country.name  + "</a>")

                responseText.text = HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
                responseText.movementMethod = LinkMovementMethod.getInstance()
            }


            override fun onFailure(call: Call<DataItem>, t: Throwable) {
                Log.d("MainActivity", "onFailure: " + t.message)
            }
        })
    }

    private fun saveSearchHistory(){
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var gson = Gson()
        var json:String = gson.toJson(history)
        editor.putString("search history", json)
        editor.apply()
    }

    private fun loadSearchHistory(){
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        var gson = Gson()
        var json = sharedPreferences.getString("search history", "[]")
        history = gson.fromJson(json, object :TypeToken<ArrayList<String>>(){}.type)
    }

}