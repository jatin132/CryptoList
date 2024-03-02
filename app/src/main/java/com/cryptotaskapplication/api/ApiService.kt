package com.cryptotaskapplication.api

import com.cryptotaskapplication.models.CryptoData
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("v3/cryptocurrency/listing?start=1&limit=500")
    suspend fun getCryptoCurrency(): Response<CryptoData>
}