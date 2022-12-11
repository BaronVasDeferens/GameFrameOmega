package advanced

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.ScreenUtils
import kotlin.random.Random

class MazeScreen(private val drop: Drop) : Screen {

    private val camera = OrthographicCamera()

    private val mazeBackgroundImage = Pixmap(drop.width, drop.height, Pixmap.Format.RGBA4444)
    private var mazeBackgroundSprite: Sprite

    private val divisions = 50
    private val mazeRooms = Maze(drop.width / divisions, drop.height / divisions, divisions)

    init {
        camera.setToOrtho(false, drop.width.toFloat(), drop.height.toFloat())

        // Draw the master background
        mazeRooms.getRooms().forEach { room ->
            mazeBackgroundImage.setColor(room.color)
            mazeBackgroundImage.fillRectangle(room.x * room.size, room.y * room.size, room.size, room.size)
        }

        mazeBackgroundSprite = Sprite(Texture(mazeBackgroundImage))
    }

    override fun show() {

    }

    override fun render(delta: Float) {
        ScreenUtils.clear(1.0f, 1.0f, 1.0f, 1.0f)
        drop.batch.setProjectionMatrix(camera.combined)
        drop.batch.begin()
        with(drop.batch) {
            mazeBackgroundSprite.draw(this)
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


data class MazeRoom(val x: Int, val y: Int, val size: Int) {

    var isPassable: Boolean = false

    val color: Color
        get() = if (isPassable) Color.DARK_GRAY else {
            Color.BLACK
        }
}


class Maze(private val rows: Int, private val cols: Int, private val defaultSize: Int = 50) {

    private val mazeRooms = mutableListOf<MazeRoom>()

    init {

        // Create rooms
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                mazeRooms.add(MazeRoom(i, j, defaultSize))
            }
        }

        // Establish the border
        val mazeBorders: List<MazeRoom> =
            mazeRooms.filter { it.y == 0 || it.y == cols - 1 || it.x == 0 || it.x == rows - 1 }

        val insideMazeRooms = mazeRooms.minus(mazeBorders)

        // Create the maze
        // This algo produces "cavernous" mazes
        val inMaze = mutableSetOf<MazeRoom>()
        val reachable = mutableSetOf<MazeRoom>()
        val frontier = arrayListOf<MazeRoom>()

        val startRoom: MazeRoom = insideMazeRooms.shuffled().first() as MazeRoom
        startRoom.isPassable = true
        inMaze.add(startRoom)
        reachable.add(startRoom)

        println(startRoom)
        println(getAdjacentRooms(startRoom))

        getAdjacentRooms(startRoom).forEach { adjacent ->
            frontier.add(adjacent)
            reachable.add(adjacent)
        }

        while (reachable.size != insideMazeRooms.size) {
            frontier.shuffle()
            val newRoom = frontier.removeAt(0)

            val adjacentRooms: Set<MazeRoom> = getAdjacentRooms(newRoom).toMutableSet().minus(mazeBorders.toSet())

            if (reachable.containsAll(adjacentRooms)) {
                continue
            }

            if (!inMaze.contains(newRoom)) {
                newRoom.isPassable = true
                inMaze.add(newRoom)

                adjacentRooms.forEach {
                    if (!frontier.contains(it)) {
                        frontier.add(it)
                    }
                    reachable.add(it)
                }
            }
        }
    }


fun getRooms(): List<MazeRoom> {
    return mazeRooms.toList()
}

fun getRoom(row: Int, col: Int): MazeRoom? {
    // FIXME: inefficient, unsafe! LOL
    return mazeRooms.firstOrNull { it.x == row && it.y == col }
}

fun getAdjacentRooms(room: MazeRoom): List<MazeRoom> {
    val row = room.x
    val col = room.y

    val adjacentRooms = mutableListOf<MazeRoom>();

    adjacentRooms.addAll(
        listOfNotNull(
            getRoom(row, col - 1),
            getRoom(row, col + 1),
            getRoom(row - 1, col),
            getRoom(row + 1, col)
        )
    )

//        val up = getRoom(row, col - 1)
//        if (up != null) {
//            adjacentRooms.add(up)
//        }
//
//        val down = getRoom(row, col + 1)
//        if (down != null) {
//            adjacentRooms.add(down)
//        }
//
//        val left = getRoom(row - 1, col)
//        if (left != null) {
//            adjacentRooms.add(left);
//        }
//
//        val right = getRoom(row + 1, col)
//        if (right != null) {
//            adjacentRooms.add(right)
//        }

    return adjacentRooms
}

}