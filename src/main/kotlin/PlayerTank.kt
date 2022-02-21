import KeyboardInputAdapter.KeyState.MOVE_UP
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

abstract class Player(
    open var x: Int,
    open var y: Int,
    open val movementPerUpdate: Int,
    val spriteSize: Int = 64
) {
    val isMoving = AtomicBoolean(false)
}

class PlayerTank(
    override var x: Int,
    override var y: Int,
    override val movementPerUpdate: Int = 4,
    spriteFileName: String = "tank_sprite_sheet.png"
) : Player(x, y, movementPerUpdate), Renderable {
    private var spriteSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(spriteFileName))
    private val frameColumn = AtomicInteger(0)
    private val frameRow = AtomicInteger(0)

    val ticksPerFrame = 4
    private val maxColumns = 4
    private val frameSize = 64
    private val currentTicks = AtomicInteger(0)


    private var lastDirection = MOVE_UP
    private val coastingDeltaMax = 10
    private var coastingDeltaCurrent = 0

    fun move(directions: Set<KeyboardInputAdapter.KeyState>) {

        isMoving.set(directions.isNotEmpty())

        if (isMoving.get()) {

            lastDirection = directions.first()
            coastingDeltaCurrent = 1

            when (directions.first()) {

                MOVE_UP -> {
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
        } else {

        }
    }

    fun update() {
        if (isMoving.get()) {

            coastingDeltaCurrent = 1

            if (currentTicks.incrementAndGet() >= ticksPerFrame) {
                frameColumn.set(frameColumn.incrementAndGet() % maxColumns)
                currentTicks.set(0)
            } else {

            }
        } else {

            if (coastingDeltaCurrent > 0) {
                coastingDeltaCurrent++
            }

            if (coastingDeltaCurrent >= coastingDeltaMax) {
                coastingDeltaCurrent = 0
            }


            // Apply drift (if any)
            if (coastingDeltaCurrent > 0) {

                if (currentTicks.incrementAndGet() >= ticksPerFrame) {
                    frameColumn.set(frameColumn.incrementAndGet() % maxColumns)
                    currentTicks.set(0)
                }


                val driftAmount = (coastingDeltaMax / coastingDeltaCurrent)

                println("dir: $lastDirection delta: $coastingDeltaCurrent amt: $driftAmount")

                when (lastDirection) {

                    MOVE_UP -> {
                        y -= driftAmount
                        frameRow.set(0)
                    }

                    KeyboardInputAdapter.KeyState.MOVE_RIGHT -> {
                        x += driftAmount
                        frameRow.set(1)
                    }

                    KeyboardInputAdapter.KeyState.MOVE_DOWN -> {
                        y += driftAmount
                        frameRow.set(2)
                    }

                    KeyboardInputAdapter.KeyState.MOVE_LEFT -> {
                        x -= driftAmount
                        frameRow.set(3)
                    }

                    else -> {

                    }
                }
            }


            // put mouse in "neutral" frame
//            frameRow.set(2)
//            frameColumn.set(1)
        }



//        println("row: ${frameRow.get()} col: ${frameColumn.get()}")
    }

    override fun render(graphics2D: Graphics2D) {
        val targetFrame =
            spriteSheet.getSubimage(frameColumn.get() * frameSize, frameRow.get() * frameSize, frameSize, frameSize)
        graphics2D.drawImage(targetFrame, x, y, null)
    }

}