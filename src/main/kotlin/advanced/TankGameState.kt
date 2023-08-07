package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
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

enum class KeyboardInput {
    LEFT_TREAD_FWD,
    LEFT_TREAD_BACK,
    RIGHT_TREAD_FWD,
    RIGHT_TREAD_BACK,
    TURRET_ROTATE_RIGHT,
    TURRET_ROTATE_LEFT,
    PRIMARY_FIRE,
    SECONDARY_FIRE
}


data class TankGameState(
    val gamePhase: GamePhase,
    val mouseX: Int = 0,
    val mouseY: Int = 0,
    val entities: List<Entity> = listOf(),
    val tankPlayer: Entity,
    val inputs: Set<KeyboardInput> = setOf()
) {
    fun updateState(): TankGameState {
        return this.copy(
            entities = entities.map { it.update() },
            tankPlayer = with(tankPlayer as Tank) {
                tankPlayer.processKeyboardInput(inputs).update()
            }
        )
    }
}


class TankGameStateManager(val width: Int = 1600, val height: Int = 1200) : InputProcessor {

    enum class ImageType {
        TANK_BODY,
        TANK_TURRET,
        ROBOT
    }

    private val textureMap = ImageType.values().associateWith {
        when (it) {

            ImageType.TANK_BODY -> {
                Texture(Gdx.files.internal("tank_2.png"))
            }

            ImageType.TANK_TURRET -> {
                Texture(Gdx.files.internal("turret_2.png"))
            }

            ImageType.ROBOT -> {
                Texture(Gdx.files.internal("robot_1.png"))
            }
        }
    }

    val tankGameStateFlow = MutableStateFlow(
        TankGameState(
            gamePhase = GamePhase.IN_PLAY,
            entities = (1..100)
                .map {
                    Robot(
                        textureMap[ImageType.ROBOT]!!,
                        Random.nextInt(width).toFloat(),
                        Random.nextInt(height).toFloat()
                    )
                }.toList(),
            tankPlayer = Tank(textureMap[ImageType.TANK_BODY]!!, textureMap[ImageType.TANK_TURRET]!!, 300.0f, 300.0f)
        )
    )

    private val continueRunning = AtomicBoolean(true)

    private val updateJob: ScheduledFuture<*> = Executors.newSingleThreadScheduledExecutor().schedule({
        while (continueRunning.get()) {
            val state = tankGameStateFlow.value

            if (state.gamePhase == GamePhase.TEARDOWN) {
                continueRunning.set(false)
                destroy()
            } else {
                tankGameStateFlow.value = state.updateState()
            }
            sleep(10)
        }
    }, 5, TimeUnit.MILLISECONDS)


    fun destroy() {
        updateJob.cancel(true)
        //gameStateFlow.value = GameState(GamePhase.IDLE)
    }

    override fun keyDown(keycode: Int): Boolean {
        val state = tankGameStateFlow.value

        when (keycode) {
            Input.Keys.ESCAPE -> {
                tankGameStateFlow.value = tankGameStateFlow.value.copy(
                    gamePhase = GamePhase.TEARDOWN,
                    entities = listOf()
                )
                exitProcess(0)
            }

            // Body
            Input.Keys.Q -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.LEFT_TREAD_FWD))
            }

            Input.Keys.A -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.LEFT_TREAD_BACK))
            }

            Input.Keys.E -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.RIGHT_TREAD_FWD))
            }

            Input.Keys.D -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.RIGHT_TREAD_BACK))
            }

            // Main gun
            Input.Keys.SPACE -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.PRIMARY_FIRE))
            }

            // Turret
            Input.Keys.J -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.TURRET_ROTATE_LEFT))
            }

            Input.Keys.L -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.TURRET_ROTATE_RIGHT))
            }

            Input.Keys.K -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.plus(KeyboardInput.SECONDARY_FIRE))
            }

            else -> {

            }
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {

        val state = tankGameStateFlow.value
        when (keycode) {

            // Body
            Input.Keys.Q -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.LEFT_TREAD_FWD))
            }

            Input.Keys.A -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.LEFT_TREAD_BACK))
            }

            Input.Keys.E -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.RIGHT_TREAD_FWD))
            }

            Input.Keys.D -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.RIGHT_TREAD_BACK))
            }

            // Main gun
            Input.Keys.SPACE -> {
                // Only the GameState can remove this
            }

            // Turret
            Input.Keys.J -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.TURRET_ROTATE_LEFT))
            }

            Input.Keys.L -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.TURRET_ROTATE_RIGHT))
            }

            Input.Keys.K -> {
                tankGameStateFlow.value =
                    tankGameStateFlow.value.copy(inputs = state.inputs.minus(KeyboardInput.SECONDARY_FIRE))
            }


        }
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val state = tankGameStateFlow.value
        when (state.gamePhase) {
            GamePhase.IN_PLAY -> {
                val robot = Robot(
                    Texture(Gdx.files.internal("robot_basic.png")),
                    state.mouseX.toFloat(),
                    state.mouseY.toFloat()
                )
                tankGameStateFlow.value = state.copy(entities = state.entities.plus(robot))
                println(">>> total entities: ${tankGameStateFlow.value.entities.size}")
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
        tankGameStateFlow.value = tankGameStateFlow.value.copy(mouseX = screenX, mouseY = screenY)
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }


}