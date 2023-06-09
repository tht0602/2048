package tzu.hsuan.tseng.tooowlforate

import android.app.Activity
import android.os.Bundle
import tzu.hsuan.tseng.tooowlforate.safering.SafeCheckRingView

class SafeRingActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_ring)
        val checkRingView = findViewById<SafeCheckRingView>(R.id.check_ring)
        checkRingView.setOnClickListener {
            checkRingView.numerator += 1
        }
    }
}