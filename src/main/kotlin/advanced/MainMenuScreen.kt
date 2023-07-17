package advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils

class MainMenuScreen(private val drop: Drop):Screen {

    private var camera: OrthographicCamera = OrthographicCamera()

    private val logoSprite = Texture(Gdx.files.internal("wanderer_logo.png"))

    init {
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);

        camera.update();
        drop.batch.projectionMatrix = camera.combined;

        drop.batch.begin()
        drop.batch.draw(logoSprite, 0.0f , 0.0f, drop.width.toFloat(), drop.height.toFloat())
        drop.font.draw(drop.batch, "PRESS A FOR TANKS", 0f, drop.height.toFloat() * (1.0f/5.0f))
        drop.font.draw(drop.batch, "PRESS B FOR WANDERER", 0f , drop.height.toFloat() * (1.0f/5.0f) + 35)

        drop.batch.end()



        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            //drop.screen = MazeScreen(drop)
            drop.screen = TankGameScreen(drop)
            dispose()
        } else if (Gdx.input.isKeyPressed(Input.Keys.B)) {
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