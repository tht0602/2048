package tzu.hsuan.tseng.tooowlforate

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import tzu.hsuan.tseng.tooowlforate.databinding.ActivityBottomSheetBinding

class BottomSheetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomSheetBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var callback: BottomSheetBehavior.BottomSheetCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomSheetBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        initView()
        initListener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        bottomSheetBehavior.peekHeight = binding.aView.height
        Log.d("BottomSheetActivity", "height = ${binding.flBottomSheet.height}")
        updateFloatTranslation()

    }

    private fun initView() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.flBottomSheet)
        bottomSheetBehavior.isFitToContents = false
        changeToAThenHalfExpend()
    }

    private fun initListener() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("BottomSheetActivity", "slideOffset = ${slideOffset}")
                Log.d("BottomSheetActivity", "top = ${bottomSheet.top}")
                updateFloatTranslation()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("BottomSheetActivity", "onStateChanged = ${newState}")
            }
        })
        binding.btnBottomSheet.setOnClickListener {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_HALF_EXPANDED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
                BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        binding.btnHideBottomSheet.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_DRAGGING && bottomSheetBehavior.state != BottomSheetBehavior.STATE_SETTLING) {
                if (binding.bView.isInvisible) {
                    animateHideAndShowNewContent { changeToBThenCollapsed() }
                } else {
                    animateHideAndShowNewContent { changeToAThenHalfExpend() }
                }
            }
        }
    }

    private fun updateFloatTranslation(){
        val bottomSheet = binding.flBottomSheet
        Log.d("BottomSheetActivity", "max height = ${bottomSheet.height * bottomSheetBehavior.halfExpandedRatio}")
        val translation = maxOf(
            (bottomSheet.top - bottomSheet.height).toFloat(),
            - bottomSheet.height * bottomSheetBehavior.halfExpandedRatio
        )
        Log.d("BottomSheetActivity", "translation = ${translation}")
        binding.fab1.translationY = translation
        binding.fab2.translationY = translation
    }

    private fun animateHideAndShowNewContent(nextStepOnHidden: () -> Unit) {
        callback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    nextStepOnHidden.invoke()
                    bottomSheetBehavior.isHideable = false
                }
            }
        }
        bottomSheetBehavior.isHideable = true
        callback?.let { bottomSheetBehavior.addBottomSheetCallback(it) }
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun changeToAThenHalfExpend() {
        binding.bView.isInvisible = true
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.peekHeight = binding.aView.height
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        callback?.let { bottomSheetBehavior.removeBottomSheetCallback(it) }
    }

    private fun changeToBThenCollapsed() {
        binding.bView.isVisible = true
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.peekHeight = binding.bView.height
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        callback?.let { bottomSheetBehavior.removeBottomSheetCallback(it) }
    }
}