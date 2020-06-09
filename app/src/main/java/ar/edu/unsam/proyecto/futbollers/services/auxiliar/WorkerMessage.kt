package ar.edu.unsam.proyecto.futbollers.services.auxiliar


import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker

import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkerMessage(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): ListenableWorker.Result {
        Log.d(TAG, "Performing long running task in scheduled job")

        return ListenableWorker.Result.success()
    }

    companion object {
        private val TAG = "MyWorker"
    }
}