package advanced

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import java.lang.Math.*
import kotlin.random.Random

interface Entity {
    val sprite: Sprite
    val x: Float
    val y: Float
    val orientation: Float

    fun update(): Entity {
        return this
    }

    fun updatePositionByDelta(deltaX: Float, deltaY: Float): Entity

    fun updateOrientationByDelta(delta: Float): Entity

    fun render() {}
}

data class Robot(
    val texture: Texture,
    override val x: Float,
    override val y: Float,
    override val orientation: Float = 0.0f
) : Entity {

    override val sprite: Sprite = Sprite(texture, texture.width, texture.height)

    init {
        sprite.setPosition(x.toFloat(), y.toFloat())
        sprite.rotation = orientation
    }


    override fun update(): Entity {
        return this.copy(orientation = orientation + 1.0f)
    }

    override fun updatePositionByDelta(deltaX: Float, deltaY: Float): Entity {
        return this.copy(x = x + deltaX, y = y + deltaY)
    }

    override fun updateOrientationByDelta(delta: Float): Entity {
        return this.copy(orientation = (orientation + delta) % 360)
    }

}

data class Tank(
    val texture: Texture,
    override val x: Float,
    override val y: Float,
    override val orientation: Float = 0.0f
) :
    Entity {

    override val sprite: Sprite = Sprite(texture, texture.width, texture.height)
    private val rotationSpeed = 0.5f
    private val movementSpeed = 2

    init {
//        sprite.setPosition(x.toFloat(), y.toFloat())
//        sprite.rotation = orientation
//
//        println(orientation)
    }

    fun processKeyboardInput(input: Set<KeyboardInput>): Tank {
        return if (input.containsAll(listOf(KeyboardInput.LEFT_TREAD_FWD, KeyboardInput.RIGHT_TREAD_FWD))) {
//            println("DOUBLE FORWARD $orientation")
            println(
                """ ori: $orientation dX: ${(kotlin.math.cos(orientation.toDouble()) * Math.PI / 180 * -movementSpeed).toInt()} "dY: ${
                    kotlin.math.sin(
                        orientation.toDouble()
                    ) * Math.PI / 180 * -movementSpeed
                }"""
            )
            updatePositionByDelta(
                (kotlin.math.cos(orientation.toDouble() * Math.PI / 180) * movementSpeed).toFloat(),
                (kotlin.math.sin(orientation.toDouble() * Math.PI / 180) * movementSpeed).toFloat()
            ) as Tank
        } else if (input.containsAll(listOf(KeyboardInput.LEFT_TREAD_BACK, KeyboardInput.RIGHT_TREAD_BACK))) {
//            println("DOUBLE BACKWARD $orientation")
            updatePositionByDelta(
                (kotlin.math.cos(orientation.toDouble() * Math.PI / 180) * -movementSpeed).toFloat(),
                (kotlin.math.sin(orientation.toDouble() * Math.PI / 180) * -movementSpeed).toFloat()
            ) as Tank
        } else {

            var returnReference = this as Tank

            if (input.contains(KeyboardInput.RIGHT_TREAD_FWD)) {
                returnReference = returnReference.updateOrientationByDelta(rotationSpeed) as Tank
            }

            if (input.contains(KeyboardInput.LEFT_TREAD_BACK)) {
                returnReference = returnReference.updateOrientationByDelta(rotationSpeed) as Tank
            }

            if (input.contains(KeyboardInput.RIGHT_TREAD_BACK)) {
                returnReference = returnReference.updateOrientationByDelta(-rotationSpeed) as Tank
            }

            if (input.contains(KeyboardInput.LEFT_TREAD_FWD)) {
                returnReference = returnReference.updateOrientationByDelta(-rotationSpeed) as Tank
            }

            returnReference
        }
    }

    override fun update(): Entity {
        sprite.rotation = orientation
        sprite.setPosition(x, y)
        return this
    }

    override fun updatePositionByDelta(deltaX: Float, deltaY: Float): Entity {
        //sprite.setPosition(deltaX + x.toFloat(), deltaY + y.toFloat())
        return this.copy(x = x + deltaX, y = y + deltaY)
    }

    fun moveForward(): Tank {
        return this
    }

    override fun updateOrientationByDelta(delta: Float): Entity {
        //sprite.rotation = kotlin.math.abs(orientation + delta) % 360
        return this.copy(orientation = kotlin.math.abs(360 + orientation + delta) % 360)
    }


}