package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Sprite

interface RenderableEntity {

}

data class EntityBasic(
    val x: Int = 1,
    val y: Int = 1,
    ): RenderableEntity {

    fun updatePosition(room: MazeRoom): EntityBasic {
        return this.copy(x = room.x, y = room.y)
    }

}