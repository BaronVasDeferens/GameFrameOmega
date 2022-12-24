package advanced


enum class GameEventType {
    FLAVOR_TEXT
}

data class GameEvent(
    val type: GameEventType,
    val priority: Int,
    val expires: Boolean,
    val isActive: Boolean = true,
    private val eventFunction: (gameState: MazeGameState) -> MazeGameState
) {


    fun triggerEvent(gameState: MazeGameState): MazeGameState {
        return eventFunction.invoke(gameState)
    }
}