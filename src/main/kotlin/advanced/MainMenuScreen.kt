package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils

class MainMenuScreen(private val drop: Drop):Screen {

    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);

        camera.update();
        drop.batch.projectionMatrix = camera.combined;

        drop.batch.begin();
        drop.font.draw(drop.batch, "W A N D E R E R", 100f, 150f)
        drop.font.draw(drop.batch, "press SPACE to begin", 100f, 100f)
        drop.batch.end()

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            drop.screen = MazeScreen(drop)
            dispose()
        }
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