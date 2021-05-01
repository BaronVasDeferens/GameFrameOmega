import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    MainBrain()
}

class MainBrain() {

    private val width = 500
    private val height = 500
    private val imageState = MutableStateFlow(BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB))

    private val keyInputState = MutableStateFlow<Set<KeyboardInputAdapter.KeyState>>(setOf())
    private val keyListener = KeyboardInputAdapter(keyInputState)

    private val entities = mutableListOf<Entity>()
    private val sprites = mutableListOf<Sprite>()
    private val playerSprite = Sprite(50, 100, "sprite1.png")

    private val mouseSprite = Mouse(100, 100, "mouse.png")
    private val mech = Mech(200, 200)

    init {

        val isPaused = AtomicBoolean(false)

        val gameFrame = GameFrame("Let's try and animations! 2021", width, height, imageState)
        gameFrame.setKeyListener(keyListener)
        gameFrame.showFrame()

        while (true) {

            // TODO: process input
            // playerSprite.move(keyInputState.value)
//                mouseSprite.move(keyInputState.value)
            mech.move(keyInputState.value)


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

            if (!isPaused.get()) {
                update()
                render()
            }

            Thread.sleep(1000 / 60);
        }
    }

    private fun update() {
        entities.forEach { entity ->
            entity.update()
        }

        sprites.forEach { sprite ->
            sprite.update()
        }

//        playerSprite.update()
//        mouseSprite.update()
        mech.update()
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

        sprites.forEach { sprite ->
            sprite.render(g)
        }

//        playerSprite.render(g)
//        mouseSprite.render(g)
        mech.render(g)
        g.dispose()

        imageState.value = image
    }
}