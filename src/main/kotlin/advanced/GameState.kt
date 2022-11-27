package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Thread.sleep
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import kotlin.system.exitProcess


/**
 * DEHUMANIZE YOURSELF AND FACE TO BLOODSHED
 * -- "The Screamer" (PC-98, Japan)
 */

enum class GamePhase {
    IDLE,
    IN_PLAY,
    PAUSED,
    TEARDOWN
}


data class GameState(
    val gamePhase: GamePhase,
    val mouseX: Int = 0,
    val mouseY: Int = 0,
    val entities: List<Entity> = listOf(),
    val tankPlayer: Entity
) {
    fun updateState(): GameState {
        return this.copy(
            entities = entities.map { it.update() },
        tankPlayer = tankPlayer.update())
    }
}

class GameStateManager(val width: Int = 1600, val height: Int = 1200) : InputProcessor {

    enum class ImageType {
        PLAYER,
        ROBOT
    }

    private val textureMap = ImageType.values().associateWith {
        when (it) {

            ImageType.PLAYER -> {
                Texture(Gdx.files.internal("tank_1.png"))
            }

            ImageType.ROBOT -> {
                Texture(Gdx.files.internal("robot_basic.png"))
            }
        }
    }

    val gameStateFlow = MutableStateFlow(
        GameState(
            gamePhase = GamePhase.IN_PLAY,
            entities = (1..100)
                .map {
                    Robot(textureMap[ImageType.ROBOT]!!, Random.nextInt(width), Random.nextInt(height))
                }.toList(),
            tankPlayer = Tank(textureMap[ImageType.PLAYER]!!, 300, 300)
        )
    )

    private val continueRunning = AtomicBoolean(true)

    private val updateJob: ScheduledFuture<*> = Executors.newSingleThreadScheduledExecutor().schedule({
        while (continueRunning.get()) {
            val state = gameStateFlow.value

            if (state.gamePhase == GamePhase.TEARDOWN) {
                continueRunning.set(false)
                destroy()
            } else {
                gameStateFlow.value = state.updateState()
            }
            sleep(10)
        }
    }, 5, TimeUnit.MILLISECONDS)


    fun destroy() {
        updateJob.cancel(true)
        //gameStateFlow.value = GameState(GamePhase.IDLE)
    }

    override fun keyDown(keycode: Int): Boolean {
        val state = gameStateFlow.value

        println(">>> key pressed: $keycode")

        when (keycode) {
            Input.Keys.ESCAPE -> {
                gameStateFlow.value = gameStateFlow.value.copy(
                    gamePhase = GamePhase.TEARDOWN,
                    entities = listOf()
                )

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

        val state = gameStateFlow.value

        when (character) {

            'w' -> {
                gameStateFlow.value =
                    gameStateFlow.value.copy(tankPlayer = state.tankPlayer.updatePositionByDelta(deltaX = 0, deltaY = 100))
            }

            's' -> {
                gameStateFlow.value =
                    gameStateFlow.value.copy(
                        tankPlayer = state.tankPlayer.updatePositionByDelta(
                            deltaX = 0,
                            deltaY = -100
                        )
                    )
            }

            'a' -> {
                gameStateFlow.value =
                    gameStateFlow.value.copy(tankPlayer = state.tankPlayer.updateOrientationByDelta(5.0f))
            }

            'd' -> {
                gameStateFlow.value =
                    gameStateFlow.value.copy(tankPlayer = state.tankPlayer.updateOrientationByDelta(-5.0f))
            }
        }

        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val state = gameStateFlow.value
        when (state.gamePhase) {
            GamePhase.IN_PLAY -> {
                val robot = Robot(Texture(Gdx.files.internal("robot_basic.png")), state.mouseX, state.mouseY)
                gameStateFlow.value = state.copy(entities = state.entities.plus(robot))
                println(">>> total entities: ${gameStateFlow.value.entities.size}")
            }
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        gameStateFlow.value = gameStateFlow.value.copy(mouseX = screenX, mouseY = screenY)
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }


}