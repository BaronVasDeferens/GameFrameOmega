package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils


/**
 * MERGANSER
 */
class MazeScreen(private val drop: Drop) : Screen {

    private val camera = OrthographicCamera()
    private val mazeStateManager = MazeStateManager(drop.width, drop.height, 20, 20)

    init {
        Gdx.input.inputProcessor = mazeStateManager
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())
    }

    override fun show() {

    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1.0f)
        drop.batch.setProjectionMatrix(camera.combined)
        drop.batch.begin()

        mazeStateManager.mazeRenderedSprite.value?.apply {
            draw(drop.batch)
        }

        drop.batch.end()
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


