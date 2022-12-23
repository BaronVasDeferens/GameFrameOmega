package advanced

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Drop(val width: Int, val height: Int) : Game() {

    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
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