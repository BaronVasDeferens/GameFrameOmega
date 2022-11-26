package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import javafx.scene.input.KeyCode
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Thread.sleep
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

enum class GamePhase {
    IDLE,
    IN_PLAY,
    PAUSED
}

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

data class GameState(
    val gamePhase: GamePhase,
    val mouseX: Int = 0,
    val mouseY: Int = 0,
    val entities: List<Entity> = listOf()
) {
    fun updateState(): GameState {
        return this.copy(entities = entities.map { it.update() })
    }
}

class GameStateManager(val width: Int = 1600, val height: Int = 1200) : InputProcessor {

    val gameStateFlow = MutableStateFlow(GameState(gamePhase = GamePhase.IN_PLAY))

    private val updateJab: ScheduledFuture<*> = Executors.newSingleThreadScheduledExecutor().schedule({
        while (true) {
            val state = gameStateFlow.value
            gameStateFlow.value = state.updateState()
            sleep(10)
        }
    }, 10, TimeUnit.MILLISECONDS)


    fun destroy() {
        updateJab.cancel(true)
        gameStateFlow.value = GameState(GamePhase.IDLE)
    }

    override fun keyDown(keycode: Int): Boolean {
        val state = gameStateFlow.value

        when {
            (state.gamePhase == GamePhase.IDLE) && (keycode == KeyCode.ESCAPE.code) -> {

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
        val state = gameStateFlow.value
        when (state.gamePhase) {
            GamePhase.IN_PLAY -> {
                val robot = Robot(Texture(Gdx.files.internal("fire64.png")), state.mouseX, state.mouseY)
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