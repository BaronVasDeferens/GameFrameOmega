package advanced

import com.badlogic.gdx.graphics.Texture
import kotlin.random.Random

abstract class Entity(open val image: Texture, open val x: Int = 0, open val y: Int = 0) {
    open fun update(): Entity {
        return this
    }

    open fun render() {}
}

data class Robot(override val image: Texture, override val x: Int, override val y: Int) : Entity(image) {
    override fun update(): Entity {
        return this.copy(
            x = x + (Random.nextInt(4) - Random.nextInt(4)),
            y = y + (Random.nextInt(4) - Random.nextInt(4))
        )
    }
}