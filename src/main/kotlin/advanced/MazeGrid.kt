package advanced

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite

data class MazeRoom(val x: Int, val y: Int) {

    var isPassable: Boolean = false

    val color: Color
        get() = if (isPassable) Color.DARK_GRAY else {
            Color.BLACK
        }
}


class MazeGrid(private val rows: Int, private val cols: Int) {

    private val mazeRooms = mutableListOf<MazeRoom>()

    init {

        // Create rooms
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                mazeRooms.add(MazeRoom(i, j))
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

    fun getSubsection(startX: Int = 0, startY: Int = 0, size: Int = 20): Set<MazeRoom> {
        return getRooms().filter { (it.x >= startX) && (it.x <= startX + size) && (it.y >= startY) && (it.y <= startY + size)}.toSet()
    }

    fun renderMaze(width: Int, height: Int, startX: Int = rows, startY: Int = cols, roomSize: Int): Sprite {
        val mazeBackgroundImage = Pixmap(width, height, Pixmap.Format.RGBA4444)

        // Clear background
        mazeBackgroundImage.setColor(Color.BLACK)
        mazeBackgroundImage.drawRectangle(0,0, width, height)

        // Draw the master background
        getSubsection().forEach { room ->
            mazeBackgroundImage.setColor(room.color)
            mazeBackgroundImage.fillRectangle(room.x * roomSize, room.y * roomSize, roomSize, roomSize)
        }

        return Sprite(Texture(mazeBackgroundImage))
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
        return adjacentRooms
    }

}