package com.cryptotaskapplication.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cryptotaskapplication.api.ApiService
import com.cryptotaskapplication.models.CryptoData

class MainRepository(private val apiService: ApiService){
    private val cryptoLiveData = MutableLiveData<CryptoData>()

    val crypto: LiveData<CryptoData>
    get() = cryptoLiveData

    suspend fun getCryptoData(){
        try {
            val response = apiService.getCryptoCurrency()
            if (response.isSuccessful) {
                cryptoLiveData.postValue(response.body())
            } else {
                Log.e("Exception", "Unsuccessful to get data: ${response.message()}, Code: ${response.code()}")
            }
        } catch (e: Exception){
            Log.e("Exception", "API error $e")
        }
    }
}