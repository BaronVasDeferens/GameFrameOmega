import KeyboardInputAdapter.KeyState
import KeyboardInputAdapter.KeyState.*
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.math.atan

abstract class Player(
    open var x: Int,
    open var y: Int,
    open val movementPerUpdate: Int,
    val spriteSize: Int = 64
): Renderable {
    val isMoving = AtomicBoolean(false)
    val isCoasting = AtomicBoolean(false)
}

class PlayerTank(
    override var x: Int,
    override var y: Int,
    override val movementPerUpdate: Int = 2,
    spriteFileName: String = "tank_body_sprite_sheet.png"
) : Player(x, y, movementPerUpdate) {
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

    private var currentMouseX: Int = 0
    private var currentMouseY: Int = 0
    private var turretOrientation = 0.0
    var opposite: Double = 0.0
    var adjacent: Double = 0.0

    val projectiles = mutableListOf<Projectile>()

    fun move(directions: Set<KeyState>, mouseState: MouseState) {

        isMoving.set(directions.isNotEmpty())

        if (isMoving.get()) {

            lastDirection = directions.first()
            coastingDeltaCurrent = 1

            when (directions.first()) {

                MOVE_UP -> {
                    y -= movementPerUpdate
                    frameRow.set(0)
                }

                MOVE_RIGHT -> {
                    x += movementPerUpdate
                    frameRow.set(1)
                }

                MOVE_DOWN -> {
                    y += movementPerUpdate
                    frameRow.set(2)
                }

                MOVE_LEFT -> {
                    x -= movementPerUpdate
                    frameRow.set(3)
                }

                else -> {

                }
            }
        } else {

        }

        if (mouseState.firing) {
            println("PEW!")
        }

        val centerX = x + 32
        val centerY = y + 32

        mouseState.mouseEvent?.apply {

            val barrelLength = 32

            currentMouseX = this.x
            currentMouseY = this.y


            // Quadrants I & II
            if (currentMouseX < centerX) {

                turretOrientation = if (currentMouseY < centerY) {
//                    println("Q2")
                    opposite = (centerY - currentMouseY).toDouble()
                    adjacent = (centerX - currentMouseX).toDouble()
                    atan(opposite / adjacent) * (180 / Math.PI)

                } else {
//                    println("Q3")
                    90 + atan(opposite / adjacent) * (180 / Math.PI)
                }
            } else {

                turretOrientation = if (currentMouseY < centerY) {
//                    println("Q1")
                    180 + atan(opposite / adjacent) * (180 / Math.PI)
                } else {
//                    println("Q4")
                    270 + atan(opposite / adjacent) * (180 / Math.PI)
                }
            }

//            println("($x $y) ($opposite, $adjacent) $turretOrientation ${mouseState.firing}")
//            println("($centerX $centerY) ($currentMouseX, $currentMouseY) ${Math.floor(turretOrientation)}")

        }

    }

    override fun update() {
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

                isCoasting.set(true)
                if (currentTicks.incrementAndGet() >= ticksPerFrame) {
                    frameColumn.set(frameColumn.incrementAndGet() % maxColumns)
                    currentTicks.set(0)
                }

                val driftAmount = (coastingDeltaMax / coastingDeltaCurrent)

                when (lastDirection) {

                    MOVE_UP -> {
                        y -= driftAmount
                        frameRow.set(0)
                    }

                    MOVE_RIGHT -> {
                        x += driftAmount
                        frameRow.set(1)
                    }

                    MOVE_DOWN -> {
                        y += driftAmount
                        frameRow.set(2)
                    }

                    MOVE_LEFT -> {
                        x -= driftAmount
                        frameRow.set(3)
                    }

                    else -> {

                    }
                }
            } else {
                isCoasting.set(false)
            }
        }
    }

    override fun render(graphics2D: Graphics2D) {
        // Find the correct sub-frame within the sprite sheet
        val targetFrame =
            spriteSheet.getSubimage(frameColumn.get() * frameSize, frameRow.get() * frameSize, frameSize, frameSize)
        graphics2D.drawImage(targetFrame, x, y, null)

        // draw "turret"
//        graphics2D.color = Color.RED
//        graphics2D.drawLine(x + 32, y + 32, currentMouseX, currentMouseY)

    }

}

class Projectile (val genesisX: Int, val genesisY: Int, val theta: Float, val speedPerTick: Int, val image: BufferedImage): Renderable {
    override fun update() {
    }

    override fun render(graphics2D: Graphics2D) {

    }

}