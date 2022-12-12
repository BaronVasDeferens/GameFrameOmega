package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.system.exitProcess

class MazeStateManager(val rows: Int, val cols: Int, val divisions: Int) : InputProcessor {

    private val mazeGrid = MazeGrid(rows, cols, divisions)
    val mazeStateFlow = MutableStateFlow(MazeGameState())

    val playerSprite = Texture(Gdx.files.internal("robot_1.png"))

    init {

        println("rows: $rows cols: $cols")

        // Place player in viable space
        val current = mazeStateFlow.value
        val viableSpace: MazeRoom = mazeGrid.getRooms().filter { it.isPassable }.sortedBy { it.x + it.y }.first()
        mazeStateFlow.value = current.copy(playerPiece = current.playerPiece.copy(x = viableSpace.x, viableSpace.y))
    }


    fun getMazeBackground(width: Int, height: Int): Sprite {
        return mazeGrid.getMazeSprite(width, height)
    }

    /**
     * LibGDX defines the origin (0,0) in the LOWER left instead of the UPPER left.
     * This means that drawing must be adjusted-- sorry about the ugly math
     */
    fun getPlayerMazeDrawingCoords(): Pair<Int, Int> {
        val current = mazeStateFlow.value.playerPiece
        return Pair(
            (current.x * divisions) + ((divisions - playerSprite.width) / 2),
            ((cols - current.y) * divisions) - divisions + ((divisions - playerSprite.width) / 2)
        )
    }

    /**
     * PROCESS KEY PRESSES
     */

    override fun keyDown(keycode: Int): Boolean {
        val current = mazeStateFlow.value
        when (keycode) {
            Keys.W -> {
                // Move UP
                mazeGrid.getRoom(current.playerPiece.x, current.playerPiece.y - 1)
                    ?.takeIf {
                        it.isPassable
                    }?.apply {
                        mazeStateFlow.value = current.copy(playerPiece = current.playerPiece.updatePosition(this))
                    }
            }

            // Move DOWN
            Keys.S -> {
                mazeGrid.getRoom(current.playerPiece.x, current.playerPiece.y + 1)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.copy(playerPiece = current.playerPiece.updatePosition(this))
                }
            }

            // Move LEFT
            Keys.A -> {
                mazeGrid.getRoom(current.playerPiece.x - 1, current.playerPiece.y)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.copy(playerPiece = current.playerPiece.updatePosition(this))
                }
            }

            // Move RIGHT
            Keys.D -> {
                mazeGrid.getRoom(current.playerPiece.x + 1, current.playerPiece.y)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.copy(playerPiece = current.playerPiece.updatePosition(this))
                }
            }

            Keys.ESCAPE -> {
                exitProcess(0)
            }

            else -> {

            }
        }

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

data class PlayerPiece(val x: Int = 1, val y: Int = 1) {

    fun updatePosition(room: MazeRoom): PlayerPiece {
        return this.copy(x = room.x, y = room.y)
    }

}