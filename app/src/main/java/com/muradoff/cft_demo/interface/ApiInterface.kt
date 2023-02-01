package com.muradoff.cft_demo.`interface`


import com.muradoff.cft_demo.MainActivity
import com.muradoff.cft_demo.data.DataItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {


    @GET("{BIN}")
    fun getData(
        @Path("BIN") BIN: String
    ): Call<DataItem>
}