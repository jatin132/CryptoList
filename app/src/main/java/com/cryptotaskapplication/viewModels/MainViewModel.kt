package com.cryptotaskapplication.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptotaskapplication.models.CryptoData
import com.cryptotaskapplication.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository): ViewModel() {
    fun getCryptoData(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCryptoData()
        }
    }

    val crypto: LiveData<CryptoData>
    get() = repository.crypto

}