package com.pysun.http

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiService {
    @GET("orgs/octokit/repos")
    fun getRepos(): Observable<ResponseBody?>
}