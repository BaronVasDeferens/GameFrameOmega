package advanced

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import java.io.File

class Drop(val width: Int, val height: Int) : Game() {

    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()

        // Set the silly C64 font
        val file = Gdx.files.internal("commodore.ttf")
        val generator = FreeTypeFontGenerator(file)
        val parameter = FreeTypeFontParameter()
        parameter.size = 35
        font = generator.generateFont(parameter)
        generator.dispose()

        this.setScreen(MainMenuScreen(this))
    }

    override fun render() {
        super.render()  // important!
    }

    override fun dispose() {
        // per @rohansuri's suggestion here:
        //    https://gist.github.com/sinistersnare/6367829#gistcomment-1661438
        this.getScreen().dispose()

        batch.dispose()
        font.dispose()
    }
}