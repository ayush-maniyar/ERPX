package com.erp.client

import android.app.Application
import com.erp.client.data.AppContainer

class ErpApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
