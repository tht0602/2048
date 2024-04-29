package tzu.hsuan.tseng.tooowlforate

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import tzu.hsuan.tseng.tooowlforate.service.ForegroundTestService

class ForegroundServiceActivity:Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)
        findViewById<View>(R.id.btn_broadcast).setOnClickListener {
            //Toast.makeText(this, "安安", Toast.LENGTH_SHORT).show()
            Intent(this, ForegroundTestService::class.java).also {
                it.action = ForegroundTestService.Action.START.toString()
                startService(it)
            }
        }

        val channel = NotificationChannel(
            "foreground_service",
            "Foreground Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        val i = Intent(this, ForegroundTestService::class.java)
        stopService(i)
        super.onDestroy()
    }
}