package mazescroller

import Player
import Renderable
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage


data class MazeRoom(val x: Int, val y: Int) {           // TODO: rename x -> column, y -> row

    var isPassable: Boolean = false

    val color: Color
        get() = if (isPassable) Color.DARK_GRAY else {
            Color.BLACK
        }
}

class Maze(private val columns: Int,
           private val rows: Int,
           private val blockSize: Int,
           private val windowWidth: Int,
           private val windowHeight: Int) {

    private val mazeRooms = mutableListOf<MazeRoom>()

    // Tracks the position of the visible portion of the maze
    private var windowX: Int = 0
    private var windowY: Int = 0

    // The areas that, when entered by the player, trigger the scrolling movement of the sub-window
    private val leftRightZoneSize = windowWidth / 3
    private val upDownZoneSize = windowWidth / 3

    private lateinit var renderedAsBackground: BufferedImage

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

        renderedAsBackground = renderMazeToPixmap(columns * blockSize, rows * blockSize, 0, 0, rows, blockSize)
    }

    private fun getMazeSubsection(startX: Int = 0, startY: Int = 0, subsectionSize: Int): Set<MazeRoom> {
        return getRooms()
            .filter { (it.x >= startX) && (it.x <= startX + subsectionSize) && (it.y >= startY) && (it.y <= startY + subsectionSize) }
            .toSet()
    }

    private fun renderMazeToPixmap(imageWidth: Int, imageHeight: Int, startX: Int, startY: Int, subsectionSize: Int, roomSize: Int): BufferedImage {
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

    fun cropToWindow(pixelStartX: Int, pixelStartY: Int, windowWidthPixels: Int, windowHeightPixels: Int): BufferedImage {
        return renderedAsBackground.getSubimage(pixelStartX, pixelStartY, windowWidthPixels, windowHeightPixels)
    }

    private fun getRooms(): List<MazeRoom> {
        return mazeRooms.toList()
    }

    private fun getRoom(row: Int, col: Int): MazeRoom? {
        // FIXME: inefficient, unsafe! LOL
        return mazeRooms.firstOrNull { it.x == row && it.y == col }
    }

    private fun getAdjacentRooms(room: MazeRoom): List<MazeRoom> {
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

    fun moveWindow(hero: Player) {

        val widthInBlocks = columns * blockSize
        val heightInBlocks = rows * blockSize

        if (hero.isMoving.get() || hero.isCoasting.get()) {

            // RIGHT SCROLL
            val rightZoneX = windowWidth - leftRightZoneSize
            if (hero.x + hero.spriteSize >= windowX + rightZoneX) {
                if (windowX + hero.movementPerUpdate < widthInBlocks - windowWidth) {
                    windowX += hero.movementPerUpdate
                    return
                }
            }

            // LEFT SCROLL
            if (hero.x > windowX && hero.x < windowX + leftRightZoneSize) {
                if (windowX - hero.movementPerUpdate > 0) {
                    windowX -= hero.movementPerUpdate
                    return
                }
            }

            // UP SCROLL
            if (hero.y > windowY && hero.y < windowY + upDownZoneSize) {
                if (windowY - hero.movementPerUpdate > 0) {
                    windowY -= hero.movementPerUpdate
                    return
                }
            }

            // DOWN SCROLL
            val downZoneX = windowHeight - upDownZoneSize
            if (hero.y + hero.spriteSize >= windowY + downZoneX) {
                if (windowY + hero.movementPerUpdate < heightInBlocks - windowHeight) {
                    windowY += hero.movementPerUpdate
                    return
                }
            }
        }

        // Update Tank's internal notion of its position within the window
        val posX = if (hero.x > windowX) {
            hero.x - windowX
        } else {
            hero.x
        }

        val posY = if(hero.y > windowY) {
            hero.y - windowY
        } else {
            hero.y
        }

        hero.setWindowPosition(posX, posY)
    }

    fun render(entities: List<Renderable>): BufferedImage {

        // determine which squares are in the window (even partially)
        val xMin = windowX / blockSize.toFloat()
        val xMax = xMin + (windowWidth / blockSize).toFloat()
        val yMin = windowY / blockSize.toFloat()
        val yMax = yMin + (windowHeight / blockSize).toFloat()
        val roomsInWindow = mazeRooms.filter { it.x in xMin.toInt()..xMax.toInt() && it.y in yMin.toInt() .. yMax.toInt() }
        val renderedImage = BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB)

        val graphics2D = renderedImage.graphics as Graphics2D
        graphics2D.color = Color.RED
        graphics2D.fillRect(0,0,windowWidth, windowHeight)

        roomsInWindow.forEach { room ->
            graphics2D.color = room.color
            graphics2D.fillRect(((room.x - xMin) * blockSize).toInt(), ((room.y - yMin) * blockSize).toInt(), blockSize, blockSize)
        }

        entities.forEach {
            it.render(graphics2D)
        }

        graphics2D.dispose()
        return renderedImage

    }

}