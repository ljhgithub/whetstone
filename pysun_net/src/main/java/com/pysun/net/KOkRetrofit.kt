package com.pysun.net

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object KOkRetrofit {
    private lateinit var baseOkHttpClient: OkHttpClient
    private lateinit var baseRetrofit: Retrofit
    private val retrofitMap = hashMapOf<String, Retrofit>()


    fun init(baseUrl: String, debug: Boolean) {


        var trustManager = TrustAllManager()
        var tmpBaseOkHttpClient: OkHttpClient = if (debug) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
                    .addInterceptor(loggingInterceptor)
                    .build()

        } else {
            OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
                    .build()

        }

        init(baseUrl, debug, tmpBaseOkHttpClient)



    }


    fun init(baseUrl: String, debug: Boolean,interceptors:List<Interceptor>) {


        var trustManager = TrustAllManager()
        var okBuilder:  OkHttpClient.Builder = if (debug) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
                    .addInterceptor(loggingInterceptor)


        } else {
            OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)


        }
        interceptors.forEach {
            okBuilder.addInterceptor(it)
        }
        var tmpBaseOkHttpClient=okBuilder.build()
        init(baseUrl, debug, tmpBaseOkHttpClient)


    }

    fun init(baseUrl: String, debug: Boolean, baseClient: OkHttpClient) {

        baseOkHttpClient = baseClient
        var gsonb = GsonBuilder()
                .setVersion(1.0)
                .serializeNulls()
                .create()
        baseRetrofit = Retrofit.Builder()
                .client(baseOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gsonb))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()
        retrofitMap[baseUrl] = baseRetrofit


    }

    fun <T> getApiService(tClazz: Class<T>): T {
        return baseRetrofit.create(tClazz)
    }


    fun <T> getApiService(tClazz: Class<T>, baseUrl: String): T {

        return if (retrofitMap.containsKey(baseUrl)) {
            retrofitMap[baseUrl]!!.create(tClazz)
        } else {
            val newRetrofit = baseRetrofit.newBuilder().baseUrl(baseUrl).build()
            retrofitMap[baseUrl] = newRetrofit
            newRetrofit.create(tClazz)
        }
    }


    private fun createSSLSocketFactory(trustManager: TrustManager): SSLSocketFactory {
        var sSLSocketFactory: SSLSocketFactory
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf(trustManager),
                SecureRandom())
        sSLSocketFactory = sc.socketFactory

        return sSLSocketFactory
    }

    public class TrustAllManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>, authType: String) {

        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)

        }


    }
}