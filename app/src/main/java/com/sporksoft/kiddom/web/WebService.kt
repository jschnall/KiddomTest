package com.sporksoft.kiddom.web

import com.sporksoft.kiddom.models.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    @GET("/api/assessment_search_wrapper")
    fun search(@Query("query") query: String): Call<SearchResult>
}