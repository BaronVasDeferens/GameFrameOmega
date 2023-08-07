package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ScreenUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random
import kotlin.system.exitProcess

class TestScreen(private val drop: Drop) : Screen {

    val playerInputQueue = MutableStateFlow<Set<MovementDirection>>(setOf(MovementDirection.NOT_MOVING))


    private val camera = OrthographicCamera()
    private val keyboardInputProcessor = KeyboardInputProcessor(playerInputQueue)
    private val frameCounter = FrameCounter()
    private val renderer = Renderer(drop.width, drop.height)

    private val animationDirectives = MutableStateFlow<List<MovementDirective>>(listOf())

    private val playerEntity = GameEntity(type = GameEntityType.PLAYER)


    init {
        Gdx.input.inputProcessor = keyboardInputProcessor
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())


        playerInputQueue.onEach { inputs ->
            inputs.map { input ->
                animationDirectives.value = animationDirectives.value.plus(
                    MovementDirective(playerEntity, input, 1, 20)
                )
            }
        }.launchIn(GlobalScope)

        frameCounter.frameCountFlow.onEach { count ->
            animationDirectives.value = animationDirectives.value.filter { it.isAlive() }
        }.launchIn(GlobalScope)


    }


    override fun render(delta: Float) {
        frameCounter.update(delta)
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1.0f)
        drop.batch.projectionMatrix = camera.combined
        drop.batch.begin()

        renderer.render().draw(drop.batch)

        animationDirectives.value.forEach { directive ->
            directive.update()
            val texture = directive.getTexture()
            val coordinates = directive.getCoordinates()

            drop.batch.draw(
                texture,
                coordinates.first.toFloat(),
                coordinates.second.toFloat()
            )
        }

        drop.batch.end()
    }

    override fun show() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {

    }
}

class FrameCounter(
    private val ticksPerDivision: Int = 25,
    private val maxDivisions: Int = 4
) {

    val frameCountFlow = MutableStateFlow<Int>(0)

    private var updateCount = 0

    fun update(delta: Float) {
        updateCount++
        if (updateCount >= ticksPerDivision) {
            updateCount = 0
            frameCountFlow.value = (frameCountFlow.value + 1) % maxDivisions
        }
    }
}


class Renderer(val width: Int, val height: Int) {

    private lateinit var sprite: Sprite

    init {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA4444)
        pixmap.setColor(
            Color(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
                1.0f
            )
        )
        pixmap.fillRectangle(0, 0, width, height)
        sprite = Sprite(Texture(pixmap))
    }


    fun render(): Sprite {
        return sprite
    }
}

enum class GameEntityType {
    PLAYER,
    ENEMY
}

class GameEntity(val type: GameEntityType, var x: Int = 0, var y: Int = 0) {

    private val spriteSheet = when (type) {
        GameEntityType.PLAYER -> {
            Spritesheet("robot_basic_sprite_sheet.png", 1, 4, 64, 64)
        }

        else -> {
            Spritesheet("robot_basic_sprite_sheet.png", 1, 4, 64, 64)
        }
    }

    fun getTextureAtIndex(index: Int): TextureRegion {
        return spriteSheet.getTextureAtIndex(index)
    }

}

class Spritesheet(fileName: String, rows: Int, columns: Int, cellWidth: Int, cellHeight: Int) {

    private val textureRegions = mutableListOf<TextureRegion>()
    val indexMax get() = textureRegions.size - 1

    init {
        val masterTexture = Texture(Gdx.files.internal(fileName))

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                println("($i , $j) -> ${i * cellWidth} , ${j * cellHeight}")
                textureRegions.add(TextureRegion(masterTexture, j * cellWidth, i * cellHeight, cellWidth, cellHeight))
            }
        }
    }

    fun getTextureAtIndex(index: Int): TextureRegion {
        return textureRegions[index]
    }
}


enum class MovementDirection(val deltaX: Int, val deltaY: Int) {
    NOT_MOVING(0, 0),
    MOVING_LEFT(-1, 0),
    MOVING_RIGHT(1, 0),
    MOVING_UP(0, 1),
    MOVING_DOWN(0, -1)
}

data class MovementDirective(
    val entity: GameEntity,
    val direction: MovementDirection,
    val pixelsPerTick: Int,
    val ticksMax: Int
) {

    private var tickCurrent: Int = 0
    private var isActive = true

    fun update() {
        tickCurrent++
        if (!isAlive()) {
            if (isActive) {
                entity.x = entity.x + (ticksMax * pixelsPerTick * direction.deltaX)
                entity.y = entity.y + (ticksMax * pixelsPerTick * direction.deltaY)
            }
            isActive = false
        }
    }

    fun getTexture(): TextureRegion {
        return entity.getTextureAtIndex(0)
    }

    fun getCoordinates(): Pair<Int, Int> {
        return Pair(
            (entity.x + ((tickCurrent * pixelsPerTick) * direction.deltaX)),
            ((entity.y + (tickCurrent * pixelsPerTick)) * direction.deltaY)
        )
    }

    fun isAlive(): Boolean {
        return tickCurrent < ticksMax
    }

}

class KeyboardInputProcessor(val playerInputQueue: MutableStateFlow<Set<MovementDirection>>) : InputProcessor {
    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {

            Input.Keys.A -> {
                playerInputQueue.value = playerInputQueue.value.plus(MovementDirection.MOVING_LEFT)
            }

            Input.Keys.D -> {
                playerInputQueue.value = playerInputQueue.value.plus(MovementDirection.MOVING_RIGHT)
            }

            Input.Keys.ESCAPE -> {
                exitProcess(0)
            }
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {

            Input.Keys.A -> {
                playerInputQueue.value = playerInputQueue.value.minus(MovementDirection.MOVING_LEFT)
            }

            Input.Keys.D -> {
                playerInputQueue.value = playerInputQueue.value.minus(MovementDirection.MOVING_RIGHT)
            }

            Input.Keys.ESCAPE -> {
                exitProcess(0)
            }
        }

        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

}