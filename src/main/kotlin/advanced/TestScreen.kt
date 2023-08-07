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
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.utils.ScreenUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.lwjgl.glfw.GLFW
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.exitProcess

class TestScreen(private val drop: Drop) : Screen {

    private val camera = OrthographicCamera()
    private val keyboardInputProcessor = KeyboardInputProcessor()
    private val frameCounter = FrameCounter()
    private val renderer = Renderer(drop.width, drop.height)

    private val spriteSheet = Spritesheet("robot_basic_sprite_sheet.png", 1, 4, 64, 64)


    init {
        Gdx.input.inputProcessor = keyboardInputProcessor
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())

//        frameCounter.frameCountFlow.onEach {
//            println(it)
//        }.launchIn(GlobalScope)

    }


    override fun render(delta: Float) {
        frameCounter.update(delta)
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1.0f)
        drop.batch.projectionMatrix = camera.combined
        drop.batch.begin()

        renderer.render().draw(drop.batch)
        drop.batch.draw(spriteSheet.getTextureAtIndex(frameCounter.frameCountFlow.value), 0.0f, 0.0f)
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


class KeyboardInputProcessor() : InputProcessor {
    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {

            Input.Keys.A -> {

            }

            Input.Keys.D -> {

            }

            Input.Keys.ESCAPE -> {
                exitProcess(0)
            }
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
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