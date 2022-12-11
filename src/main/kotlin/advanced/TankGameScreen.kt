package advanced
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.ScreenUtils

class TankGameScreen(val drop: Drop) : Screen {

    private val tankGameStateManager = TankGameStateManager()

    private var dropImage: Texture
    private var bucketImage: Texture
//    private var dropSound: Sound
//    private var rainMusic: Music
    // The camera ensures we can render using our target resolution of 800x480
    //    pixels no matter what the screen resolution is.
    private var camera: OrthographicCamera
    private var bucket: Rectangle
    private var touchPos: Vector3
//    private var raindrops: Array<Rectangle> // gdx, not Kotlin Array
    private var lastDropTime: Long = 0L
    private var dropsGathered: Int = 0



    // initializer block
    init {

        Gdx.input.inputProcessor = tankGameStateManager

        // load the images for the droplet & bucket, 64x64 pixels each
        dropImage = Texture(Gdx.files.internal("fire64.png"))
        bucketImage = Texture(Gdx.files.internal("floorGreyTest.png"))

        // load the drop sound effect and the rain background music
//        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))
//        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))
//        rainMusic.setLooping(true)

        // create the camera and the SpriteBatch
        camera = OrthographicCamera()
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())

        // create a Rectangle to logically represent the bucket
        bucket = Rectangle()
        bucket.x = 800f/2f - 64f/2f  // center the bucket horizontally
        bucket.y = 20f               // bottom left bucket corner is 20px above
        //    bottom screen edge
        bucket.width = 64f
        bucket.height = 64f

        // create the touchPos to store mouse click position
        touchPos = Vector3()

        // create the raindrops array and spawn the first raindrop
//        raindrops = Array<Rectangle>()
//        spawnRaindrop()
    }

//    private fun spawnRaindrop() {
//        var raindrop = Rectangle()
//        raindrop.x = MathUtils.random(0f, 800f-64f)
//        raindrop.y = 480f
//        raindrop.width = 64f
//        raindrop.height = 64f
//        raindrops.add(raindrop)
//        lastDropTime = TimeUtils.nanoTime()
//    }

    override fun render(delta: Float) {

        val state = tankGameStateManager.tankGameStateFlow.value

        if (state.gamePhase == GamePhase.TEARDOWN) {
            tankGameStateManager.destroy()
            dispose()
        }

        // clear the screen with a dark blue color. The arguments to clear
        //    are the RGB and alpha component in the range [0,1] of the color to
        //    be used to clear the screen.
        ScreenUtils.clear(1.0f, 1.0f, 1.0f, 1.0f)

        // generally good practice to update the camera's matrices once per frame
        camera.update()

        // tell the SpriteBatch to render in the coordinate system specified by the
        //    camera.
        drop.batch.setProjectionMatrix(camera.combined)

        // begin a new batch and draw the bucket and all drops
        drop.batch.begin()
        drop.font.draw(drop.batch, "${state.mouseX},${state.mouseY}", 0f, 480f)

//        drop.batch.draw(bucketImage, bucket.x, bucket.y,
//            bucket.width, bucket.height)
//        for (raindrop in raindrops) {
//            drop.batch.draw(dropImage, raindrop.x, raindrop.y)
//        }

        state.entities.forEach { entity ->
            //drop.batch.draw(entity.sprite, entity.x.toFloat(), entity.y.toFloat())
            entity.tankSprite.draw(drop.batch)
        }

        //drop.batch.draw(state.tankPlayer.sprite, state.tankPlayer.x.toFloat(), state.tankPlayer.y.toFloat())

        with (state.tankPlayer as Tank) {
            tankSprite.draw(drop.batch)
            turretSprite.draw(drop.batch)
        }


        drop.batch.end()

        // process user input
//        if (Gdx.input.isTouched()) {
//            touchPos.set(Gdx.input.getX().toFloat(),
//                Gdx.input.getY().toFloat(),
//                0f)
//            camera.unproject(touchPos)
//            bucket.x = touchPos.x - 64f/2f
//        }
//        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
//            // getDeltaTime returns the time passed between the last and the current
//            //    frame in seconds
//            bucket.x -= 200 * Gdx.graphics.getDeltaTime()
//        }
//        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
//            bucket.x += 200 * Gdx.graphics.getDeltaTime()
//        }

        // make sure the bucket stays within the screen bounds
//        if (bucket.x < 0f)
//            bucket.x = 0f
//        if (bucket.x > 800f-64f)
//            bucket.x = 800f-64f

        // check if we need to create a new raindrop
//        if (TimeUtils.nanoTime() - lastDropTime > 1_000_000L)
//            spawnRaindrop()

        // move the raindrops, remove any that are beneath the bottom edge of the
        //    screen or that hit the bucket.  In the latter case, play back a sound
        //    effect also
//        var iter = raindrops.iterator()
//        while (iter.hasNext()) {
//            var raindrop = iter.next()
//            raindrop.y -= 300 * Gdx.graphics.getDeltaTime()
//            if (raindrop.y + 64 < 0)
//                iter.remove()
//
//            if (raindrop.overlaps(bucket)) {
//                dropsGathered++
//                //dropSound.play()
//                iter.remove()
//            }
//        }
    }

    // the following overrides are no-ops, unused in tutorial, but needed in
    //    order to compile a class that implements Screen
    override fun resize(width: Int, height: Int) { }
    override fun hide() { }
    override fun pause() { }
    override fun resume() { }

    override fun show() {
        // start the playback of the background music when the screen is shown
        //rainMusic.play()
    }

    override fun dispose() {
        dropImage.dispose()
        bucketImage.dispose()
        //dropSound.dispose()
        //rainMusic.dispose()
    }
}