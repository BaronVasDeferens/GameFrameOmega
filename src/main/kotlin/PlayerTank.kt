import KeyboardInputAdapter.KeyState
import KeyboardInputAdapter.KeyState.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

abstract class Player(
    open var x: Int,
    open var y: Int,
    open val movementPerUpdate: Int,
    val spriteSize: Int = 64
) : Renderable {
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
    private val coastingDeltaMax = movementPerUpdate * 4
    private var coastingDeltaCurrent = 0

    private var currentMouseX: Int = 0
    private var currentMouseY: Int = 0
    private var turretOrientation = 0.0
    private val turretLength = 20
    private var opposite: Double = 0.0
    private var adjacent: Double = 0.0

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

        val centerX = x + frameSize / 2
        val centerY = y + frameSize / 2

        mouseState.mouseEvent?.apply {

            currentMouseX = this.x
            currentMouseY = this.y

            opposite = (centerY - currentMouseY).toDouble()
            adjacent = (centerX - currentMouseX).toDouble()

            // Quadrants I & II
            if (currentMouseX > centerX) {

                turretOrientation = if (currentMouseY < centerY) {
//                    println("Q2")
                    atan(opposite / adjacent)
                } else {
//                    println("Q3")
                    atan(opposite / adjacent)
                }
            } else {
                turretOrientation = if (currentMouseY < centerY) {
                    // println("Q1")
                    Math.PI + atan(opposite / adjacent)
                } else {
//                    println("Q4")
                    Math.PI + atan(opposite / adjacent)
                }
            }
        }

        // Handle FIRING
        if (mouseState.firing) {
            projectiles.add(
                Projectile(
                    x + (frameSize / 2),
                    y + (frameSize / 2),
                    turretOrientation.toFloat(),
                    20,
                    ImageIO.read(javaClass.classLoader.getResourceAsStream("projectile_16x16.png"))
                )
            )
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

        projectiles.forEach {
            it.update()
        }
    }

    override fun render(graphics2D: Graphics2D) {
        // Find the correct frame within the sprite sheet
        val targetFrame =
            spriteSheet.getSubimage(frameColumn.get() * frameSize, frameRow.get() * frameSize, frameSize, frameSize)
        graphics2D.drawImage(targetFrame, x, y, null)

        // draw "turret"
        graphics2D.color = Color.BLACK
        val deltaX = sin(turretOrientation) * turretLength
        val deltaY = cos(turretOrientation) * turretLength
        graphics2D.drawLine(x + (frameSize / 2), y + (frameSize / 2), x + deltaX.toInt(), y + deltaY.toInt())

        projectiles.forEach {
            it.render(graphics2D)
        }
    }
}

class Projectile(
    val genesisX: Int,
    val genesisY: Int,
    val thetaAngle: Float,
    val speedPerTick: Int,
    val image: BufferedImage
) : Renderable {

    private var x = genesisX
    private var y = genesisY

    private var deltaX = 0
    private var deltaY = 0

    var isValid: Boolean = true

    init {
        deltaX = (cos(thetaAngle.toDouble()) * speedPerTick).toInt()
        deltaY = (sin(thetaAngle.toDouble()) * speedPerTick).toInt()
    }

    override fun update() {
        x += deltaX
        y += deltaY
    }

    /**
     * Updates the projectiles validity.
     * Returns true if the projectile is still valid, false otherwise
     */
    fun checkValidity(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        isValid = (x > minX) && (x < maxX) && (y > minY) && (y < maxY)
        return isValid
    }

    override fun render(graphics2D: Graphics2D) {
        graphics2D.drawImage(image, x - (image.width / 2), y - (image.width / 2), null)
    }

}