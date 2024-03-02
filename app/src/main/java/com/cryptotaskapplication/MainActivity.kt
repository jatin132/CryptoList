package com.cryptotaskapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cryptotaskapplication.adapter.MainAdapter
import com.cryptotaskapplication.api.ApiService
import com.cryptotaskapplication.api.RetrofitClient
import com.cryptotaskapplication.models.CryptoCurrency
import com.cryptotaskapplication.repository.MainRepository
import com.cryptotaskapplication.services.CryptoDataWorker
import com.cryptotaskapplication.viewModels.MainViewModel
import com.cryptotaskapplication.viewModels.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var adapter: MainAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var filter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()

    }

    private fun setup() {
        initialization()
        configuration()
        setupWorkManager()
        filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(NetworkReceiver(), filter)
    }

    private fun initialization() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.noData)
    }

    private fun configuration(){
        val apiService = RetrofitClient.getInstance().create(ApiService::class.java)

        val repository = MainRepository(apiService)
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
    }

    fun getCryptoData() {
        mainViewModel.getCryptoData()

        mainViewModel.crypto.observe(this) { cryptoData ->
            if (cryptoData != null) {
                if (cryptoData.data.cryptoCurrencyList.isNotEmpty()) {
                    Log.i("Exception", "In if")
                    // Data is not empty, process it
                    textView.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    setupRecyclerView(cryptoData.data.cryptoCurrencyList)
                } else {
                    Log.i("Exception", "In else")
                    progressBar.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                }
            } else {
                Log.i("Exception", "In parent else")
                progressBar.visibility = View.GONE
                textView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView(dataList: List<CryptoCurrency>) {
        // Assuming you have an instance of MainAdapter, and your RecyclerView is recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(applicationContext, dataList)
        recyclerView.adapter = adapter
    }

    private fun setupWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val cryptoDataWorkRequest = PeriodicWorkRequestBuilder<CryptoDataWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()


        WorkManager.getInstance(this).enqueue(cryptoDataWorkRequest)
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isOnlineAPI23(connectivityManager)
        } else {
            isOnlineLegacy(connectivityManager)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun isOnlineAPI23(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun isOnlineLegacy(connectivityManager: ConnectivityManager): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val mainActivity = context as? MainActivity
            mainActivity?.handleNetworkChange()
        }
    }

    private fun handleNetworkChange() {
        val view: View = findViewById(android.R.id.content)
        if (isOnline()) {
            getCryptoData()
        } else {
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
        }
    }
}