package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils

class MainMenuScreen(private val drop: Drop):Screen {

    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(.9f, 0f, 0.2f, 1f);

        camera.update();
        drop.batch.projectionMatrix = camera.combined;

        drop.batch.begin();
        drop.font.draw(drop.batch, "Welcome to advanced.Drop!!! ", 100f, 150f)
        drop.font.draw(drop.batch, "Tap anywhere to begin!", 100f, 100f)
        drop.batch.end()

        if (Gdx.input.isTouched) {
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