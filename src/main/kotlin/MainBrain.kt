import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    MainBrain()
}

class MainBrain() {

    private val blockSize = 64
    private val rows = 20
    private val columns = 20

    /**
     * When the windowWidth is less than blockSize * columns, the mpa will scroll with player movement
     */
    private val windowWidth = 750
    private val windowHeight = 750
    private val map = WorldMap(blockSize, columns, rows, windowWidth = windowWidth, windowHeight = windowHeight)

    private val imageState = MutableStateFlow(BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB))

    private val keyInputState = MutableStateFlow<Set<KeyboardInputAdapter.KeyState>>(setOf())
    private val keyListener = KeyboardInputAdapter(keyInputState)

    private val mouseInputState = MutableStateFlow(MouseState())
    private val mouseListener = MouseInputAdapter(mouseInputState)

    private val hero = PlayerTank(64, 64)
    private val renderables = mutableListOf<Renderable>()

    init {

        renderables.add(RobotDrone(400, 400))
        renderables.add(CpuClock(500, 600))
        renderables.add(hero)

        val isPaused = AtomicBoolean(false)

        val gameFrame = GameFrame("Omega Blaster", windowWidth, windowHeight, imageState)
        gameFrame.setKeyListener(keyListener)
        gameFrame.setMouseAdapter(mouseListener)
        gameFrame.showFrame()


        while (true) {

            keyInputState.value.forEach { state ->
                when (state) {

                    KeyboardInputAdapter.KeyState.PAUSE -> {
                        isPaused.set(!isPaused.get())
                    }

                    KeyboardInputAdapter.KeyState.QUIT -> {
                        exitProcess(0)
                    }

                    else -> {

                    }
                }
            }

            if (!isPaused.get()) {
                hero.move(keyInputState.value, mouseInputState.value)
                update()
                render()
            } else {
                println("PAUSED!")
            }
            sleep(5)
        }
    }

    private fun update() {
        renderables.forEach { sprite ->
            sprite.update()
        }

        map.moveWindow(hero)
    }

    private fun render() {
        val image = BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB)
        val g = image.graphics as Graphics2D
        //g.clearRect(0, 0, windowWidth, windowHeight)

        map.render(renderables, g)
        g.dispose()

        imageState.value = image
    }
}