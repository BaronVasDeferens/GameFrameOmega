package advanced

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlinx.coroutines.flow.MutableStateFlow

class MazeStateManager(gameWidth: Int, gameHeight: Int, divisions: Int) : InputProcessor {

    private val mazeGrid = MazeGrid(gameWidth, gameHeight, divisions)


    val mazeStateFlow = MutableStateFlow(MazeGameState())


    fun getMazeBackground(width: Int, height: Int): Sprite {
        return mazeGrid.getMazeSprite(width, height)
    }

    /**
     * PROCESS KEY PRESSES
     */

    override fun keyDown(keycode: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

}

enum class MazeGamePhase {
    PLAYER_MOVING,
    DISPLAY_OVERLAY
}

data class MazeGameState(
    val turnNumber: Int = 1,
    val phase: MazeGamePhase = MazeGamePhase.PLAYER_MOVING,
    val playerPiece: PlayerPiece = PlayerPiece()
)

data class PlayerPiece(val x: Int = 1, val y: Int = 1)