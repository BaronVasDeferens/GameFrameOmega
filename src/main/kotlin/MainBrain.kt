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

    private val entities = mutableListOf<Entity>()
    private val sprites = mutableListOf<Sprite>()

    // private val playerSprite = Sprite(50, 100, "sprite1.png")
//    private val mouseSprite = Mouse(100, 100, "mouse.png")
//    private val mech = Mech(200, 200)
    private val hero = PlayerTank(64, 64)

    private val robo = RobotDrone(400, 400)
    private val cpu = CpuClock(600, 600)

    init {

        val isPaused = AtomicBoolean(false)

        val gameFrame = GameFrame("Omega Blaster", windowWidth, windowHeight, imageState)
        gameFrame.setKeyListener(keyListener)
        gameFrame.setMouseAdapter(mouseListener)
        gameFrame.showFrame()

        thread {
            while (true) {

                // TODO: process input
//            playerSprite.move(keyInputState.value)
//            mouseSprite.move(keyInputState.value)
//            mech.move(keyInputState.value)
                hero.move(keyInputState.value, mouseInputState.value)


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
                sleep(5)
                //sleep(1000 / 60);
            }
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
//        mech.update()
        hero.update()
        robo.update()


        map.moveWindow(hero)


    }

    private fun render() {
        val image = BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB)
        val g = image.graphics as Graphics2D
        g.color = Color.BLACK
        g.fillRect(0, 0, windowWidth, windowHeight)

        map.render(listOf(hero, robo, cpu), g)

//        entities.forEach { entity ->
//            g.color = entity.color
//            g.drawRect(entity.x, entity.y, 5, 5)
//        }
//
//        sprites.forEach { sprite ->
//            sprite.render(g)
//        }

//        playerSprite.render(g)
//        mouseSprite.render(g)
//        mech.render(g)
//        hero.render(g)

        g.dispose()

        imageState.value = image
    }
}