package com.cryptotaskapplication.services

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cryptotaskapplication.MainActivity

class CryptoDataWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val mainActivity = applicationContext as? MainActivity
        mainActivity?.getCryptoData()
        Log.i("Exception", "Work manager setup")
        return Result.success()
    }
}
