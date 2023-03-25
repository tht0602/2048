package tzu.hsuan.tseng.tooowlforate

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import tzu.hsuan.tseng.tooowlforate.databinding.Activity2048Binding
import kotlin.random.Random

class TooOwlForAteActivity : Activity() {
    private lateinit var binding: Activity2048Binding
    private var touchStartX = 0;
    private var touchStartY = 0;
    private var moveTriggered = true;
    private var triggerDistance = 100;
    private var array = Array(4) { Array(4) { 0 } }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity2048Binding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        binding.root.setOnTouchListener { v, event ->
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.x.toInt()
                    touchStartY = event.y.toInt()
                    moveTriggered = false
                }
                event.action == MotionEvent.ACTION_MOVE && !moveTriggered -> {
                    when {
                        touchStartX - event.x.toInt() > triggerDistance -> {
//                            binding.tvTest.text = "左"
                            moveCardsHorizon()
                            moveTriggered = true
                        }
                        touchStartX - event.x.toInt() < -triggerDistance -> {
//                            binding.tvTest.text = "右"
                            moveCardsHorizon(reverse = true)
                            moveTriggered = true
                        }
                        touchStartY - event.y.toInt() > triggerDistance -> {
//                            binding.tvTest.text = "上"
                            moveCardsVertical()
                            moveTriggered = true
                        }
                        touchStartY - event.y.toInt() < -triggerDistance -> {
//                            binding.tvTest.text = "下"
                            moveCardsVertical(reverse = true)
                            moveTriggered = true
                        }
                    }
                }
                event.action == MotionEvent.ACTION_UP -> v.performClick()
            }
            return@setOnTouchListener true
        }

        startGame()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun startGame() {
        array = Array(4) { Array(4) { 0 } }
        array[2][2] = 2
        array[1][2] = 2
        showArray()
    }

    private fun moveCardsHorizon(reverse: Boolean = false) {
        var moved = false
        for (y in 0..3) {
            if (reverse) array[y].reverse()
            var mergedX = -1
            for (x in 0..3) {
                val temp = array[y][x]
                if (array[y][x] != 0) {
                    var moveToX = x
                    while (moveToX > 0) {
                        if (mergedX != moveToX - 1 && array[y][moveToX - 1] == temp) {
                            array[y][moveToX - 1] = temp * 2
                            array[y][moveToX] = 0
                            moved = true
                            mergedX = moveToX - 1
                            break
                        }
                        if (array[y][moveToX - 1] != 0) {
                            break
                        }
                        array[y][moveToX - 1] = temp
                        array[y][moveToX] = 0
                        moved = true
                        moveToX -= 1
                    }
                }
            }
            if (reverse) array[y].reverse()
        }
        nextStep(moved)
    }

    private fun moveCardsVertical(reverse: Boolean = false) {
        var moved = false
        if (reverse) array.reverse()
        for (x in 0..3) {
            var mergedY = -1
            for (y in 0..3) {
                val temp = array[y][x]
                if (array[y][x] != 0) {
                    var moveToY = y
                    while (moveToY > 0) {
                        if (mergedY != moveToY - 1 && array[moveToY - 1][x] == temp) {
                            array[moveToY - 1][x] = temp * 2
                            array[moveToY][x] = 0
                            moved = true
                            mergedY = moveToY - 1
                            break
                        }
                        if (array[moveToY - 1][x] != 0) {
                            break
                        }
                        array[moveToY - 1][x] = temp
                        array[moveToY][x] = 0
                        moved = true
                        moveToY -= 1
                    }
                }
            }
        }
        if (reverse) array.reverse()
        nextStep(moved)
    }

    private fun nextStep(moved: Boolean) {
        if (moved) {
            var zeroCount = 0
            array.forEach {
                it.forEach { it2 ->
                    if (it2 == 0) {
                        zeroCount++
                    }
                }
            }
            if (zeroCount > 0) {
                var nextAddSlot = Random.nextInt(zeroCount)
                array.forEach {
                    it.forEachIndexed { i, it2 ->
                        if (it2 == 0) {
                            if (nextAddSlot == 0) {
                                it[i] = 2
                            }
                            nextAddSlot--
                        }
                    }
                }
            }
        }
        showArray()
    }

    private fun showArray() {
        var string = ""
        array.forEach {
            string += it.contentToString() + "\n"
        }
        binding.tvTest.text = string
    }
}