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
    override val movementPerUpdate: Int,
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
    private val turretLength = 40
    private var opposite: Double = 0.0
    private var adjacent: Double = 0.0

    private var windowPosX: Int = x
    private var windowPosY: Int = y

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

        mouseState.mouseEvent?.apply {

            currentMouseX = this.x
            currentMouseY = this.y

            opposite = (windowPosY - currentMouseY).toDouble()
            adjacent = (windowPosX - currentMouseX).toDouble()

            // Quadrants I & II
            if (currentMouseX > windowPosX) {

                turretOrientation = if (currentMouseY < windowPosY) {
//                    println("Q2")
                    atan(opposite / adjacent)
                } else {
//                    println("Q3")
                    atan(opposite / adjacent)
                }
            } else {
                turretOrientation = if (currentMouseY < windowPosY) {
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

    override fun setWindowPosition(posX: Int, posY: Int) {
        windowPosX = posX
        windowPosY = posY
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
        val deltaX = (cos(turretOrientation) * turretLength).toInt()
        val deltaY = (sin(turretOrientation) * turretLength).toInt()
        graphics2D.drawLine(x + (frameSize / 2), y + (frameSize / 2), x + (frameSize / 2) + deltaX, y + (frameSize / 2) + deltaY)

        // Draw circle around tank
//        graphics2D.drawArc(x, y , frameSize, frameSize, 0, 360)

        projectiles.forEach {
            it.render(graphics2D)
        }
    }

    override fun render(graphics2D: Graphics2D, offsetX: Int, offsetY: Int) {
        TODO("Not yet implemented")
    }
}

class MazeRunner(
    override var x: Int,
    override var y: Int,
    override val movementPerUpdate: Int,
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
    private val turretLength = 40
    private var opposite: Double = 0.0
    private var adjacent: Double = 0.0

    private var windowPosX: Int = x
    private var windowPosY: Int = y

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
        }

    }

    override fun setWindowPosition(posX: Int, posY: Int) {
        windowPosX = posX
        windowPosY = posY
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

    }

    override fun render(graphics2D: Graphics2D, offsetX: Int, offsetY: Int) {
        val targetFrame =
            spriteSheet.getSubimage(frameColumn.get() * frameSize, frameRow.get() * frameSize, frameSize, frameSize)
        graphics2D.drawImage(targetFrame, x - offsetX, y - offsetY, null)    }
}