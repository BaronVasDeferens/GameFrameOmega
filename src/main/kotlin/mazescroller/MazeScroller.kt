package mazescroller

import KeyboardInputAdapter
import MouseState
import PlayerTank
import Renderable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    MazeScroller()
}


class MazeScroller {

    private val gameFrameWidth = 650
    private val gameFrameHeight = 675

    private val mazeRoomSize = 64
    private val maze = Maze(50, 50, mazeRoomSize, gameFrameWidth, gameFrameHeight)


    private val playerPiece = PlayerTank(100, 100, movementPerUpdate = 1)
    private val renderables = mutableListOf<Renderable>()

    // Input: Keyboard
    private val keyInputState = MutableStateFlow<Set<KeyboardInputAdapter.KeyState>>(setOf())
    private val keyListener = KeyboardInputAdapter(keyInputState)

    // Finished renders are published to this flow
    private val bufferedImageFlow =
        MutableStateFlow(BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB))

    // Private coroutine scope
    private val scope = CoroutineScope(Dispatchers.Default)

    private val isGameRunning = AtomicBoolean(true)


    init {

        renderables.add(playerPiece)

        val gameFrame = GameFrameEX("Maze Scroller", gameFrameWidth, gameFrameHeight, bufferedImageFlow, scope)
        gameFrame.setKeyListener(keyListener)
        gameFrame.showFrame()

        // Allow the window to finish rendering (fixes issue where screen would go blank)
        runBlocking {
            delay(500)
        }

        bufferedImageFlow.value = maze.cropToWindow(0, 0, gameFrameWidth, gameFrameHeight)


        /**
         * MAIN LOOP
         */
        while (isGameRunning.get()) {

            keyInputState.value.forEach { state ->
                when (state) {

                    KeyboardInputAdapter.KeyState.QUIT -> {
                        isGameRunning.set(false)
                    }

                    else -> {

                    }
                }
            }

            playerPiece.move(keyInputState.value, MouseState())
            update()
            render()
//            sleep(5L)
        }
        println("Main loop terminated")
        scope.cancel()
        exitProcess(0)
    }

    private fun update() {
        renderables.forEach { sprite ->
            sprite.update()
        }

        maze.moveWindow(playerPiece)
    }

    private fun render() {
        bufferedImageFlow.value = maze.render(renderables)
    }

}

