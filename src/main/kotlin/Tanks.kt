import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

enum class Orientation {
    UP,
    RIGHT,
    DOWN,
    LEFT
}

class Mouse(
    var x: Int,
    var y: Int,
    // var turretOrientation: Orientation,
    spriteFileName: String
) {
    private var spriteSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(spriteFileName))
    private val frameRow = AtomicInteger(0)
    private val frameColumn = AtomicInteger(0)

    private val isMoving = AtomicBoolean(false)
    private val movementPerUpdate = 1
    private val ticksPerFrame = 8
    private val currentTicks = AtomicInteger(0)



    fun move(directions: Set<KeyboardInputAdapter.KeyState>) {

        isMoving.set(directions.isNotEmpty())

        if (isMoving.get()) {

            when (directions.first()) {

                KeyboardInputAdapter.KeyState.MOVE_UP -> {
                    frameRow.set(0)
                    y -= movementPerUpdate
                }

                KeyboardInputAdapter.KeyState.MOVE_RIGHT -> {
                    frameRow.set(1)
                    x += movementPerUpdate
                }

                KeyboardInputAdapter.KeyState.MOVE_DOWN -> {
                    frameRow.set(2)
                    y += movementPerUpdate
                }

                KeyboardInputAdapter.KeyState.MOVE_LEFT -> {
                    frameRow.set(3)
                    x -= movementPerUpdate
                }

                else -> {

                }
            }
        }
    }

    fun update() {
        if (isMoving.get()) {
            println(currentTicks.get())

            if (currentTicks.incrementAndGet() >= ticksPerFrame) {

                frameColumn.set(frameColumn.incrementAndGet() % 2)
                currentTicks.set(0)
            } else {

            }
        } else {

            // put mouse in "neutral" frame
//            frameRow.set(2)
//            frameColumn.set(1)
        }

//        println("row: ${frameRow.get()} col: ${frameColumn.get()}")
    }

    fun render(graphics2D: Graphics2D) {
        val targetFrame = spriteSheet.getSubimage(frameColumn.get() * 32, frameRow.get() * 32, 32, 32)
        graphics2D.drawImage(targetFrame, x, y, null)
    }

}