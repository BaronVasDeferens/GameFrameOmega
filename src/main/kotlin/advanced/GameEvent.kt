package advanced

import com.badlogic.gdx.Gdx


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
            with(Gdx.audio.newSound(Gdx.files.internal("beep.mp3"))) {
                play(1.0f)
            }

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