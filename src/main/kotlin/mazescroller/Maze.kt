package mazescroller

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage


data class MazeRoom(val x: Int, val y: Int) {

    var isPassable: Boolean = false

    val color: Color
        get() = if (isPassable) Color.DARK_GRAY else {
            Color.BLACK
        }
}

class Maze(val columns: Int, val rows: Int) {

    private val mazeRooms = mutableListOf<MazeRoom>()

    init {

        // Create rooms
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                mazeRooms.add(MazeRoom(i, j))
            }
        }

        // Establish the border
        val mazeBorders: List<MazeRoom> =
            mazeRooms.filter { it.y == 0 || it.y == columns - 1 || it.x == 0 || it.x == rows - 1 }

        val insideMazeRooms = mazeRooms.minus(mazeBorders)

        // Create the maze
        // This algo produces "cavernous" mazes
        val inMaze = mutableSetOf<MazeRoom>()
        val reachable = mutableSetOf<MazeRoom>()
        val frontier = arrayListOf<MazeRoom>()

        val startRoom: MazeRoom = insideMazeRooms.shuffled().first()
        startRoom.isPassable = true
        inMaze.add(startRoom)
        reachable.add(startRoom)

        getAdjacentRooms(startRoom).forEach { adjacent ->
            frontier.add(adjacent)
            reachable.add(adjacent)
        }

        while (reachable.size != insideMazeRooms.size) {
            frontier.shuffle()
            val newRoom =
                try {
                    frontier.removeAt(0)
                } catch(e: Exception) {
                    continue
                }

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

    fun getMazeSubsection(startX: Int = 0, startY: Int = 0, subsectionSize: Int): Set<MazeRoom> {
        return getRooms()
            .filter { (it.x >= startX) && (it.x <= startX + subsectionSize) && (it.y >= startY) && (it.y <= startY + subsectionSize) }
            .toSet()
    }

    fun renderMazeToPixmap(imageWidth: Int, imageHeight: Int, startX: Int, startY: Int, subsectionSize: Int, roomSize: Int): BufferedImage {
        val mazeBackgroundImage = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics2D = mazeBackgroundImage.graphics as Graphics2D

        // Clear background
        graphics2D.color = Color.RED
        graphics2D.drawRect(0, 0, imageWidth, imageHeight)

        // Draw the master background
        getMazeSubsection(startX = startX, startY= startY, subsectionSize = subsectionSize).forEach { room ->
            graphics2D.color = room.color
            graphics2D.fillRect((room.x - startX) * roomSize, (room.y - startY) * roomSize, roomSize, roomSize)
        }

        return mazeBackgroundImage
    }

    fun cropToWindow(image: BufferedImage, windowsStartX: Int, windowStartY: Int, windowWidth: Int, windowHeight: Int): BufferedImage {
        return image.getSubimage(windowsStartX, windowStartY, windowWidth, windowHeight)
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