package tzu.hsuan.tseng.tooowlforate

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast

class BroadcastActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)
        findViewById<View>(R.id.btn_broadcast).setOnClickListener {
            //Toast.makeText(this, "安安", Toast.LENGTH_SHORT).show()
            sendBroadcast(Intent("local.action.anan"))
        }

        val broadcastReceiver = object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(context, "安安2", Toast.LENGTH_SHORT).show()
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("local.action.anan"))
    }
}