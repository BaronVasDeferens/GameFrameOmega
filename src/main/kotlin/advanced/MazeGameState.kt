package advanced

data class MazeGameState(
    val turnNumber: Int = 1,
    val phase: MazeGamePhase = MazeGamePhase.PLAYER_MOVING,
    val mazeGrid: MazeGrid,
    val entityBasic: EntityBasic = EntityBasic(),
    val enemies: List<EntityBasic> = listOf(),
    val gameEvents: Map<MazeRoom, List<GameEvent>> = mapOf()
) {


    fun updatePlayerPosition(newRoom: MazeRoom): MazeGameState {
        val updatedPlayer = entityBasic.updatePosition(newRoom)

        val events = gameEvents[newRoom] ?: listOf()

        var newState: MazeGameState = this.copy()
        val updatedEvents = mutableListOf<GameEvent>()

        events
            .sortedBy { it.priority }
            .forEach { event ->
                if (event.isActive) {
                    newState = event.triggerEvent(newState)

                    if (!event.expires) {
                        updatedEvents.add(event)
                    }
                }
            }

        return newState.copy(
            turnNumber = turnNumber + 1,
            entityBasic = updatedPlayer,
            gameEvents = gameEvents.plus(newRoom to updatedEvents)
        )
    }

}