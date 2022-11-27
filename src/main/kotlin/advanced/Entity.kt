package advanced

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlin.random.Random

interface Entity {
    val sprite: Sprite
    val x: Int
    val y: Int
    val orientation: Float

    fun update(): Entity {
        return this
    }

    fun updatePositionByDelta(deltaX: Int, deltaY: Int): Entity

    fun updateOrientationByDelta(delta: Float): Entity

    fun render() {}
}

data class Robot(val texture: Texture, override val x: Int, override val y: Int, override val orientation: Float = 0.0f) : Entity {

    override val sprite: Sprite = Sprite(texture, texture.width, texture.height)

    init {
        sprite.setPosition(x.toFloat(), y.toFloat())
        sprite.rotation = orientation
    }


    override fun update(): Entity {
        return this.copy(orientation = orientation + 1.0f)
    }

    override fun updatePositionByDelta(deltaX: Int, deltaY: Int): Entity {
        return this.copy(x = x + deltaX, y = y + deltaY)
    }

    override fun updateOrientationByDelta(delta: Float): Entity {
        return this.copy(orientation = (orientation + delta) % 360)
    }

}

data class Tank(val texture: Texture, override val x: Int, override val y: Int, override val orientation: Float = 0.0f) :
    Entity {

    override val sprite: Sprite = Sprite(texture, texture.width, texture.height)
    private val rotationSpeed = 2.0f

    init {
        //sprite.setOrigin(0.0f, 0.0f)
        sprite.setPosition(x.toFloat(), y.toFloat())
        sprite.rotation = orientation
        println(orientation)
    }

    override fun update(): Entity {
        sprite.rotation = orientation
        sprite.setPosition(x.toFloat(), y.toFloat())
        return this
    }

    override fun updatePositionByDelta(deltaX: Int, deltaY: Int): Entity {
        return this.copy(x = x + deltaX, y = y + deltaY)
    }

    fun moveForward(): Tank {
        return this
    }

    override fun updateOrientationByDelta(delta: Float): Entity {
        sprite.rotation = orientation
        return this.copy(orientation = (orientation + delta) % 360)
    }


}