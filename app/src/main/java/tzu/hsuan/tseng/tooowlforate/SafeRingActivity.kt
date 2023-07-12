package tzu.hsuan.tseng.tooowlforate

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import tzu.hsuan.tseng.tooowlforate.safering.SafeCheckRingAnimateView

class SafeRingActivity : Activity() {

    var process = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_ring)
        val checkRingView = findViewById<SafeCheckRingAnimateView>(R.id.check_ring)
        checkRingView.setOnClickListener {
            process = 8
            checkRingView.setProgressByAnimate(process, 8) {
                // 動畫結束的 callback
//                Toast.makeText(this@SafeRingActivity, "安安", Toast.LENGTH_SHORT).show()
//                Log.d("2048", "安安")
            }
        }
        val backGroundView = findViewById<ConstraintLayout>(R.id.check_ring_bg)
        backGroundView.setOnClickListener {
            process += 1
            if(process > 8) process = 0
            checkRingView.setProgressByAnimate(process, 8)
        }
    }
}