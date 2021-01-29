package com.pysun.http

import com.pysun.net.KOkRetrofit

object ApiManager {
    private var mApiService: ApiService?=null


    fun getApiService(): ApiService {
        if (null == mApiService) {
            mApiService = KOkRetrofit.getApiService(ApiService::class.java) as ApiService
        }
        return mApiService!!

    }



}