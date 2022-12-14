package advanced


enum class GameEventType {
    FLAVOR_TEXT
}

data class GameEvent(
    val type: GameEventType,
    val priority: Int,
    val expires: Boolean,
    val isActive: Boolean = true,
    private val eventFunction: () -> Unit
) {


    fun triggerEvent(): GameEvent {

        return if (isActive) {
            eventFunction.invoke()
            if (expires) {
                this.copy(isActive = false)
            } else {
                this.copy()
            }
        } else {
            this
        }
    }
}