package mazescroller

import KeyboardInputAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    MazeScroller()
}


class MazeScroller {

    private val gameFrameWidth = 1000
    private val gameFrameHeight = 800

    private val gameRunning = AtomicBoolean(true)

    // Input: Keyboard
    private val keyInputState = MutableStateFlow<Set<KeyboardInputAdapter.KeyState>>(setOf())
    private val keyListener = KeyboardInputAdapter(keyInputState)

    private val bufferedImageFlow =
        MutableStateFlow(BufferedImage(gameFrameWidth, gameFrameHeight, BufferedImage.TYPE_INT_ARGB))
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        val gameFrame = GameFrameEX("Maze Scroller", gameFrameWidth, gameFrameHeight, bufferedImageFlow, scope)
        gameFrame.setKeyListener(keyListener)
        gameFrame.showFrame()


        while (gameRunning.get()) {

            keyInputState.value.forEach { state ->
                when (state) {

                    KeyboardInputAdapter.KeyState.QUIT -> {
                        gameRunning.set(false)
                        exitProcess(0)
                    }

                    else -> {

                    }
                }
            }

            update()
            render()
        }

    }

    private fun update() {

    }

    private fun render() {
        val image = BufferedImage(gameFrameWidth, gameFrameHeight, BufferedImage.TYPE_INT_ARGB)
        val g = image.graphics as Graphics2D

        g.dispose()
        bufferedImageFlow.value = image
    }

}

