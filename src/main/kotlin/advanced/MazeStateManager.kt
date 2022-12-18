package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import kotlin.system.exitProcess

class MazeStateManager(val imageWidth: Int, val imageHeight: Int, val rows: Int, val cols: Int, val divisions: Int) : InputProcessor {

    private val mazeGrid = MazeGrid(rows, cols, divisions)

    val mazeStateFlow = MutableStateFlow(MazeGameState())
    val mazeRenderedSprite = MutableStateFlow<Sprite?>(null)


    val playerSprite = Texture(Gdx.files.internal("wanderer.png"))


    /**
     *
     *
     * Flavor text should lean into the hopelessness of leaving by any means than discovering the time and place
     * of the next exit.
     *
     */
    init {

        println("rows: $rows cols: $cols")

        // Place player in viable space
        val viableStartingLocations: List<MazeRoom> =
            mazeGrid.getRooms().filter { it.isPassable }.sortedBy { it.x + it.y }
        val playerStartingRoom = viableStartingLocations.first()

        // TODO: task loading audio files to an audio asset manager
        val beep: Clip = AudioSystem.getClip()
        val beepFile = File("src\\main\\resources\\beep_3a.wav")
        /* PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian */
        val ais: AudioInputStream = AudioSystem.getAudioInputStream(beepFile)
        beep.open(ais)

        val doorClosesIntroEvent = GameEvent(GameEventType.FLAVOR_TEXT, 1, true) {
            println(
                """The exit seals noiselessly and, just one moment later, is gone without as a seam. 
                |Walls of featureless black obsidian soar impossibly into the darkness. 
                |The complex is still, lit only by your lanterns.
                |""".trimMargin()
            )
        }

        val garbageEvent = GameEvent(GameEventType.FLAVOR_TEXT, 2, false) {
            beep.stop()
            beep.framePosition = 0
            beep.start()
            println(
                """This area is strewn with the debris of human lives and activity, all warn and deeply soiled.
                    |There is nothing worth scavenging.
                    |
                """.trimMargin()
            )
        }

        val somewhereEvent = GameEvent(GameEventType.FLAVOR_TEXT, 2, false) {
            beep.stop()
            beep.framePosition = 0
            beep.start()
            println("""The skeletal remains of a large animal, desiccated and ragged, lies here.""")
        }

        val presidentFoundEvent = GameEvent(GameEventType.FLAVOR_TEXT, 1, false) {
            beep.stop()
            beep.framePosition = 0
            beep.start()
            println("""YOU HAVE FOUND THE PRESIDENT!""")
        }

        mazeStateFlow.value = mazeStateFlow.value.copy(
            gameEvents = mapOf(
                playerStartingRoom to listOf(
                    doorClosesIntroEvent,
                    garbageEvent
                ),
                // Put the skeleton in some random room
                viableStartingLocations.random() to listOf(somewhereEvent),
                // Put the president in a cul-de-sac
                viableStartingLocations.filter { room ->
                    val adjacentRooms = mazeGrid.getAdjacentRooms(room)
                    adjacentRooms.size == 4 && adjacentRooms.filterNot { it.isPassable }.size == 3
                }.sortedByDescending{ it.x + it.y }.first() to listOf(presidentFoundEvent)
            )
        )

        mazeStateFlow.value = mazeStateFlow.value.updatePlayerPosition(playerStartingRoom)
        mazeRenderedSprite.value = renderMazeSprite()
    }


    fun renderMazeSprite(): Sprite {
        return mazeGrid.getMazeSprite(imageWidth, imageHeight)
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
            Keys.UP,
            Keys.W -> {
                // Move UP
                mazeGrid.getRoom(current.playerPiece.x, current.playerPiece.y - 1)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // Move DOWN
            Keys.DOWN,
            Keys.S -> {
                mazeGrid.getRoom(current.playerPiece.x, current.playerPiece.y + 1)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // Move LEFT
            Keys.LEFT,
            Keys.A -> {
                mazeGrid.getRoom(current.playerPiece.x - 1, current.playerPiece.y)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // Move RIGHT
            Keys.RIGHT,
            Keys.D -> {
                mazeGrid.getRoom(current.playerPiece.x + 1, current.playerPiece.y)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            Keys.ESCAPE -> {
                exitProcess(0)
            }

            else -> {

            }
        }

        mazeRenderedSprite.value = renderMazeSprite()
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
    val playerPiece: PlayerPiece = PlayerPiece(),
    val gameEvents: Map<MazeRoom, List<GameEvent>> = mapOf()
) {


    fun updatePlayerPosition(newRoom: MazeRoom): MazeGameState {
        val updatedPlayer = playerPiece.updatePosition(newRoom)

        val events = gameEvents[newRoom] ?: listOf()

        println("Turn:${turnNumber + 1} / Pos:(${newRoom.x},${newRoom.y})")


        val updatedEvents = events
            .sortedBy { it.priority }
            .map {
                it.triggerEvent()
            }

        return this.copy(
            turnNumber = turnNumber + 1,
            playerPiece = updatedPlayer,
            gameEvents = gameEvents.plus(newRoom to updatedEvents)
        )
    }

}

data class PlayerPiece(val x: Int = 1, val y: Int = 1) {

    fun updatePosition(room: MazeRoom): PlayerPiece {
        return this.copy(x = room.x, y = room.y)
    }

}