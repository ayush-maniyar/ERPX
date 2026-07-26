package com.erp.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.erp.client.ui.navigation.ErpNavHost
import com.erp.client.ui.theme.ERPXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as ErpApplication).container

        setContent {
            ERPXTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ErpNavHost(container = container)
                }
            }
        }
    }
}
