import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO


class Hero (
    override var x: Int,
    override var y: Int,
    override val movementPerUpdate: Int = 4,
    spriteFileName: String = "Character1Walk.png",

): Player(x,y, movementPerUpdate), Renderable {
    private var spriteSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(spriteFileName))
    private val frameRow = AtomicInteger(0)
    private val frameColumn = AtomicInteger(0)

    val ticksPerFrame = 4
    private val maxColumns = 8
    private val frameSize = 64
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
                    frameRow.set(3)
                    x += movementPerUpdate
                }

                KeyboardInputAdapter.KeyState.MOVE_DOWN -> {
                    frameRow.set(2)
                    y += movementPerUpdate
                }

                KeyboardInputAdapter.KeyState.MOVE_LEFT -> {
                    frameRow.set(1)
                    x -= movementPerUpdate
                }

                else -> {

                }
            }
        }
    }

    override fun update() {
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

    override fun setWindowPosition(posX: Int, posY: Int) {

    }

    override fun render(graphics2D: Graphics2D) {
        val targetFrame = spriteSheet.getSubimage(frameColumn.get() * frameSize, frameRow.get() * frameSize, frameSize, frameSize)
        graphics2D.drawImage(targetFrame, x, y, null)
    }

}