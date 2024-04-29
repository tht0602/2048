package tzu.hsuan.tseng.tooowlforate.service

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import tzu.hsuan.tseng.tooowlforate.R
import java.lang.Exception

class ForegroundTestService:Service() {

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when(intent.action){
            Action.START.toString() -> start()
            Action.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){

        try {
            var mBuilder = NotificationCompat.Builder(this, "foreground_service")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("ha")
                .setContentText("pp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startForeground(1, mBuilder.build())
            }
        }catch (e:Exception){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException) {
                // App not in a valid state to start foreground service
                // (e.g. started from bg)
            }
        }
    }

    enum class Action{
        START, STOP
    }
}