import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

object MainBrain {

    private const val width = 500
    private const val height = 500
    private val imageState = MutableStateFlow(BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB))

    private val keyInputState = MutableStateFlow<Set<KeyboardInputAdapter.KeyState>>(setOf())
    private val keyListener = KeyboardInputAdapter(keyInputState)

    private val entities = mutableListOf<Entity>()

    init {
        // Put ins some "spites."
        for (j in 0..50) {
            for (k in 0..50) {
                entities.add(Entity(j * 10, k * 10))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {

        val isPaused = AtomicBoolean(false)

        val gameFrame = GameFrame("Let's try and animations! 2021", width, height, imageState)
        gameFrame.setKeyListener(keyListener)
        gameFrame.showFrame()

        while (true) {

            // TODO: process input
            keyInputState.value.forEach { state ->

                when (state) {

                    KeyboardInputAdapter.KeyState.PAUSE -> {
                        isPaused.set(!isPaused.get())
                    }

                    KeyboardInputAdapter.KeyState.QUIT -> {
                        exitProcess(0)
                    }
                }
            }

            // Clear all input state
            keyInputState.value = setOf()

            if (!isPaused.get()) {
                update()
                render()
            }

            Thread.sleep(1000/60);
        }
    }

    private fun update() {
        entities.forEach { entity ->
            entity.update()
        }
    }

    private fun render() {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.graphics as Graphics2D
        g.color = Color.BLACK
        g.fillRect(0, 0, width, height)

        entities.forEach { entity ->
            g.color = entity.color
            g.drawRect(entity.x, entity.y, 5, 5)
        }

        g.color = Color.RED
        g.drawString("${System.currentTimeMillis()}", 50.0f, 50.0f)
        g.dispose()

        imageState.value = image
    }
}