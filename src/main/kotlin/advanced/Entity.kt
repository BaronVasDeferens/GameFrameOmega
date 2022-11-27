package advanced

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite

interface Entity {
    val tankSprite: Sprite
    val x: Float
    val y: Float
    val bodyOrientation: Float

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
    override val bodyOrientation: Float = 0.0f
) : Entity {

    override val tankSprite: Sprite = Sprite(texture, texture.width, texture.height)

    init {
        tankSprite.setPosition(x.toFloat(), y.toFloat())
        tankSprite.rotation = bodyOrientation
    }


    override fun update(): Entity {
        return this.copy(bodyOrientation = bodyOrientation + 1.0f)
    }

    override fun updatePositionByDelta(deltaX: Float, deltaY: Float): Entity {
        return this.copy(x = x + deltaX, y = y + deltaY)
    }

    override fun updateOrientationByDelta(delta: Float): Entity {
        return this.copy(bodyOrientation = (bodyOrientation + delta) % 360)
    }

}

data class Tank(
    val tankTexture: Texture,
    val turretTexture: Texture,
    override val x: Float,
    override val y: Float,
    override val bodyOrientation: Float = 0.0f,
    val turretOrientation: Float = 0.0f
) : Entity {

    override val tankSprite: Sprite = Sprite(tankTexture, tankTexture.width, tankTexture.height)
    val turretSprite: Sprite = Sprite(turretTexture, turretTexture.width, turretTexture.height)

    val turretRotationSpeed = 2.0f
    private val bodyRotationSpeed = 0.5f
    private val movementSpeed = 2


    fun processKeyboardInput(input: Set<KeyboardInput>): Tank {

        var returnReference = this

        // Body rotations
        returnReference = if (input.containsAll(listOf(KeyboardInput.LEFT_TREAD_FWD, KeyboardInput.RIGHT_TREAD_FWD))) {
            returnReference.updatePositionByDelta(
                (kotlin.math.cos(bodyOrientation.toDouble() * Math.PI / 180) * movementSpeed).toFloat(),
                (kotlin.math.sin(bodyOrientation.toDouble() * Math.PI / 180) * movementSpeed).toFloat()
            ) as Tank
        } else if (input.containsAll(listOf(KeyboardInput.LEFT_TREAD_BACK, KeyboardInput.RIGHT_TREAD_BACK))) {
            returnReference.updatePositionByDelta(
                (kotlin.math.cos(bodyOrientation.toDouble() * Math.PI / 180) * -movementSpeed).toFloat(),
                (kotlin.math.sin(bodyOrientation.toDouble() * Math.PI / 180) * -movementSpeed).toFloat()
            ) as Tank
        } else {

            if (input.contains(KeyboardInput.RIGHT_TREAD_FWD)) {
                returnReference = returnReference.updateOrientationByDelta(bodyRotationSpeed) as Tank
            }

            if (input.contains(KeyboardInput.LEFT_TREAD_BACK)) {
                returnReference = returnReference.updateOrientationByDelta(bodyRotationSpeed) as Tank
            }

            if (input.contains(KeyboardInput.RIGHT_TREAD_BACK)) {
                returnReference = returnReference.updateOrientationByDelta(-bodyRotationSpeed) as Tank
            }

            if (input.contains(KeyboardInput.LEFT_TREAD_FWD)) {
                returnReference = returnReference.updateOrientationByDelta(-bodyRotationSpeed) as Tank
            }

            returnReference
        }

        // Turret rotations
        if (input.contains(KeyboardInput.TURRET_ROTATE_LEFT)) {
            returnReference = returnReference.rotateTurretByDelta(-turretRotationSpeed) as Tank
        }

        if (input.contains(KeyboardInput.TURRET_ROTATE_RIGHT)) {
            returnReference = returnReference.rotateTurretByDelta(turretRotationSpeed) as Tank
        }

        return returnReference
    }

    override fun update(): Entity {
        tankSprite.rotation = bodyOrientation
        turretSprite.rotation = turretOrientation
        tankSprite.setPosition(x, y)
        turretSprite.setPosition(x - (turretTexture.width - tankTexture.width) / 2 , y - (turretTexture.height - tankTexture.height) / 2)
        return this
    }

    override fun updatePositionByDelta(deltaX: Float, deltaY: Float): Entity {
        //sprite.setPosition(deltaX + x.toFloat(), deltaY + y.toFloat())
//        turretSprite.setPosition(x, y + (turretTexture.height / 2))
        return this.copy(x = x + deltaX, y = y + deltaY)
    }

    fun moveForward(): Tank {
        return this
    }

    override fun updateOrientationByDelta(delta: Float): Entity {
        //sprite.rotation = kotlin.math.abs(orientation + delta) % 360
        val updatedOrientation = kotlin.math.abs(360 + bodyOrientation + delta) % 360
        return (rotateTurretByDelta(delta) as Tank).copy(bodyOrientation = updatedOrientation)

        //return this.copy(orientationBody = updatedOrientation)
    }

    fun rotateTurretByDelta(delta: Float): Entity {
        return this.copy(turretOrientation = kotlin.math.abs(360 + turretOrientation + delta) % 360)
    }

}