package com.preachooda.heroapp.app

import android.app.Application
import android.util.Log
import com.jcraft.jsch.JSch
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties

@HiltAndroidApp
class HeroApp : Application(){
    override fun onCreate() {
        super.onCreate()

        // forward port to helios
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val config = Properties()
                config["StrictHostKeyChecking"] = "no"
                val jsch = JSch()
                val session = jsch.getSession("username", "helios.cs.ifmo.ru", 2222)
                session.setConfig(config)
                session.setPassword("password")
                session.connect()
                if (session != null && session.isConnected) {
                    session.setPortForwardingL(8080, "localhost", 8088)
                    Log.d("port_forwarding", "session is connected (session = $session)" +
                            "\n\tsession.host=${session.host}" +
                            "\n\tsession.port=${session.port}" +
                            "\n\tsession.userInfo=${session.userInfo}" +
                            "\n\tsession.userName=${session.userName}" +
                            "\n\tsession.timeout=${session.timeout}")
                } else {
                    Log.d("port_forwarding", "session is null or not connected (session = $session)")
                }
            } catch (e: Exception) {
                Log.d("port_forwarding", "error = ${e.message}")
            }
        }
    }
}
