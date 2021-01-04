import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.random.Random

data class Sprite(var x: Int, var y: Int, val imageFileName: String) {

    companion object {
        val random = Random(System.currentTimeMillis())
        fun randomRange(min: Int, max: Int): Int {
            return random.nextInt(max) + min
        }
    }


    private var image: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(imageFileName))

    // sprite1.png is 92x74px per frame
    private val width = 32
    private val height = 74;

    private var subImage = image.getSubimage(0, 0, width, height)

    private val spriteArrayMax = 5 // 6 = frames 0 - 5
    private val currentSpriteIndex = AtomicInteger(randomRange(0, 5));
    private val updateAfterTicks = 10
    private val currentTick = AtomicInteger(0)


    private val movementAllotment = 2;

    fun move(directions: Set<KeyboardInputAdapter.KeyState>) {

        directions.forEach { state ->

            when (state) {

                KeyboardInputAdapter.KeyState.MOVE_UP -> {
                    y -= movementAllotment
                }

                KeyboardInputAdapter.KeyState.MOVE_DOWN -> {
                    y += movementAllotment
                }

                KeyboardInputAdapter.KeyState.MOVE_LEFT -> {
                    x -= movementAllotment
                }

                KeyboardInputAdapter.KeyState.MOVE_RIGHT -> {
                    x += movementAllotment
                }
                else -> {

                }
            }
        }

    }

    fun update() {

        // x += 2 // move to the right for funsies
        // if (x > 500) x = -40

        if (currentTick.incrementAndGet() == updateAfterTicks) {
            currentTick.set(0)

            if (currentSpriteIndex.incrementAndGet() > spriteArrayMax) {
                currentSpriteIndex.set(0)
            }
            subImage = image.getSubimage(width * currentSpriteIndex.get(), 0, width, height)
        }
    }

    fun render(graphics2D: Graphics2D) {
        graphics2D.drawImage(subImage, x, y, null)
    }
}

data class Entity(var x: Int, var y: Int, var color: Color = Color.WHITE) {

    companion object {
        val random = Random(System.currentTimeMillis())
    }

    fun update() {
        x += random.nextInt(2) * if (random.nextInt(2) == 1) 1 else -1
        y += random.nextInt(2) * if (random.nextInt(2) == 1) 1 else -1
    }
}