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

    private val mazeRoomSize = 64
    private val maze = Maze(50, 50)

    private val gameFrameWidth = 650
    private val gameFrameHeight = 675


    // Input: Keyboard
    private val keyInputState = MutableStateFlow<Set<KeyboardInputAdapter.KeyState>>(setOf())
    private val keyListener = KeyboardInputAdapter(keyInputState)

    // Finished renders are published to this flow
    private val bufferedImageFlow =
        MutableStateFlow(BufferedImage(gameFrameWidth, gameFrameHeight, BufferedImage.TYPE_INT_ARGB))

    // Private coroutine scope
    private val scope = CoroutineScope(Dispatchers.Default)

    private val gameRunning = AtomicBoolean(true)


    init {

        val mazeRender = maze.renderMazeToPixmap(
            gameFrameWidth,
            gameFrameHeight,
            0,
            0,
            10,
            mazeRoomSize
        )

        bufferedImageFlow.value = maze.cropToWindow(
            mazeRender,
            0,
            0,
            640,
            640
        )

        val gameFrame = GameFrameEX("Maze Scroller", gameFrameWidth, gameFrameHeight, bufferedImageFlow, scope)
        gameFrame.setKeyListener(keyListener)
        gameFrame.showFrame()


        /**
         * MAIN LOOP
         */
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

