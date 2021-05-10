import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

abstract class Player(open var x: Int,
                      open var y: Int,
                      open val movementPerUpdate: Int,
                      val spriteSize: Int = 64
                      ) {
    val isMoving = AtomicBoolean(false)
}

class PlayerTank (
    override var x: Int,
    override var y: Int,
    override val movementPerUpdate: Int = 2,
    spriteFileName: String = "tank_sprite_sheet.png"
): Player(x,y, movementPerUpdate) , Renderable {
    private var spriteSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(spriteFileName))
    private val frameColumn = AtomicInteger(0)
    private val frameRow = AtomicInteger(0)

    val ticksPerFrame = 4
    private val maxColumns = 4
    private val frameSize = 64
    private val currentTicks = AtomicInteger(0)


    fun move(directions: Set<KeyboardInputAdapter.KeyState>) {

        isMoving.set(directions.isNotEmpty())

        if (isMoving.get()) {

            when (directions.first()) {

                KeyboardInputAdapter.KeyState.MOVE_UP -> {
                    y -= movementPerUpdate
                    frameRow.set(0)
                }

                KeyboardInputAdapter.KeyState.MOVE_RIGHT -> {
                    x += movementPerUpdate
                    frameRow.set(1)
                }

                KeyboardInputAdapter.KeyState.MOVE_DOWN -> {
                    y += movementPerUpdate
                    frameRow.set(2)
                }

                KeyboardInputAdapter.KeyState.MOVE_LEFT -> {
                    x -= movementPerUpdate
                    frameRow.set(3)
                }

                else -> {

                }
            }
        }
    }

    fun update() {
        if (isMoving.get()) {
            if (currentTicks.incrementAndGet() >= ticksPerFrame) {
                frameColumn.set(frameColumn.incrementAndGet() % maxColumns)
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

    override fun render(graphics2D: Graphics2D) {
        val targetFrame = spriteSheet.getSubimage(frameColumn.get() * frameSize, frameRow.get() * frameSize, frameSize, frameSize)
        graphics2D.drawImage(targetFrame, x, y, null)
    }

}