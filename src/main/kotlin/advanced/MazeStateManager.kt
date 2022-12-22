package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import kotlin.system.exitProcess

class MazeStateManager(val imageWidth: Int, val imageHeight: Int, val rows: Int, val cols: Int) : InputProcessor {

    private var gridSquaresPerScreen = 8         // The number of maze rooms to show on each axis
    private var gridWindowX = 0
    private var gridWindowY = 0

    private var roomSize: Int = 100 // listOf((imageWidth / gridSquaresPerScreen), (imageHeight / gridSquaresPerScreen)).minOf { it }


    val mazeStateFlow = MutableStateFlow(MazeGameState(mazeGrid = MazeGrid(rows, cols)))

    // Renderables
    val mazeRenderedSprite = MutableStateFlow<Sprite?>(null)
    val playerSprite = Pixmap(Gdx.files.internal("wanderer.png"))


    /**
     *
     *
     * Flavor text should lean into the hopelessness of leaving by any means than discovering the time and place
     * of the next exit.
     *
     */
    init {
        reinitializeMazeGameState()
    }

    private fun reinitializeMazeGameState() {

        println("------------------- INITIALIZING -------------------")

        mazeStateFlow.value = MazeGameState(mazeGrid = MazeGrid(rows, cols))

        // Place player in viable space
        val viableStartingLocations: List<MazeRoom> =
            mazeStateFlow.value.mazeGrid.getRooms().filter { it.isPassable }.sortedBy { it.x + it.y }
        val playerStartingRoom = viableStartingLocations.first()

        // TODO: task loading audio files to an audio asset manager
        val beep: Clip = AudioSystem.getClip()
        val beepFile = File("src\\main\\resources\\beep_3a.wav")
        /* PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian */
        val ais: AudioInputStream = AudioSystem.getAudioInputStream(beepFile)
        beep.open(ais)


        /* DUMB EVENTS */

        val doorClosesIntroEvent = GameEvent(GameEventType.FLAVOR_TEXT, 1, true) {
            println(
                """The exit closes noiselessly.  
                |The walls of the complex, featureless and black, soar impossibly into the darkness. 
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

        val wideOpenSpaceEvent = GameEvent(GameEventType.FLAVOR_TEXT, 1, false) {
            beep.stop()
            beep.framePosition = 0
            beep.start()
            println("""A data terminal lies in pieces here, utterly smashed beyond repair.""")
        }

        val gapEvent = GameEvent(GameEventType.FLAVOR_TEXT, 1, false) {
            beep.stop()
            beep.framePosition = 0
            beep.start()
            println("""This passage is marked as gang territory.""")
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

                // Put the "wide open" event in a wide-open space
                viableStartingLocations.filter { room -> mazeStateFlow.value.mazeGrid.getAdjacentRooms(room).all { it.isPassable }}.random() to listOf(wideOpenSpaceEvent),

                // Put the passage event in a gap
                viableStartingLocations.filter{ room -> mazeStateFlow.value.mazeGrid.getAdjacentRooms(room).filter { it.isPassable }.size == 2}.random() to listOf(gapEvent),

                // Put the president in a cul-de-sac
                viableStartingLocations.filter { room ->
                    val adjacentRooms = mazeStateFlow.value.mazeGrid.getAdjacentRooms(room)
                    adjacentRooms.size == 4 && adjacentRooms.filterNot { it.isPassable }.size == 3
                }.maxByOrNull { it.x + it.y }!! to listOf(presidentFoundEvent)
            )
        )

        mazeStateFlow.value = mazeStateFlow.value.updatePlayerPosition(playerStartingRoom)
        mazeRenderedSprite.value = renderMazeSprite()
    }


    private fun renderMazeSprite(): Sprite {
        val current = mazeStateFlow.value

        // Compute the sub-window
        recomputeMazeWindowCoordinates()

        // Render the background
        val bg = current.mazeGrid.renderMazeToPixmap(imageWidth, imageHeight, gridWindowX, gridWindowY, gridSquaresPerScreen, roomSize)

        // Render the events as dots on the map
        current.gameEvents.entries.filter{ it.value.isNotEmpty() }.forEach {
            bg.setColor(Color.LIGHT_GRAY)
            bg.fillCircle(((it.key.x - gridWindowX) * roomSize) + roomSize / 2, ((it.key.y - gridWindowY) * roomSize) + roomSize / 2, roomSize / 4)
        }

//        bg.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.50f)
//        bg.blending = Pixmap.Blending.SourceOver
//        bg.fillCircle(imageWidth/ 2, imageHeight / 2, 100)

        // Render the player sprite
        bg.drawPixmap(playerSprite,
            (current.playerPiece.x - gridWindowX) * roomSize + (roomSize / 2) - (playerSprite.width / 2),
            (current.playerPiece.y - gridWindowY) * roomSize + (roomSize / 2) - (playerSprite.height / 2))


        return Sprite(Texture(bg))
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
                mazeStateFlow.value.mazeGrid.getRoom(current.playerPiece.x, current.playerPiece.y - 1)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // Move DOWN
            Keys.DOWN,
            Keys.S -> {
                mazeStateFlow.value.mazeGrid.getRoom(current.playerPiece.x, current.playerPiece.y + 1)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // Move LEFT
            Keys.LEFT,
            Keys.A -> {
                mazeStateFlow.value.mazeGrid.getRoom(current.playerPiece.x - 1, current.playerPiece.y)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // Move RIGHT
            Keys.RIGHT,
            Keys.D -> {
                mazeStateFlow.value.mazeGrid.getRoom(current.playerPiece.x + 1, current.playerPiece.y)?.takeIf {
                    it.isPassable
                }?.apply {
                    mazeStateFlow.value = current.updatePlayerPosition(this)
                }
            }

            // DEBUG: create a new maze
            Keys.R -> {
                reinitializeMazeGameState()
            }

            // DEBUG: Increase room size
            Keys.NUMPAD_ADD,
            Keys.EQUALS -> {
                roomSize += 10
                println("INCREASING ROOM SIZE: $roomSize")
                mazeRenderedSprite.value = renderMazeSprite()
            }

            // DEBUG: Decrease room size
            Keys.NUMPAD_SUBTRACT,
            Keys.MINUS -> {
                roomSize -= 10
                println("DECREASING ROOM SIZE: $roomSize")
                mazeRenderedSprite.value = renderMazeSprite()
            }

            // QUIT
            Keys.ESCAPE -> {
                exitProcess(0)
            }

            else -> {
                println(keycode)
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


    private fun recomputeMazeWindowCoordinates() {

        val current = mazeStateFlow.value
        val pX = current.playerPiece.x
        val pY = current.playerPiece.y

        gridWindowX = pX - (gridSquaresPerScreen / 2 - 1)
        if (gridWindowX < 0) {
            gridWindowX = 0
        } else if (gridWindowX >= cols - gridSquaresPerScreen) {
            gridWindowX = cols - gridSquaresPerScreen
        }

        gridWindowY = pY - (gridSquaresPerScreen / 2 - 1)
        if (gridWindowY < 0) {
            gridWindowY = 0
        } else if (gridWindowY >= rows - gridSquaresPerScreen) {
            gridWindowY = rows - gridSquaresPerScreen
        }

    }

}

enum class MazeGamePhase {
    PLAYER_MOVING,
    DISPLAY_OVERLAY
}

data class MazeGameState(
    val turnNumber: Int = 1,
    val phase: MazeGamePhase = MazeGamePhase.PLAYER_MOVING,
    val mazeGrid: MazeGrid,
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